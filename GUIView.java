/*
 * This class enables a user interface to work with the model.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;


/**
 * 
 * @author Michael Washburn <mdw@michaelwashburn.com>
 *
 */
public class GUIView extends JFrame implements Observer{
	
	/* buttons holds all the buttons that represent spaces on the board*/
	public HashMap<Cell,CButton> buttons;
	
	/* This stores the model for the current game */
	private GameModel model;
	
	/* Stores the status text for the game */
	private JLabel status;
	
	/**
	 * Constructs a new GUI for the checkers game
	 * @param model
	 */
	public GUIView(GameModel model){
		//initializing variables
		super("Checkers by Michael Washburn");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.model = model;
		model.addObserver(this);
		JPanel board = new JPanel();
		board.setLayout(new GridLayout(8,8));
		this.buttons = new HashMap<Cell,CButton>();
		//build hashmap of buttons reflecting spaces on the board
		for (int r = 0; r < 8; r++){
			for (int c = 0; c < 8; c++){
				Cell cell = new Cell(r,c);
				CButton button = new CButton(r,c,model);
				board.add(button);
				buttons.put(cell, button);
			}
		}
		this.add(board,BorderLayout.CENTER);
		
		//build the status bar to show what to do at the top of the game
		JPanel statusbar = new JPanel();
		statusbar.setPreferredSize(new Dimension(10,30));
		statusbar.setLayout(new FlowLayout(FlowLayout.LEFT));
		statusbar.setBorder(BorderFactory.createRaisedBevelBorder());
		this.status = new JLabel("Status: Select the piece you want to move");
		statusbar.add(status);
		this.add(statusbar,BorderLayout.NORTH);
		
		//Build the lower bar containing the buttons.
		JPanel lowBar = new JPanel();
		lowBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		//build an undo button
		JButton undo = new JButton("Undo");
		undo.addActionListener(new UndoListener(this.model));
		lowBar.add(undo);
		
		//build the reset button
		JButton reset = new JButton("New Game");
		reset.addActionListener(new ResetListener(this.model));
		lowBar.add(reset);
		
		//build the commit or next move button to advance to player 2's move.
		JButton commit = new JButton("Next Move");
		commit.addActionListener(new CommitListener(this.model));
		lowBar.add(commit);
		
		this.add(lowBar,BorderLayout.SOUTH);
		
		this.setSize(500, 540);
		this.setResizable(false);
		this.setVisible(true);
		model.update();
		
	}
	
	/**
	 * Returns the button at the r,c coordinate to the caller.
	 * @param r
	 * @param c
	 * @return
	 */
	public CButton getButton(int r, int c){
		return buttons.get(new Cell(r,c));
	}
	
	/**
	 * Updates the gui when the observable object notifies its observers.
	 * This will iterate through updating all the buttons, the status, and 
	 * check if the game has been won.
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		for (int r = 0; r < 8; r++){
			for (int c = 0; c < 8; c++){
				getButton(r,c).update();
			}
		}
		if ((this.model.hasWon() != 0)){
			this.setEnabled(false);
			new GameOver(this,this.model);
			this.setEnabled(true);
		}
		else if (this.model.clicked.size() == 0){
			this.status.setText("Status: Select the piece you want to move");
		}
		else{
			this.status.setText("Status: Select where to move this piece to");
		}
		
	}

	/**
	 * This will create the new instance of the game when the program is launched.
	 * @param args
	 */
	public static void main(String[] args) {
		GameModel model = new GameModel();
		new Instructions(new GUIView(model));
	}

}

/**
 * This class represents the action to be performed when the next move button
 * is pressed. It will notify the ai to make a move and then change the turn
 * back to the user.
 */
class CommitListener implements ActionListener{
	
	/* stores the model of the current game */
	private GameModel model;
	
	/**
	 * Constructs a new instance of this listener.
	 */
	public CommitListener(GameModel model){
		this.model = model;
	}
	
	/**
	 * When this button is pressed it will check to see that
	 * the player made a move, if so, it will notify the ai to make their move.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (model.jumping || model.movedOne){
			model.jumping = false;
			model.movedOne = false;
			model.nextMove();
			model.ai.move();
			model.nextMove();
		}
	}
}

/**
 * Represents the action for reseting the game
 */
class ResetListener implements ActionListener{
	
	private GameModel model;
	public ResetListener(GameModel model){
		this.model = model;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		model.reset();
	}
	
}

/**
 * Represents the action for undoing a move.
 */
class UndoListener implements ActionListener{
	private GameModel model;
	public UndoListener(GameModel model){
		this.model = model;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		model.undo();
	}
	
}
