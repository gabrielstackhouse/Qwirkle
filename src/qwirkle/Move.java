package qwirkle;

/**
 * A simple class that holds a tile and a valid location for it to be placed on the board 
 * @author Gabriel Stackhouse
 *
 */
public class Move {

	private Tile tile;
	private int tileIndex;
	private int x;
	private int y;
	private int score;
	
	/**
	 * 
	 * @param tile tile to be moved
	 * @param tileIndex index of the tile in hand
	 * @param x location to be placed on x-axis
	 * @param y location to be placed on y-axis
	 */
	public Move(Tile tile, int tileIndex, int x, int y) {
		this.tile = tile;
		this.tileIndex = tileIndex;
		this.x = x;
		this.y = y;
		score = -1;
	}
	
	public Move(Tile tile, int tileIndex, int x, int y, int score) {
		this.tile = tile;
		this.tileIndex = tileIndex;
		this.x = x;
		this.y = y;
		this.score = score;
	}
	
	public Tile getTile() {
		return tile;
	}
	
	public int getIndex() {
		return tileIndex;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
}
