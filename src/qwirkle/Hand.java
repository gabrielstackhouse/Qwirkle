package qwirkle;
import java.util.ArrayList;

public class Hand {

	private Tile[] hand = new Tile[6];
	private int score;
	private int handSize;
	
	/**
	 * Constructs hand of default 6 tiles
	 * @param deck the deck to draw the tiles from
	 */
	public Hand(Deck deck) {
		for (int i = 0; i < hand.length; i++) {
			hand[i] = deck.removeTile();
		}
		score = 0;
		handSize = hand.length;
	}
	
	/**
	 * Adds a tile from the deck
	 * @param deck the deck the tile is added from
	 * @return true if successful
	 */
	public boolean addTileFromDeck(Deck deck) {
		if (handSize == hand.length || deck.isEmpty())
			return false;
		
		for (int i = 0; i < hand.length; i++) {
			if (hand[i] == null) {
				hand[i] = deck.removeTile();
				handSize++;
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Fills hand with tiles from deck until either hand is full or deck is empty
	 * @param deck
	 */
	public void fillHand(Deck deck) {
		for (int i = 0; i < hand.length; i++) {
			if (deck.isEmpty())
				return;
			else if (hand[i] == null) {
				hand[i] = deck.removeTile();
				handSize++;
			}
		}
	}
	
	/**
	 * Places a tile of specified index in the deck
	 * @param deck the deck where the tile will be placed
	 * @param index index of the tile (must be between 0 and hand.size() - 1
	 */
	public void putTileInDeck(Deck deck, int index) {
		if (handSize > 0 && index >= 0 && index < hand.length && hand[index] != null) {
			deck.addTile(hand[index]);
			hand[index] = null;
			handSize--;
		}
	}
	
	/**
	 * Gets tile without removing it
	 * @param index index of tile
	 * @return tile at index, null if not present
	 */
	public Tile getTile(int index) {
		if (handSize > 0 && index >= 0 && index < hand.length && hand[index] != null) 
			return hand[index];
		else
			return null;	
	}

	
	/**
	 * Removes tile at index
	 * @param index
	 * @return tile removed, null if not present
	 */
	public Tile removeTile(int index) {
		if (handSize > 0 && index >= 0 && index < hand.length && hand[index] != null) {
			Tile tile = hand[index];
			hand[index] = null;
			handSize--;
			return tile;
		}
		else
			return null;
	}
	
	public int getHandSize() {
		return handSize;
	}
	
	public int getHandLength() {
		return hand.length;
	}
	
	public boolean isEmpty() {
		return handSize == 0;
	}
	
	public boolean isFull() {
		return handSize == hand.length;
	}
	
	public Tile[] getHand() {
		return hand;
	}
	
	public int getScore() {
		return score;
	}
	
	public void addToScore(int val) {
		score += val;
	}
	
	public void clearScore() {
		score = 0;
	}
	
	/**
	 * Checks if a move is valid, given a tile and a location
	 * @param x x-axis
	 * @param y y-axis
	 * @param tile tile to be placed
	 * @param board game board
	 * @return true if move is valid, false otherwise
	 */
	public boolean isValidMove(int x, int y, Tile tile, Board board, int tilesPlaced) {
		
		if (tile == null || x < 1 || x >= board.getXMax() - 1 || y < 1 || y >= board.getYMax() - 1 || board.getTile(x, y) != null)
			return false;
		
		//0 = nothing, 1 = color, 2 = symbol
		int xAxis = 0;
		int yAxis = 0;
		
		//X-axis
		if ((board.getTile(x-1, y) != null && board.getTile(x-1, y).getColor() == tile.getColor()) ||
			(board.getTile(x+1, y) != null && board.getTile(x+1, y).getColor() == tile.getColor()))
			xAxis = 1;
		else if ((board.getTile(x-1, y) != null && board.getTile(x-1, y).getSymbol() == tile.getSymbol()) ||
				 (board.getTile(x+1, y) != null && board.getTile(x+1, y).getSymbol() == tile.getSymbol()))
			xAxis = 2;
		
		//Y-axis
		if ((board.getTile(x, y-1) != null && board.getTile(x, y-1).getColor() == tile.getColor()) ||
			(board.getTile(x, y+1) != null && board.getTile(x, y+1).getColor() == tile.getColor()))
			yAxis = 1;
		else if ((board.getTile(x, y-1) != null && board.getTile(x, y-1).getSymbol() == tile.getSymbol()) ||
				 (board.getTile(x, y+1) != null && board.getTile(x, y+1).getSymbol() == tile.getSymbol()))
			yAxis = 2;
		
		//If tile is placed on its own, move invalid (unless first move)
		if (xAxis == 0 && yAxis == 0 && tilesPlaced > 0)
			return false;
		else if (xAxis == 0 && yAxis == 0 && tilesPlaced == 0)
			return true;
		
		
		//Check Right
		int i;
		for (i = 1; i <= hand.length; i++) {
			
			if (x + i >= board.getXMax() - 1 || (xAxis == 0 && board.getTile(x+i, y) == null) || board.getTile(x + i, y) == null)
				break;
			
			if (checkForMatch(x + i, y, xAxis, tile, board) == false)
				return false;
		}
		
		
		//Check Left
		for (i = 1; i <= hand.length; i++) {
			
			if (x - i < 1 || (xAxis == 0 && board.getTile(x-i, y) == null) || board.getTile(x - i, y) == null)
				break;
	
			if (checkForMatch(x - i, y, xAxis, tile, board) == false)
				return false;
		}
			
		
		//Check Up
		for (i = 1; i <= hand.length; i++) {
			
			if (y - i < 1 || (yAxis == 0 && board.getTile(x, y-i) == null) || board.getTile(x, y - i) == null)
				break;
			
			if (checkForMatch(x, y - i, yAxis, tile, board) == false)
				return false;
		}
		
		
		//Check Down
		for (i = 1; i <= hand.length; i++) {
			
			if (y + i >= board.getYMax() - 1 || (yAxis == 0 && board.getTile(x, y+i) == null) || board.getTile(x, y + i) == null)
				break;
			
			if (checkForMatch(x, y + i, yAxis, tile, board) == false)
				return false;
		}
		
		//At this point, move must be valid
		return true;
	}
	
	private boolean checkForMatch(int x, int y, int colorOrSymbol, Tile tile, Board board) {
		
		
		if (colorOrSymbol == 0)
			return false;
		else if (colorOrSymbol == 1) {
			
			if (board.getTile(x, y).getColor()  == tile.getColor() && 
				board.getTile(x, y).getSymbol() != tile.getSymbol())
				return true;
			else
				return false;
			
		}
		else {
			
			if (board.getTile(x, y).getSymbol() == tile.getSymbol() &&
				board.getTile(x, y).getColor()  != tile.getColor()) 
				return true;
			else
				return false;
		}
		
	}
	
	/**
	 * Returns an ArrayList of all move possibilities
	 * @param startIndex index of hand to start checking
	 * @param endIndex   index of hand to stop checking
	 * @param hand hand of player
	 * @param board game board
	 * @return ArrayList of moves
	 */
	public ArrayList<Move> findMoves(int startIndex, int endIndex, Board board, int tilesPlaced) {
		ArrayList<Move> moves = new ArrayList<Move>();
		
		for (int i = startIndex; i < endIndex + 1; i++) {
			
			for (int y = 1; y < board.getYMax() - 1; y++) {
				for (int x = 1; x < board.getXMax() - 1; x++) {
					
					if (isValidMove(x, y, this.getTile(i), board, tilesPlaced)) {
						int moveScore = getMoveScore(x, y, board);
						Move move = new Move(this.getTile(i), i, x, y, moveScore);
						moves.add(move);
					}
				}
			}
		}
		
		return moves;
	}
	
	/**
	 * Returns the score of the move (assume move is valid)
	 * @param x x position of where the tile is to be placed
	 * @param y y position of where the tile is to be placed
	 * @param tile the tile to be placed
	 * @param board the game board
	 * @return score of move, 0 if invalid
	 */
	public int getMoveScore(int x, int y, Board board) {
		int score = 0;
		
		//Check Left
		int i;
		int line = 0;
		for (i = 1; i < 6; i++) {
			if (x - i < 0 || board.getTile(x-i, y) == null)
				break;
			line++;
		}
		
		//Check Right
		for (i = 1; i < 6; i++) {
			if (x + i >= board.getXMax() || board.getTile(x+i, y) == null)
				break;
			line++;
		}
		score += calculateLine(line);
		
		//Check Up
		line = 0;
		for (i = 1; i < 6; i++) {
			if (y - i < 0 || board.getTile(x, y-i) == null)
				break;
			line++;
		}
		
		//Check Down
		for (i = 1; i < 6; i++) {
			if (y + i >= board.getYMax() || board.getTile(x, y+i) == null)
				break;
			line++;
		}
		score += calculateLine(line);
		
		//TODO --take multiple tiles per move into account
		
		if (score == 0)
			score += 1;

		return score;
	}
	
	private int calculateLine(int line) {
		int lineScore = line;
		
		if (lineScore == 0)
			return 0;
		else
			lineScore += 1;
		
		if (lineScore == 6)
			lineScore += 6;
		
		return lineScore;
	}
	
	/**
	 * Returns first move detected, regardless of score.  Null if none found.
	 * @param board
	 * @param tilesPlaced
	 * @return move, null if no possible moves
	 */
	public Move aiEasy(Board board, int tilesPlaced) {
		
		for (int i = 0; i < hand.length; i++) {
			for (int y = 1; y < board.getYMax() - 1; y++) {
				for (int x = 1; x < board.getXMax() - 1; x++) {
					if (this.isValidMove(x, y, hand[i], board, tilesPlaced)) {
						int moveScore = getMoveScore(x, y, board);
						Move move = new Move(hand[i], i, x, y, moveScore);
						return move;
					}	
				}
			}
		}
		return null;
	}
	
	/**
	 * Finds three moves, then returns the highest scoring one
	 * @param board
	 * @param tilesPlaced
	 * @return move, null if no possible moves
	 */
	public Move aiModerate(Board board, int tilesPlaced) {
		
		int count = 0;
		int choices = 3;
		Move[] moves = new Move[3];
		for (int i = 0; i < hand.length && count < choices; i++) {
			for (int y = 1; y < board.getYMax() - 1 && count < choices; y++) {
				for (int x = 1; x < board.getXMax() - 1 && count < choices; x++) {
					if (this.isValidMove(x, y, hand[i], board, tilesPlaced)) {
						int moveScore = getMoveScore(x, y, board);
						Move move = new Move(hand[i], i, x, y, moveScore);
						moves[count] = move;
						count++;
					}	
				}
			}
		}
		
		Move bestMove = moves[0];
		for (int i = 1; i < choices; i++) {
			if (moves[i] != null && moves[i].getScore() > bestMove.getScore())
				bestMove = moves[i];
		}
		
		return bestMove;
	}
	
	/**
	 * Finds all possible moves and chooses the best of them
	 * @param board the game board
	 * @param tilesPlaced number of tiles placed
	 * @return best possible move, null if none possible
	 */
	public Move aiHard(Board board, int tilesPlaced) {
		
		ArrayList<Move> moves = findMoves(0, 5, board, tilesPlaced);
		
		Move bestMove = moves.get(0);
		for (int i = 1; i < moves.size(); i++) {
			if (moves.get(i).getScore() > bestMove.getScore())
				bestMove = moves.get(i);
		}
		
		return bestMove;
	}
	
}
