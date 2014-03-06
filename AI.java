import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * This button class represents a space on the game board for which players to
 * move across.
 * @author Michael Washburn <mdw@michaelwashburn.com>
 *
 */
public class AI {
	
	private GameModel model;
	
	private ArrayList<Cell> occupied;

	
	/**
	 * Construct a new instance of this class
	 * @param model
	 */
	public AI(GameModel model){
		this.occupied = new ArrayList<Cell>();
		this.model = model;
	}
	
	/**
	 * Update this objects data structures to reflect the current board
	 */
	private void update(){
		occupied = new ArrayList<Cell>();
		for (int r = 0; r < 8; r++){
			for (int c = 0; c < 8; c++){
				if ((model.get(r,c) != null) && (model.get(r,c).get() == 2)){
					occupied.add(new Cell(r,c));
				}
			}
		}
	}
	
	/**
	 * Return to the user how many pieces are in danger of being captured on
	 * the given board configuration
	 * @param config
	 * @return
	 */
	private int inDanger(GameModel config){
		int danger = 0;
		for (int r = 0; r < 8; r++){
			for (int c = 0; c < 8; c++){
				if (config.isOccupied(r, c) && (config.get(r, c).get() == 2)){
					//check sides of this square for opponents and check if they can jump you
					if ((config.isOccupied(r+1,c+1)) && (config.get(r+1, c+1).get() == 1)){
						if (((r-1 >= 0) && (r-1 < 8) && (c-1 >= 0) && (c-1 < 8))){
							if (config.get(r-1, c-1) == null){
								danger++;
							}
						}
					}
					if (config.isOccupied(r-1,c-1) && config.get(r-1, c-1).get() == 1){
						if (((r+1 >= 0) && (r+1 < 8) && (c+1 >= 0) && (c+1 < 8))){
							if (config.get(r+1, c+1) == null){
								danger++;
							}
						}
					}
					if (config.isOccupied(r+1,c-1) && config.get(r+1, c-1).get() == 1){
						if (((r-1 >= 0) && (r-1 < 8) && (c+1 >= 0) && (c+1 < 8))){
							if (config.get(r-1, c+1) == null){
								danger++;
							}
						}
					}
					if (config.isOccupied(r-1,c+1) && config.get(r-1, c+1).get() == 1){
						if (((r+1 >= 0) && (r+1 < 8) && (c-1 >= 0) && (c-1 < 8))){
							if (config.get(r+1, c-1) == null){
								danger++;
							}
						}
					}
				}
			}
		}
		return danger;
	}
	
	/**
	 * sort the configurations based on priority.
	 * Priorities highest to lowest:
	 * Boards where opponents lose pieces
	 * Boards where we lose pieces shouldn't be taken.
	 * @param configs
	 */
	public void sort(ArrayList<GameModel> configs){
		for (int i = 0; i < configs.size(); i++){
			for (int k = 0; k < configs.size(); k++){
				if (configs.get(i).p1Pieces() < configs.get(k).p1Pieces()){
					Collections.swap(configs, i, k);
				}
				else if (inDanger(configs.get(i)) < inDanger(configs.get(k))){
					Collections.swap(configs, i, k);
				}
			}
		}
	}
	
	/**
	 * make a move by finding the best possible nextMove for the current board.
	 */
	public void move(){
		update();
		HashMap<Cell,Disc> copy = model.copyBoard(model.getBoard());
		ArrayList<GameModel> neighbors = model.getNeighbors(model);
		sort(neighbors);
		if (neighbors.size() > 0){
			model.moves.addFirst(copy);
			model.setBoard(neighbors.get(0).getBoard());
		}
		
	}
		
}
