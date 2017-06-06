package qwirkle;
import java.io.IOException;
import java.util.ArrayList;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * The main Qwirkle game, containing a graphical interface using the Lanterna API
 * @author Gabriel Stackhouse
 *
 */
public class Qwirkle {
	
	private static int tilesPlaced;
	
	private static int aiDifficulty;
	
	private static Deck deck;
	private static Board board;
	private static Hand player;
	private static Hand computer;
	private static Screen screen;
	private static Terminal terminal;
	private static TextGraphics graphics;

	public static void main(String[] args) throws IOException, InterruptedException {
		
		//Initialize Game Screen
		terminal = new DefaultTerminalFactory().createTerminal();
		screen = new TerminalScreen(terminal);
		graphics = screen.newTextGraphics();
		screen.startScreen();
		screen.setCursorPosition(null);
		graphics.putString(0, 0, "Player Score: ");
		graphics.putString(61, 0, "Computer Score: ");
		graphics.putString(37, 0, "Qwirkle");
		graphics.putString(71, 22, "Deck: ");
		
		boolean run = true;
		while (run) {
		
			//Create game variables
			deck = new Deck();
			board = new Board();
			player = new Hand(deck);
			computer = new Hand(deck);
			int shuffleCount = 0; //we're going to re-shuffle the deck every three turns
			tilesPlaced = 0;
			refreshGameBoard();
			
			//Choose AI difficulty
			aiDifficulty = 0;
			printMessage("Choose difficulty: [0] Easy, [1] Moderate, [2] Hard");
			boolean isValidKey = false;
			while (!isValidKey) {
				KeyStroke key = terminal.readInput();
				if (key.getKeyType() == KeyType.Character) {
					char input = key.getCharacter();
					if (input == '0' || input == '1' || input == '2') {
						aiDifficulty = Character.getNumericValue(input);
						isValidKey = true;
					}
				}
				
				if (!isValidKey)
					printMessage("Try again: [0] Easy, [1] Moderate, [2] Hard");
			}
	
			//The game loop
			while (!deck.isEmpty() || (!player.isEmpty() && !computer.isEmpty())) {
				refreshGameBoard();
				
				//Perform move based off user's choice
				char option = playOrSwap();
				if (option == 'p') {
					if (!playTiles())
						continue;
					else
						player.fillHand(deck);
				}
				else if (option == 's' && !deck.isEmpty() && !swapTiles())
					continue;
				else if (deck.isEmpty()) {
					printMessage("No tiles in deck to swap.  Press any key to continue.");
					terminal.readInput();
					if (!playTiles())
						continue;
					else
						player.fillHand(deck);
				}
				
				//Check for game end
				if (isGameOver(player))
					break;
				
				Thread.sleep(750);
				
				//AI makes move
				//	Difficulty
				Move aiMove = null;
				if (aiDifficulty == 0)
					aiMove = computer.aiEasy(board, tilesPlaced);
				else if (aiDifficulty == 1)
					aiMove = computer.aiModerate(board, tilesPlaced);
				else 
					aiMove = computer.aiHard(board, tilesPlaced);
				
				//Place Tile
				if (aiMove != null) {
					board.placeTile(computer.removeTile(aiMove.getIndex()), aiMove.getX(), aiMove.getY());
					tilesPlaced++;
					computer.addToScore(aiMove.getScore());
					computer.addTileFromDeck(deck);
				}
				
				//Check for game end
				if (isGameOver(computer))
					break;
				
				//Shuffle deck every three rounds
				if (shuffleCount < 3)
					shuffleCount++;
				else if (!deck.isEmpty()) {
					deck.shuffle();
					shuffleCount = 0;
				}
				
				//Refresh the board
				refreshGameBoard();
				
			}
	
	
			//Calculate winner
			if (player.getScore() > computer.getScore()) {
				printMessage("You win!  Play again? [y]es, [n]o");
			}
			else if (player.getScore() == computer.getScore()) {
				printMessage("Tie game.  Play again? [y]es, [n]o");
			}
			else
				printMessage("Better luck next time.  Play again? [y]es, [n]o");
			
			//Play again?
			KeyStroke key = null;
			while (key == null || (key != null && (key.getCharacter() != 'y' && key.getCharacter() != 'n'))) {
				key = terminal.readInput();
			}

			if (key.getCharacter() == 'n')
				run = false;
			
		}	
		
		screen.stopScreen();
	}
	
