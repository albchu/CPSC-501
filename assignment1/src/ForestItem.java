/*
CPSC 233
Assignment 4
By Albert Chu
10059388
Professor James Tam

Updated March 11th 3:11pm
Version 1.24
 */

import java.util.Random;

public class ForestItem
{
    private int playerCourage;
    private final int OTHERCOURAGE = 0;
    private char appearance;
    public final static char TOKEN_ICON = 'x';
    public final static char EXIT_ICON = 'E';
    public final static char PLAYER_ICON = 'p';
    public final static char EMPTY = ' ';
    private int courage;
    private static final int TOKEN_FEAR = 10;
    public static final int PLAYER_STARTING_COURAGE = 100;
    public static final int PANIC_LEVEL = 25;
    public static int tokenRow;
    public static int tokenColumn;


    public ForestItem()
    {
        appearance = EMPTY;
    }

    public ForestItem(char c)
    {
        setAppearance(c);
        if(c == PLAYER_ICON)
            courage = PLAYER_STARTING_COURAGE;
        else
        if(c == TOKEN_ICON)
            courage = OTHERCOURAGE;
        else
        if(c == EXIT_ICON)
            courage = OTHERCOURAGE;
    }
        public void setAppearance(char c)
    {
        appearance = c;
    }

    public char getAppearance()
    {
        return appearance;
    }

    public int getCourage()
    {
        return courage;
    }

    public void setCourage(int i)
    {
        courage = i;
    }

    public void setTokenRow (int r)
    {
	tokenRow = r;
    }

    public int getTokenRow ()
    {
	return tokenRow;
    }

    public void setTokenColumn (int c)
    {
	tokenColumn = c;
    }

    public int getTokenColumn ()
    {
	return tokenColumn;
    }



}