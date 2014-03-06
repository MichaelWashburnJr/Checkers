import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This button class represents a space on the game board for which players to
 * move across.
 * @author Michael Washburn <mdw@michaelwashburn.com>
 *
 */
public class CButton extends JButton{

	private GameModel model;
	private int r;
	private int c;
	
	/**
	 * Constructs a new button and the way it is displayed based on what piece,
	 * if any, is there.
	 * @param r
	 * @param c
	 * @param model
	 */
	public CButton(int r, int c, GameModel model){
		super();
		this.r = r;
		this.c = c;
		this.model = model;
		//set checkers pattern for board
		if((r + c) % 2 == 0){
			this.setBackground(Color.red);
		}
		else{
			this.setBackground(Color.DARK_GRAY);
		}
		this.setOpaque(true);
		this.addActionListener(new listener(model,r,c));
	}
	
	/**
	 * update this particular piece's view.
	 * This method is used by the GUIView update function to
	 * update when notified by the observer.
	 */
	public void update(){
		Disc disc = this.model.get(r, c);
		//if its empty
		if (disc == null){
			this.setIcon(null);
		}
		else{
			//if player 1 has a piece there
			if (disc.get() == 1){
				if (disc.isKing()){
					this.setIcon(new ImageIcon("red-king.jpg"));
				}
				else{
					this.setIcon(new ImageIcon("red.jpg"));
				}
			}
			//if player 2 has a piece there
			else{
				if (disc.isKing()){
					this.setIcon(new ImageIcon("black-king.jpg"));
				}
				else{
					this.setIcon(new ImageIcon("black.jpg"));
				}
			}
		}
	}
	
}

/**
 * This class constructs a new listener for when a button is pressed.
 */
class listener implements ActionListener{

	private GameModel model;
	private Integer r;
	private Integer c;
	
	/**
	 * Construct and store variables
	 */
	public listener(GameModel model,int r, int c){
		this.model = model;
		this.r = r;
		this.c = c;
	}
	
	/**
	 * when the button is pressed, if it is the right turn,
	 * the first time a button is clicked it will store the
	 * coordinates. Then the second time it will try to make
	 * a move with the first coordinates and the second ones.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		//if its the human turn and we havent already made a regular move
		if (model.isHumanTurn() && !model.movedOne){
			//if this is the first button clicked we should store it
			if (model.clicked.size() == 0){
				//if there is a piece here
				if (model.get(r, c) != null){
					//if that piece is player 1's
					if (model.get(r, c).get() == 1){
						//if player 1 is in a jumping sequence
						if (model.jumping && (model.jumper.equals(new Cell(r,c)))){
							model.clicked.add(r);
							model.clicked.add(c);
						}
						//if player one is making the first move of their turn (no jump sequence yet)
						else if (!model.jumping){
							model.clicked.add(r);
							model.clicked.add(c);
						}
						model.update();
					}
				}
			}
			//if there is already a button stored
			else{
				//check to see that the game isnt over
				if (model.hasWon() == 0){
					//remove the stored click
					int r1 = model.clicked.remove(0);
					int c1 = model.clicked.remove(0);
					Disc disc = model.get(r1, c1);
					//if the spot originally clicked has a piece in it
					if (disc != null){
						int move = 0;
						//if player 1 is in a jump sequence
						if (model.jumping){
							if(model.move(r1, c1, r,c) != 2){
								model.undo();
							}
						}
						//if they are entering a jump sequence (moving with a normal jump, not double or more yet)
						else if ((move = model.move(r1, c1, r, c)) == 2){
							model.jumping = true;
							model.jumper = new Cell(r,c);
						}
						//if it is a normal move (one space away)
						else if (move == 1){
							model.movedOne = true;
						}
						
					}
					model.update();
				}
			}
		}
	}
	
}