	/**
	 * Refreshes the game board
	 * @param board game board
	 * @param player the user
	 * @param computer the AI opponent
	 * @param screen screen object
	 * @param graphics graphics object
	 * @throws IOException
	 */
	private static void refreshGameBoard() throws IOException {
		graphics.setBackgroundColor(TextColor.ANSI.BLACK);
		for (int i = 0; i < board.getYMax(); i++) {
			for (int j = 0; j < board.getXMax(); j++) {
				if (i == 0 || i == 20) {
					graphics.setForegroundColor(TextColor.ANSI.WHITE);
					graphics.setCharacter(j, i + 1, '-');
				}
				else if (j == 0 || j == 79) {
					graphics.setForegroundColor(TextColor.ANSI.WHITE);
					graphics.setCharacter(j, i + 1, '|');
				}
				else {
					Tile tile = board.getTile(j, i);
					if (tile == null) {
						graphics.setForegroundColor(TextColor.ANSI.WHITE);
						graphics.setCharacter(j, i + 1, ' ');
					}
					else {
						graphics.setForegroundColor(tile.getTextColor());
						graphics.setCharacter(j, i + 1, tile.getSymbol());
					}
				}
			}
		}
		
		//Hand
		int lastXPos = 35;
		for (int i = 0; i < 6; i++) {
			Tile tile = player.getTile(i);
			if (tile == null) {
				graphics.setForegroundColor(TextColor.ANSI.WHITE);
				graphics.setCharacter(lastXPos, 22, 'X');
			}
			else {
				graphics.setForegroundColor(tile.getTextColor());
				graphics.setCharacter(lastXPos, 22, tile.getSymbol());
			}
			graphics.setForegroundColor(TextColor.ANSI.WHITE);
			graphics.setCharacter(lastXPos, 23, (char) ('0' + i));
			lastXPos += 2;
		}	
		
		//Scores
		graphics.setForegroundColor(TextColor.ANSI.WHITE);
		graphics.putString(14, 0, "" + player.getScore());
		graphics.putString(77, 0, "" + computer.getScore());
		graphics.putString(77, 22, "  ");
		graphics.putString(77, 22, "" + deck.getSize());
		
		screen.refresh();
	}
	
	/**
	 * Draws yellow circles on the map for all possible moves player can make with one tile
	 * @param moves ArrayList of moves
	 * @param screen 
	 * @param graphics
	 * @throws IOException
	 */
	private static void highlightValidMoves(ArrayList<Move> moves) throws IOException {
		if (aiDifficulty == 2) return; //This is hard mode, you find your own moves!
		
		graphics.setForegroundColor(TextColor.ANSI.YELLOW);
		graphics.setBackgroundColor(TextColor.ANSI.BLACK);
		for (int i = 0; i < moves.size(); i++) {
			int x = moves.get(i).getX();
			int y = moves.get(i).getY();
			graphics.setCharacter(x, y + 1, 'O');
		}
		graphics.setForegroundColor(TextColor.ANSI.WHITE);
		screen.refresh();
	}
	
	private static void unHighlightValidMoves(ArrayList<Move> moves) throws IOException {
		
		//Un-highlight moves
		if (moves != null) {
			graphics.setForegroundColor(TextColor.ANSI.WHITE);
			for (int i = 0; i < moves.size(); i++) {
				int x2 = moves.get(i).getX();
				int y2 = moves.get(i).getY();
				graphics.setCharacter(x2, y2+1, ' ');
			}
		}
	}
	
	/**
	 * Helper method that prints a message on the bottom of the game screen
	 * @param screen game screen
	 * @param graphics graphics
	 * @param msg message to print
	 * @throws IOException
	 */
	private static void printMessage(String msg) throws IOException {
		graphics.setForegroundColor(TextColor.ANSI.WHITE);
		graphics.setBackgroundColor(TextColor.ANSI.BLACK);
		graphics.putString(0, 23, "                                                                                        ");
		graphics.putString(0, 23, msg);
		screen.refresh();
	}
	
	/**
	 * Checks if the game is over, and adds the bonus to the player's score who ended it
	 * @param deck the deck
	 * @param hand player who ended the game
	 * @return
	 */
	private static boolean isGameOver(Hand hand) {
		//Check for game end
		if (deck.isEmpty() && hand.isEmpty()) {
			hand.addToScore(6);
			return true;
		}
		return false;
	}
	
