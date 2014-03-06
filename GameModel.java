import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;

/**
 * This class holds all information necessary to play a game of checkers 
 * except a UI. This is the model for the GUIView.
 * @author Michael Washburn <mdw@michaelwashburn.com>
 *
 */
public class GameModel extends Observable{
	
	private HashMap<Cell,Disc> board;
	public LinkedList<HashMap<Cell,Disc>> moves; //list of all moves made (used for undo)
	
	public ArrayList<Integer> clicked;//currently clicked buttons
	public boolean jumping;//if a player is in a jumping sequence
	public Cell jumper; //the space on the board containing the currently jumping piece (in jumping sequence)
	public boolean movedOne; //if the player has moved a piece
	public boolean gameOver;//if someone won
	
	private boolean humanTurn;
	
	private int player1Pieces;//count of pieces player has left
	private int player2Pieces;
	public AI ai;
	
	/**
	 * Construct a new instance of the game with the board completely initialized.
	 */
	public GameModel(){
		//initialize variables
		this.ai = new AI(this);
		this.humanTurn = true;
		this.player1Pieces = 12;
		this.player2Pieces = 12;
		this.jumping = false;
		this.jumper = null;
		this.movedOne = false;
		this.gameOver = true;
		clicked = new ArrayList<Integer>();
		//create board
		board = new HashMap<Cell, Disc>();
		//for each space on the board
		for (int r = 0; r < 8; r++){
			for (int c = 0; c < 8; c++){
				Cell cell = new Cell(r,c);
				Disc disc = null;
				//if this space should have a player 2 piece on it set the disc to player 2
				if (((r == 0) && (c % 2 == 1)) || ((r == 1) && (c % 2 == 0))){ 
					disc = new Disc(2);
				}
				//if it should be a player 1 piece make it so
				else if (((r == 7) && (c % 2 == 0)) || ((r == 6) && (c % 2 == 1))){
					disc = new Disc(1);
				}
				board.put(cell, disc);
			}
		}
		moves = new LinkedList<HashMap<Cell,Disc>>();
	}
	
	/**
	 * return a copy of the board passed in
	 * @param board
	 * @return
	 */
	public HashMap<Cell,Disc> copyBoard(HashMap<Cell,Disc> board){
		HashMap<Cell,Disc> boardCopy = new HashMap<Cell,Disc>();
		for (int r = 0; r < 8; r++){
			for (int c = 0; c < 8; c++){
				Disc cur = board.get(new Cell(r,c));
				Disc copy;
				if (cur == null){
					copy = null;
				}
				else{
					copy = new Disc(cur.get());
					if (cur.isKing()){
						copy.kingMe();
					}
				}
				boardCopy.put(new Cell(r,c), copy);
			}
		}
		return boardCopy;
	}
	
	/**
	 * Reset the board to its starting state.
	 * Reinitialize all variables as well.
	 */
	public void reset(){
		this.humanTurn = true;
		this.player1Pieces = 12;
		this.player2Pieces = 12;
		this.jumping = false;
		this.jumper = null;
		this.movedOne = false;
		clicked = new ArrayList<Integer>();
		board = new HashMap<Cell, Disc>();
		for (int r = 0; r < 8; r++){
			for (int c = 0; c < 8; c++){
				Cell cell = new Cell(r,c);
				Disc disc = null;
				if (((r == 0) && (c % 2 == 1)) || ((r == 1) && (c % 2 == 0))){ 
					disc = new Disc(2);
				}
				else if (((r == 7) && (c % 2 == 0)) || ((r == 6) && (c % 2 == 1))){
					disc = new Disc(1);
				}
				board.put(cell, disc);
			}
		}
		setChanged();
		notifyObservers();
	}
	
	/**
	 * update the gui
	 */
	public void update(){
		setChanged();
		notifyObservers();
	}
	
	/**
	 * return to the caller the current board configuration for the model.
	 * @return
	 */
	public HashMap<Cell,Disc> getBoard(){
		return this.board;
	}
	
	/**
	 * Construct a new instance of this model given another model.
	 * @param config
	 */
	public GameModel(GameModel config){
		this.humanTurn = config.humanTurn;
		this.player1Pieces = config.player1Pieces;
		this.player2Pieces = config.player2Pieces;
		clicked = new ArrayList<Integer>(config.clicked);
		this.board = copyBoard(config.board);
	}
	
