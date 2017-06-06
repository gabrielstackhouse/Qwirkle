package qwirkle;

import java.awt.Color;

public class Board {
	
	private int xMax = 80;
	private int yMax = 21;

	private Tile[][] board = new Tile[yMax][xMax];
	
	/** 
	 * Constructs a board of initial null values
	 */
	public Board() {
		for (int i = 0; i < xMax; i++) {
			for (int j = 0; j < yMax; j++) {
				board[j][i] = null;
			}
		}
	}
	
	/**
	 * Returns the board array
	 * @return board array
	 */
	public Tile[][] getBoard() {
		return board;
	}
	
	/**
	 * Returns tile from board without removing it
	 * @param x - x parameter
	 * @param y - y parameter
	 * @return - tile specified, null if no tile
	 */
	public Tile getTile(int x, int y) {
		if (x < 0 || x >= xMax || y < 0 || y >= xMax)
			return null;
		return board[y][x];
	}
	
	/**
	 * Removes tile from board
	 * @param x - x parameter
	 * @param y - y parameter
	 * @return - tile removed, null if no tile
	 */
	public Tile removeTile(int x, int y) {
		if (x < 0 || x >= xMax || y < 0 || y >= xMax)
			return null;
		Tile result = board[y][x];
		board[y][x] = null;
		return result;
	}
	
	public char getTileSymbol(int x, int y) {
		return board[y][x].getSymbol();
	}
	
	public Color getTileColor(int x, int y) {
		return board[y][x].getColor();
	}
	
	/**
	 * Place a tile on the board
	 * @param tile tile to be placed
	 * @param x x index
	 * @param y y index
	 */
	public void placeTile(Tile tile, int x, int y) {
		board[y][x] = tile;
	}
	
	public int getXMax() {
		return xMax;
	}
	
	public int getYMax() {
		return yMax;
	}
}
