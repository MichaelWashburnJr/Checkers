/**
 * This class represents an ordered pair. It is used for the 
 * HashMap that the board is stored in and overrides the hashcode and 
 * equals methods.
 * @author Michael Washburn <mdw@michaelwashburn.com>
 *
 */
public class Cell {
	
	private int r;
	private int c;

	public Cell(int r, int c){
		this.r = r;
		this.c = c;
	}
	
	public int getR(){
		return r;
	}
	
	public int getC(){
		return c;
	}
	
	@Override
	public int hashCode(){
		return (r * 10) + c;
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof Cell){
			Cell other = (Cell) o;
			if ((other.r == this.r) && (other.c == this.c)){
				return true;
			}
		}
		return false;
	}
	
}
