package main.utilities;

import main.Mode;

/**
 * Class of static methods to hold text display functionality
 * @author achu
 *
 */
public class TextDisplay 
{
	private static int DEFAULT_TEXT_WRAP = 50;	// The default value to wrap around display text.
	private static String DEFAULT_DELIMINATOR =  "\n";	// The default value to wrap around display text.
	
	/**
	 * Method to return wrapped string messages
	 * @param textWrap
	 * @param message
	 * @return
	 */
	public static String wrapString(String s, String deliminator, int length) {
		StringBuilder sb = new StringBuilder(s);

		int i = 0;
		while (i + length < sb.length() && (i = sb.lastIndexOf(" ", i + length)) != -1) {
		    sb.replace(i, i + 1, deliminator);
		}
	    return sb.toString();
	}

	public static void display(String message)
	{
		display(message, DEFAULT_DELIMINATOR, DEFAULT_TEXT_WRAP);
	}
	
	public static void display(String message, String deliminator, int length)
	{
		System.out.println(wrapString(message, deliminator, length));   // By not directly calling sysout, we allow the program to be scaled easier later on
	}
	
	public static String repeatChar(char c)
	{
		return repeatChar(c, DEFAULT_TEXT_WRAP);
	}
	
	public static String repeatChar(char c, int repetitions)
	{
		return String.format(String.format("%%0%dd", repetitions), 0).replace('0',c);
	}
	
	public static void alert(String message)
	{
		display(repeatChar('-'));
		display(message);
		display(repeatChar('-'));
	}
	
	public static void debug(String message)
	{
		if (Mode.debug)
			display("DEBUG: " + message);
	}
	
	public static void cheat(String message)
	{
		if (Mode.cheat)
			display("CHEAT: " + message);
	}
}
