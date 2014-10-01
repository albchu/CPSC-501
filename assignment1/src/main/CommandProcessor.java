package main;

/*
CPSC 233
Assignment 4
By Albert Chu
10059388
Professor James Tam

Updated March 11th 3:11pm
Version 1.24
 */

import java.util.Scanner;

public class CommandProcessor
{
    public static Forest greenwood;
    public Scanner in;
    public boolean cheatMode;
    public boolean debugMode;
    private int move;

    public CommandProcessor()
    {
	 in = new Scanner (System.in);
	 cheatMode = false;
	 debugMode = false;
	 greenwood = new Forest();
    }

    public void instructions ()
    {
	System.out.println("MOVEMENT INSTRUCTIONS");
	System.out.println("\t7 8 9");
	System.out.println("\t4 5 6");
	System.out.println("\t1 2 3");
    }


    public int userInput ()
    {
	move = in.nextInt();
	if (move > 0 && move < 10)
	{
	    greenwood.movePlayer(move);
	    if (Mode.cheat)
	    {
		System.out.println();
		System.out.println("<<Cheat: GOD MODE");
		System.out.println();
	    }
	    else{
	    if (greenwood.adjacentToToken())
	    {
		greenwood.inflictTokenFear();
	    }
	    else
	    {
		greenwood.player.setCourage(greenwood.player.getCourage() - 1);
	    }
	    GameStatus.updateStatus(greenwood.player.getCourage());
	    }    
	}
	else if (move == 0)
	{
	    hiddenMenu();
	}
	else
	{
	    return move;
	}
	return move;
    }

    public void hiddenMenu ()
    {
	int select;
	System.out.println("[1]- Cheat Toggle");
	System.out.println("[2]- Debug Toggle");

	select = in.nextInt();
	if (select == 1)
	{
	    cheatToggle();
	}
	else if (select == 2)
	{
	    debugToggle();
	}
	else
	{
	    System.out.println("You have entered an invalid value");
	}
	
    }

    public void cheatToggle ()
    {
	if (Mode.cheat == false)
	{
	    System.out.println("CheatMode: ON");
	    Mode.cheat = true;
	}
	else if (Mode.cheat == true)
	{
	    System.out.println("CheatMode: OFF");
	    Mode.cheat = false;
	}
	else
	{
	    System.out.println("ERROR: Could not change CHEAT mode");
	}
    }

    public void debugToggle ()
    {
	if (Mode.debug == false)
	{
	    System.out.println("CheatMode: ON");
	    Mode.debug = true;
	}
	else if (Mode.debug == true)
	{
	    System.out.println("CheatMode: OFF");
	    Mode.debug = false;
	}
	else
	{
	    System.out.println("ERROR: Could not change DEBUG mode");
	}
    }

    public void intro()
    {
    System.out.println();
    System.out.println("ESCAPE FROM THE WOODS");
    System.out.println("---------------------");
    System.out.println("Your party 'P' is lost in the middle of the");
    System.out.println("greenwood and you must guide them safely to");
    System.out.println("exit 'E'. It's not as easy as it sounds though,");
    System.out.println("the horrors of wood decreases your party's");
    System.out.println("courage over time. The longer you take to escape");
    System.out.println("the weaker you will get. If your courage gets");
    System.out.println("too low then your party may panic and run off");
    System.out.println("randomly into the woods. If your courage reaches");
    System.out.println("zero then your party has died of fright and you");
    System.out.println("lose the game!");
    System.out.println();
    System.out.println("Further challenges abound, scary magic tokens 'x'");
    System.out.println("randomly appear. If your party gets close enough");
    System.out.println("to them then their courage will quickly drain");
    System.out.println("away.");
    System.out.println();
    System.out.println("Are you up to the challenge of saving your");
    System.out.println("hapless party? Find out now and see if you can");
    System.out.println("ESCAPE FROM THE WOODS!!!");
    System.out.println();
    System.out.println("Press enter to proceed");
    in.nextLine();
    }

    public void start ()
    {
	greenwood.getGrid();
	ForestItem player = greenwood.getPlayerLocation();
	intro();
	do
	{
	    greenwood.display();
	    if (greenwood.isPanicking())
	    {
		greenwood.movePlayer (greenwood.panicMove());
	    }
	    instructions ();
	    greenwood.addTokens ();
	    userInput ();
	}while(GameStatus.checkStatus() == GameStatus.NEUTRAL);

	if (GameStatus.checkStatus() == GameStatus.WON)
	{
	    System.out.println("------------------------");
	    System.out.println("YOU HAVE BEATEN THE GAME");
	    System.out.println("------------------------");
	}
	else if (GameStatus.checkStatus() == GameStatus.LOST)
	{
	    System.out.println("----------------------");
	    System.out.println("YOU HAVE LOST THE GAME");
	    System.out.println("----------------------");
	}
	else
	{
	    System.out.println("----------------------");
	    System.out.println("QUITTING ALREADY?");
	    System.out.println("GAME ENDED ON DRAW");
	    System.out.println("----------------------");
	}

    }

}