	/**
	 * Gets input from user that chooses what to do on their turn
	 * @param terminal
	 * @param screen
	 * @param graphics
	 * @return 'p' to play tiles, 's' to swap them
	 * @throws IOException
	 */
	private static char playOrSwap() throws IOException {
		printMessage("Choose option: [p]lay or [s]wap?");
		char result = '0'; //Give an initial value to compile
		boolean isValidKey = false;
		
		while (!isValidKey) {
			KeyStroke key = terminal.readInput();
			if (key.getKeyType() == KeyType.Character) {
				result = Character.toLowerCase(key.getCharacter());
				if (result == 'p' || result == 's')
					isValidKey = true;
			}
			
			if (!isValidKey)
				printMessage("Invalid key. Try again: [p]lay or [s]wap?");
			
		}
		
		return result;
	}
	
	/**
	 * User plays tiles
	 * @param terminal
	 * @param screen
	 * @param graphics
	 * @throws IOException
	 */
	private static boolean playTiles() throws IOException {
		
		//Initialize variables
		ArrayList<Move> turn = new ArrayList<Move>();
		boolean isSpace = false;
		int min = -1;
		
		//Will continue until space is hit
		while (!isSpace) {
			
			refreshGameBoard();
			
			//Find index of first tile in hand
			if (min < 0) {
				min = 0;
				while (player.getTile(min) == null) {
					min++;
				}
			}
			
			//Get tile from hand
			int index = min;
			setHandCursor(index);
			screen.refresh();
			printMessage("Choose tile to play.  Arrow keys + Enter to choose, Esc to go back");
			KeyStroke key = null;
			while (key == null || (key.getKeyType() != KeyType.Enter && key.getKeyType() != KeyType.Escape && !isSpace)) {
				key = terminal.readInput();
				
				if (key.getKeyType() == KeyType.ArrowRight && index < player.getHandLength() - 1) {
					index = incrementHandCursor(index, 1, null);
					screen.refresh();
				}
				else if (key.getKeyType() == KeyType.ArrowLeft && index > 0) {
					index = incrementHandCursor(index, -1, null);
					screen.refresh();
				}
				else if (key.getKeyType() == KeyType.Character && key.getCharacter() == ' ')
					isSpace = true;
			}
			
			//If escape is hit, go back to previous
			if (key.getKeyType() == KeyType.Escape) {
				if (turn == null || turn.size() == 0)
					return false;
				else {
					Move move = turn.remove(turn.size() - 1);
					player.addTileFromBoard(board, move.getX(), move.getY());
					tilesPlaced--;
				}
				continue;
			}
			
			//Choose tile position and place tile
			if (!isSpace) {
			
				//Highlight valid moves
				ArrayList<Move> moves = null;
				if (tilesPlaced > 0) {
					moves = player.findMoves(index, index, board, tilesPlaced);
					highlightValidMoves(moves);
				}
				
				printMessage("Place tile on board. Arrow keys + Enter to choose, Esc to go back");
				key = null;
				int x = board.getXMax() / 2;
				int y = board.getYMax() / 2;
				while (key == null || (key.getKeyType() != KeyType.Enter && key.getKeyType() != KeyType.Escape)) {
					showMapCursor(x, y, index);
					key = terminal.readInput();
					if (key.getKeyType() == KeyType.ArrowUp && y > 1) 
						hideMapCursor(x, y--, moves);
					else if (key.getKeyType() == KeyType.ArrowDown && y < board.getYMax() - 2) 
						hideMapCursor(x, y++, moves);
					else if (key.getKeyType() == KeyType.ArrowLeft && x > 1) 
						hideMapCursor(x--, y, moves);
					else if (key.getKeyType() == KeyType.ArrowRight && x < board.getXMax() - 2) 
						hideMapCursor(x++, y, moves);
					else if (key.getKeyType() == KeyType.Enter && !player.isValidMove(x, y, player.getTile(index), board, tilesPlaced)) {
						printMessage("Invalid move. Try again. Arrow keys + Enter to choose, Esc to go back");
						key = null;
					}
				}
				
				//Go back if escape is hit
				if (key.getKeyType() == KeyType.Escape) {
					hideMapCursor(x, y, moves);
					unHighlightValidMoves(moves);
					min = index;
					continue;
				}
				
				//Place Tile and increment score
				board.placeTile(player.removeTile(index), x, y);
				turn.add(new Move(player.getTile(index), index, x, y));
				tilesPlaced++;
				min = -1;
				player.addToScore(player.getMoveScore(x, y, board)); //remove later
			}
		}

		//Tile(s) placed successfully
		return true;
	}
	
	private static int incrementHandCursor(int givenIndex, int dir, boolean[] toSwap) {
		int index = givenIndex;
		
		//Skip over null values
		int increment = dir;
		boolean isValid = false;
		while (!isValid && index + increment >= 0 && index + increment < player.getHandLength()) {
			if (player.getTile(index + increment) == null)
				increment += dir;
			else
				isValid = true;
		}
		if (index + increment < 0 || index + increment >= player.getHandLength())
			increment = 0;
		
		if (increment != 0) {
			if (toSwap == null || (toSwap != null && toSwap[index] == false)) //Don't hide cursor if set to swap
				hideHandCursor(index);
			index += increment;
			setHandCursor(index);
		}
		
		return index;
	}
	
