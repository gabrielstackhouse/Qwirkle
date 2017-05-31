package qwirkle;
import java.awt.Color;

import com.googlecode.lanterna.TextColor;

public class Tile {

	private char symbol;
	private Color color;
	
	public Tile(char symbol, Color color) {
		this.symbol = symbol;
		this.color = color;
	}
	
	public char getSymbol() {
		return symbol;
	}
	
	public Color getColor() {
		return color;
	}
	
	public TextColor getTextColor() {
		if (color.equals(Color.orange)) {
			TextColor.RGB textColor = new TextColor.RGB(255, 153, 0);
			return textColor;
		}
		else if (color.equals(Color.magenta))
			return TextColor.ANSI.MAGENTA;
		else if (color.equals(Color.yellow))
			return TextColor.ANSI.YELLOW;
		else if (color.equals(Color.red)) 
			return TextColor.ANSI.RED;
		else if (color.equals(Color.blue)) 
			return TextColor.ANSI.CYAN;
		else
			return TextColor.ANSI.GREEN;
	}
	
	public String toString() {
		return color.toString() + " " + symbol;
	}
	
	
}
