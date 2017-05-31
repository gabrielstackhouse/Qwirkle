package qwirkle;
import java.util.ArrayList;
import java.awt.Color;
import java.util.Random;

public class Deck {
	
	private ArrayList<Tile> deck = new ArrayList<Tile>();
	private static char[] symbols = {'@', '#', '$', '%', '&', '*'};
	private static Color[] colors = 
		{Color.orange, Color.magenta, Color.yellow,
		 Color.red,    Color.green,   Color.blue
		};
	private int maxSize;
	
	/**
	 * Constructs a deck with default 108 tiles, then shuffles it
	 */
	public Deck() {
		maxSize = 108;
		
		//Construct 3 of each type of tile, then add to deck
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 6; j++) {
				for (int k = 0; k < 6; k++) {
					Tile tile = new Tile (symbols[j], colors[k]);
					deck.add(tile);
				}
			}
		}
		
		//Shuffle the deck
		shuffle();
	}
	
	public int getSize() {
		return deck.size();
	}
	
	public boolean isEmpty() {
		return deck.isEmpty();
	}
	
	public boolean isFull() {
		return deck.size() == maxSize;
	}
	
	public void addTile(Tile tile) {
		deck.add(tile);
	}
	
	/**
	 * Removes the tile on the top of the deck
	 * @return tile removed, null if deck is empty
	 */
	public Tile removeTile() {
		if (!deck.isEmpty())
			return deck.remove(0);
		else
			return null;
	}
	
	/**
	 * Gets tile at the specified index without removing it
	 * @param index index of tile
	 * @return tile, null if index doesn't exist
	 */
	public Tile getTileAtIndex(int index) {
		if (index < 0 || index >= deck.size())
			return null;
		else
			return deck.get(index);
		
	}
	
	/**
	 * Removes tile at specified index
	 * @param index index of tile
	 * @return
	 */
	public Tile removeTileAtIndex(int index) {
		if (index < 0 || index >= deck.size())
			return null;
		else
			return deck.remove(index);
	}
	
	/**
	 * Shuffles the deck by swapping each tile with one at a random index
	 */
	public void shuffle() {
		if (deck.size() > 0) {
			Random rand = new Random();
			for (int i = 0; i < deck.size(); i++) {
				int sw = rand.nextInt(deck.size());
				Tile temp = deck.get(i);
				deck.set(i, deck.get(sw));
				deck.set(sw, temp);
			}
		}
	}
	
}