	private static void hideHandCursor(int index) {
		graphics.setBackgroundColor(TextColor.ANSI.BLACK);
		graphics.setForegroundColor(player.getTile(index).getTextColor());
		graphics.setCharacter(35 + (index * 2), 22, player.getTile(index).getSymbol());
	}
	
	private static void setHandCursor(int index) {
		graphics.setBackgroundColor(TextColor.ANSI.WHITE);
		graphics.setForegroundColor(player.getTile(index).getTextColor());
		graphics.setCharacter(35 + (index * 2), 22, player.getTile(index).getSymbol());
	}
	
	private static void showMapCursor(int x, int y, int index) throws IOException {
		graphics.setBackgroundColor(TextColor.ANSI.WHITE);
		graphics.setForegroundColor(player.getTile(index).getTextColor());
		graphics.setCharacter(x, y+1, player.getTile(index).getSymbol());
		screen.refresh();
		
	}
	
	private static void hideMapCursor(int x, int y, ArrayList<Move> moves) throws IOException {
		graphics.setBackgroundColor(TextColor.ANSI.BLACK);
		
		if (board.getTile(x, y) != null) {
			graphics.setForegroundColor(board.getTile(x, y).getTextColor());
			graphics.setCharacter(x, y+1, board.getTileSymbol(x, y));
		}
		else {
			graphics.setForegroundColor(TextColor.ANSI.WHITE);
			graphics.setCharacter(x, y+1, ' ');
		}
		
		//Highlight valid moves
		if (tilesPlaced > 0)
			highlightValidMoves(moves);
		
	}
	
	/**
	 * User swaps tiles
	 * @param hand hand of the user or computer
	 * @param deck the game deck
	 * @param terminal
	 * @param screen
	 * @param graphics
	 * @throws IOException
	 */
	private static boolean swapTiles() throws IOException {
		
		//Initialize toSwap array
		boolean[] toSwap = new boolean[player.getHandLength()];
		for (int i = 0; i < player.getHandLength(); i++)
			toSwap[i] = false;
		
		
		//Find index of first tile in hand
		int index = 0;
		while (player.getTile(index) == null) {
			index++;
		}
		
		//Choose tiles to swap
		setHandCursor(index);
		screen.refresh();
		printMessage("Choose tiles to swap. Arrow keys + Enter to choose, Space to swap, Esc to return");
		KeyStroke key = null;
		boolean isSpace = false;
		
		//Loops until either space or escape is pushed
		while (key == null || (key.getKeyType() != KeyType.Escape && !isSpace)) {
			key = terminal.readInput();
			if (key.getKeyType() == KeyType.ArrowRight && index < player.getHandLength() - 1) {
				index = incrementHandCursor(index, 1, toSwap);
				screen.refresh();
			}
			else if (key.getKeyType() == KeyType.ArrowLeft && index > 0) {
				index = incrementHandCursor(index, -1, toSwap);
				screen.refresh();
			}
			else if (key.getKeyType() == KeyType.Enter) {
				if (!toSwap[index]) {
					toSwap[index] = true;
					graphics.setBackgroundColor(TextColor.ANSI.WHITE);
				}
				else {
					toSwap[index] = false;
					graphics.setBackgroundColor(TextColor.ANSI.BLACK);
				}
				graphics.setForegroundColor(player.getTile(index).getTextColor());
				graphics.setCharacter(35 + (index * 2), 22, player.getTile(index).getSymbol());
			}
			else if (key.getKeyType() == KeyType.Character && key.getCharacter() == ' ') {
				int count = 0;
				for (int i = 0; i < player.getHandLength(); i++) {
					if (toSwap[i])
						count++;
				}
				if (count <= deck.getSize())
					isSpace = true;
				else
					printMessage("Not enough tiles in deck.  Arrow + Enter to choose, Space to swap, Esc to return");
			}
		}
		
		//If escape is hit, return false
		if (key.getKeyType() == KeyType.Escape)
			return false;
		
		//Swap tiles
		for (int i = 0; i < player.getHandLength(); i++) {
			if (toSwap[i])
				player.putTileInDeck(deck, i);
		}
		player.fillHand(deck);
		refreshGameBoard();
		printMessage("Swapped");
		
		return true;
		
	}
}