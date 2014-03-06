import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * This class makes a new window that is shown when the game is over.
 * It displays whether the user won or lost and gives them options to
 * quit or start a new game.
 * @author Michael Washburn <mdw@michaelwashburn.com>
 *
 */
public class GameOver extends JFrame{

	/*construct a new window */
	public GameOver(GUIView root, GameModel model){
		this.setSize(250, 100);
		this.setResizable(false);
		
		//scenter the window over the root window
		int x = root.getX() + root.getWidth()/2 - this.getWidth()/2;
		int y = root.getY() + root.getHeight()/2 - this.getHeight()/2;
		this.setLocation(new Point(x,y));
		
		//set the victory/defeat message
		JLabel victory = new JLabel();
		String text = "";
		if (model.hasWon() == 1){
			text = "You Won!";
		}
		else{
			text = "You Lost!";
		}
		victory.setText(text);
		victory.setHorizontalAlignment(SwingConstants.CENTER);
		victory.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		this.add(victory,BorderLayout.NORTH);
		
		//create the quit and new game buttons
		JPanel buttons = new JPanel();
		buttons.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(0, 20, 10, 20),
				BorderFactory.createEtchedBorder()));
		
		JButton newGame = new JButton("New Game");
		newGame.addActionListener(new newGame(model,this));
		buttons.add(newGame);
		
		JButton quit = new JButton("Quit");
		quit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(DISPOSE_ON_CLOSE);
			}
		});
		buttons.add(quit);
		this.add(buttons);
		this.setVisible(true);
	}
	
}

/**
 * Listener for the new game button. this will call the 
 * model to restart and dispose of this window so the player
 * can play.
 */
class newGame implements ActionListener{
	private GameModel model;
	private GameOver window;
	public newGame (GameModel model,GameOver window){
		this.model = model;
		this.window = window;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		model.reset();
		window.dispose();
		model.gameOver = false;
	}
	
}