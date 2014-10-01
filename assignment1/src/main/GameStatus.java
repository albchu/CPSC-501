/*
CPSC 233
Assignment 4
By Albert Chu
10059388
Professor James Tam

Updated March 11th 3:11pm
Version 1.24
 */

public class GameStatus
{
    public static final int NEUTRAL = 0;
    public static final int LOST = 1;
    public static final int WON = 2;
    private static int currentStatus;

    public GameStatus()
    {
	currentStatus = 0;
    }

    public static void updateStatus(int n)
    {
        if(n <= 0)
        {
            currentStatus = 1;
        }

	else
        {
            int i = CommandProcessor.greenwood.getPlayerRow();
            int j = CommandProcessor.greenwood.getPlayerColumn();
            if(i == Forest.EXIT_ROW && j == Forest.EXIT_COLUMN)
                currentStatus = 2;
        }
    }

    public static int checkStatus()
    {
        return currentStatus;
    }



}