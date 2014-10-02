package main;

/**
 * Class of static methods to hold text display functionality
 * @author achu
 *
 */
public class TextDisplay 
{
	private static int TEXT_WRAP_DEFAULT = 50;	// The default value to wrap around display text.
	
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
		System.out.println(message);   // By not directly calling sysout, we allow the 
	}
	
	public static void debug()
	{
		
	}
}
