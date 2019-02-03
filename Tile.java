package elsie.main;

public class Tile {
	
	public int val, x, y;
	public char c;
	public boolean hasMark;
	
	//constructor for tile object
	public Tile (int val, int x, int y, char c, boolean hasMark){
		this.val = val;
		this.x = x;
		this.y = y;
		this.c = c;
		this.hasMark = hasMark;
	}

}