	/**
	 * @return whether a player has won. 0 if no one won.
	 */
	public int hasWon(){
		if (player1Pieces == 0){
			return 2;
		}
		else if (player2Pieces == 0){
			return 1;
		}
		else{
			return 0;
		}
	}
	
	/**
	 * @return if it is a human turn return true
	 */
	public boolean isHumanTurn(){
		return humanTurn;
	}
	
	/**
	 * Set the turn to the next move
	 */
	public void nextMove(){
		humanTurn = !humanTurn;
	}
	
	/**
	 * Check to see that the given coordinate is in bounds
	 * @param r
	 * @param c
	 * @return
	 */
	public boolean validPoint(int r, int c){
		if ((0 <= r) && (r < 8)){
			if ((0 <= c) && (c < 8)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Undo the last move that was stored in the moves linked list
	 */
	public void undo(){
		this.movedOne = false;
		if (moves.size() > 0){
			if (moves.size() % 2 == 0){
				this.clicked.clear();
				moves.remove();
				setBoard(moves.remove());
			}
			else{
				this.clicked.clear();
				setBoard(moves.remove());
			}
		}
	}
	
	/**
	 * This move method is used by the player to move.
	 * returns 1 if it made a simple move.
	 * returns 2 if it made a jump.
	 * return 0 if nothing happened.
	 * @param r1
	 * @param c1
	 * @param r2
	 * @param c2
	 * @return
	 */
	public int move(int r1, int c1, int r2, int c2){
		int moved = 0;
		HashMap<Cell,Disc> copy = copyBoard(this.board);
		if (simpleMove(this,1,r1,c1,r2,c2)){
			moved = 1;
		}
		else if (simpleJump(this,1,r1,c1,r2,c2)){
			moved = 2;
			player2Pieces--;
		}
		if (moved != 0){
			if (r2 == 0){
				get(r2,c2).kingMe();
			}
			this.moves.addFirst(copy);
		}
		return moved;
	}
	
	/**
	 * This move method is used by the AI to move player number 2.
	 * It will return true if the move was successful. Unlinke the 
	 * move method above, this method takes in a game configuration.
	 * @param config
	 * @param r1
	 * @param c1
	 * @param r2
	 * @param c2
	 * @return
	 */
	public boolean moveAI(GameModel config,int r1, int c1, int r2, int c2){
		boolean moved = false;
		if (simpleMove(config,2,r1,c1,r2,c2)){
			moved = true;
		}
		else if (simpleJump(config,2,r1,c1,r2,c2)){
			moved = true;
			config.player1Pieces--;
		}
		if (moved){
			if (r2 == 7){
				config.get(r2,c2).kingMe();
			}
		}
		return moved;
	}
	
	/**
	 * This move method trys to move one space away from the current location.
	 * It will return true if it was successful.
	 * @param config
	 * @param id
	 * @param r1
	 * @param c1
	 * @param r2
	 * @param c2
	 * @return
	 */
	public boolean simpleMove(GameModel config, int id, int r1, int c1, int r2, int c2){
		if (config.validPoint(r1,c1) && (config.validPoint(r2,c2))){
			if (config.isOccupied(r1,c1) && (config.get(r1,c1).get() == id)){
				if ((!config.isOccupied(r2,c2)) && (Math.abs(c1 - c2) == 1)){
					if ((((id == 1) && (r2 - r1 == -1)) || ((id == 2) && (r2 - r1 == 1)))
							|| (config.get(r1,c1).isKing() && (Math.abs(r2 - r1) == 1))){
						Disc disc = config.get(r1,c1);
						config.set(r1,c1,null);
						config.set(r2,c2,disc);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * This move method will attempt to jump to another space (two spaces away). 
	 * It will return true if the jump was successful.
	 * @param config
	 * @param id
	 * @param r1
	 * @param c1
	 * @param r2
	 * @param c2
	 * @return
	 */
	public boolean simpleJump(GameModel config, int id, int r1, int c1, int r2, int c2){
		if (config.validPoint(r1,c1) && (config.validPoint(r2,c2))){
			if (config.isOccupied(r1,c1) && (config.get(r1,c1).get() == id)){
				if (!config.isOccupied(r2,c2) && (Math.abs(c1 - c2) == 2)){
					if ((((id == 1) && (r2 - r1 == -2)) || ((id == 2) && (r2 - r1 == 2)))
							|| (config.get(r1,c1).isKing() && (Math.abs(r2 - r1) == 2))) {
						int midr = (r1 + r2)/2;
						int midc = (c1 + c2)/2;
						if (config.isOccupied(midr,midc) && (config.get(midr,midc).get() != id)){
							Disc disc = config.get(r1,c1);
							config.set(r1,c1,null);
							config.set(midr,midc,null);
							config.set(r2,c2,disc);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * This method will return all possible next configuration of this particular 
	 * space if there is a piece in it.
	 * @param config
	 * @param r
	 * @param c
	 * @return an arrayList of configurations
	 */
	public ArrayList<GameModel> cellNeighbors(GameModel config, int r, int c){
		ArrayList<GameModel> neighbors = new ArrayList<GameModel>();
		if (config.isOccupied(r, c) && config.get(r, c).get() == 2){
			for (int i = 1; i <= 2; i++){
				GameModel neighbor = new GameModel(config);
				//Check if we can move to any adjacent tiles 1 or 2 spaces away at diagonals.
				//if we can add that config to the neighbors.
				if (neighbor.moveAI(neighbor, r, c, r - i, c - i)){
					neighbors.add(neighbor);
				}
				neighbor = new GameModel(config);
				if (neighbor.moveAI(neighbor, r, c, r - i, c + i)){
					neighbors.add(neighbor);
				}
				neighbor = new GameModel(config);
				if (neighbor.moveAI(neighbor, r, c, r + i, c - i)){
					neighbors.add(neighbor);
				}
				neighbor = new GameModel(config);
				if (neighbor.moveAI(neighbor, r, c, r + i, c + i)){
					neighbors.add(neighbor);
				}
			}
		}
		return neighbors;
	}
	
	/**
	 * get all possible next configurations of the current board.
	 * @param config
	 * @return
	 */
	public ArrayList<GameModel> getNeighbors(GameModel config){
		ArrayList<GameModel> neighbors = new ArrayList<GameModel>();
		for (int r = 0; r < 8; r++){
			for (int c = 0; c < 8; c++){
				neighbors.addAll(cellNeighbors(config,r,c));
			}
		}
		return neighbors;
	}
	
	/**
	 * Set the current board for this game to the board passed in.
	 * This will also adjust variables as necessary.
	 * @param board
	 */
	public void setBoard(HashMap<Cell,Disc> board){
		this.board = board;
		int p1 = 0;
		int p2 = 0;
		
		for (int r = 0; r < 8; r++){
			for (int c = 0; c < 8; c++){
				if (get(r,c) != null){
					if (get(r,c).get() == 1){
						p1++;
					}
					else{
						p2++;
					}
				}
			}
		}
		this.player1Pieces = p1;
		this.player2Pieces = p2;
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Getter for the number of pieces player 1 has left
	 * @return
	 */
	public int p1Pieces(){
		return player1Pieces;
	}
	
	/**
	 * Getter for the number of pieces player 2 has left
	 * @return
	 */
	public int p2Pieces(){
		return player2Pieces;
	}
	
	/**
	 * Setter for a space on the board.
	 * Sets this space to hold the given disc object
	 * @param r
	 * @param c
	 * @param disc
	 */
	public void set(int r, int c, Disc disc){
		Cell cell = new Cell(r,c);
		board.put(cell, disc);
	}
	
	/**
	 * Gets the disc, if any, at the given spot on the board.
	 * @param r
	 * @param c
	 * @return
	 */
	public Disc get(int r, int c){
		if ((r >= 0) && (r < 8) && (c >= 0) && (c < 8)){
			Cell cell = new Cell(r,c);
			return board.get(cell);
		}
		else{
			return null;
		}
	}
	
	/**
	 * Returns true if the given spot is occupied by a player
	 * @param r
	 * @param c
	 * @return
	 */
	public boolean isOccupied(int r, int c){
		Cell cell = new Cell(r,c);
		return (board.get(cell) != null);
	}
	
	/**
	 * Returns to the caller a string representation
	 * of the board.
	 */
	@Override
	public String toString(){
		String display = "\n";
		for (int r = 0; r < 8; r++){
			for (int c = 0; c < 8; c++){
				Disc disc = get(r,c);
				String string = "0";
				if (disc != null){
					string = disc.toString();
				}
				display += string;
			}
			display += "\n";
		}
		display += "\n";
		return display;	
	}
	
}
