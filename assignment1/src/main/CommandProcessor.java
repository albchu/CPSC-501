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

import javax.swing.text.Utilities;

import main.utilities.Common;
import main.utilities.TextDisplay;

public class CommandProcessor
{
	public static Forest greenwood;
	public Scanner in;
	public boolean cheatMode;
	public boolean debugMode;
	private int move;

	public CommandProcessor()
	{
		in = new Scanner(System.in);
		cheatMode = false;
		debugMode = false;
		greenwood = new Forest();
	}

	public void instructions()
	{
		TextDisplay.debug("Display Instructions");
		TextDisplay.display("MOVEMENT INSTRUCTIONS");
		TextDisplay.display("\t7 8 9");
		TextDisplay.display("\t4 5 6");
		TextDisplay.display("\t1 2 3");
	}

	public int userInput()
	{
		move = in.nextInt();
		if (greenwood.inBorders(move))
		{
			greenwood.movePlayer(move);
			
			TextDisplay.cheat("Mode Activated! No damage taken.");
			
			if (!Mode.cheat)
			{
				if (greenwood.adjacentToToken())
				{
					greenwood.inflictTokenFear();
				} else
				{
					greenwood.player
							.setCourage(greenwood.player.getCourage() - 1);
				}
				GameStatus.updateStatus(greenwood.player.getCourage());
			}
		} else if (move == 0)
		{
			hiddenMenu();
		} 
		return move;
	}

	public void hiddenMenu()
	{
		int select;
		TextDisplay.display("[1]- Cheat Toggle");
		TextDisplay.display("[2]- Debug Toggle");

		select = in.nextInt();
		
		//TODO: Refactor: Use case statements
		if (select == 1)
		{
			cheatToggle();
		} else if (select == 2)
		{
			debugToggle();
		} else
		{
			TextDisplay.display("You have entered an invalid value");
		}

	}

	public void cheatToggle()
	{
		Mode.cheat = !Mode.cheat;
	}

	public void debugToggle()
	{
		Mode.debug = !Mode.debug;
	}

	public void intro()
	{
		TextDisplay.alert(Common.readFromFile("intro.txt"));
		in.nextLine();
	}

	public void start()
	{
		intro();
		do
		{
			greenwood.display();
			instructions();
			userInput();
			if (greenwood.isPanicking())
			{
				greenwood.movePlayer(greenwood.panicMove());
			}
			greenwood.addTokens();
		} while (GameStatus.checkStatus() == GameStatus.NEUTRAL);

		if (GameStatus.checkStatus() == GameStatus.WON)
		{
			TextDisplay.alert("YOU HAVE BEATEN THE GAME");
		} else if (GameStatus.checkStatus() == GameStatus.LOST)
		{
			TextDisplay.alert("YOU HAVE LOST THE GAME");
		} else
		{
			//TODO: Cannot quit early, functionality needs to be added
			TextDisplay.alert("QUITTING ALREADY?");
		}

	}

}