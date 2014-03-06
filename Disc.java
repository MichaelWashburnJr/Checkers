/**
 * This class represents a disc object. It stores whether or not the disc is a 
 * king and the id of the player who owns it.
 * @author Michael Washburn <mdw@michaelwashburn.com>
 *
 */
public class Disc {

	private boolean king;
	
	private int id;
	
	public Disc(int id){
		this.id = id;
		this.king = false;
	}
	
	public void kingMe(){
		this.king = true;
	}
	
	public int get(){
		return id;
	}
	
	public boolean isKing(){
		return king;
	}
	
	@Override
	public String toString(){
		return "" + id;
	}
	
}
