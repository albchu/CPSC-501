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

import java.util.Random;

import main.utilities.TextDisplay;

public class Forest
{
	public static final int EXIT_ROW = 9;
	public static final int EXIT_COLUMN = 9;
	public static final int SIZE = 10;
	private static final int RANGE_RANDOM_TOKENS = 4;
	private static final int MIN_RANDOM_TOKENS = 5;
	private ForestItem grid[][];
	private final String HORIZONTAL_NUMBERS = "  0 1 2 3 4 5 6 7 8 9 ";
	private final String HORIZONTAL_BORDER = "  ------------------- ";
	private int playerRow;
	private int playerColumn;
	private int numberTokens;
	private int tokenRow;
	private int tokenColumn;
	public ForestItem player;
	private int courage;

	public Forest()
	{ // Initialize grid with each spot put to null
		grid = new ForestItem[SIZE][SIZE];
		for (int r = 0; r < 10; r++)
		{
			for (int c = 0; c < 10; c++)
				grid[r][c] = null;
		}

		grid = FileForest.fileRead(SIZE, SIZE);
		playerRow = 1;
		playerColumn = 1;
		numberTokens = 0;
		player = grid[playerRow][playerColumn];
		player.setCourage(100);

	}

	public ForestItem[][] getGrid()
	{
		ForestItem aGrid[][] = new ForestItem[10][10];
		for (int i = 0; i < 10; i++)
		{
			for (int j = 0; j < 10; j++)
				aGrid[i][j] = grid[i][j];

		}

		return aGrid;
	}

	public ForestItem getPlayerLocation()
	{
		if (Mode.debug)
		{
			TextDisplay.debug("Forest.getPlayer()");
			if (grid[playerRow][playerColumn] == null)
				TextDisplay.alertMsgLen("Player has become null");
		}
		return grid[playerRow][playerColumn];
	}

	public int getPlayerRow()
	{
		return playerRow;
	}

	public int getPlayerColumn()
	{
		return playerColumn;
	}

	public void setPlayerRow(int r)
	{
		playerRow = r;
	}

	public void setPlayerColumn(int c)
	{
		playerColumn = c;
	}

	public void display()
	{
		courage = player.getCourage();
		TextDisplay.debug("Forest.display()");
		TextDisplay.alert("Player Courage: " + courage);
		TextDisplay.alertMsgLen(HORIZONTAL_NUMBERS);
		for (int r = 0; r < SIZE; r++)
		{
			System.out.println(HORIZONTAL_BORDER);
			System.out.print(r);
			for (int c = 0; c < SIZE; c++)
			{
				System.out.print("|");
				if (grid[r][c] != null)
					System.out.print(grid[r][c].getAppearance());
				else
					System.out.print(" ");
			}
			System.out.println("|");
		}
		TextDisplay.debug("MODE ON");
		TextDisplay.cheat("MODE ON");
	}

	public void addTokens()
	{
		Random random = new Random();
		int tokensAdded = random.nextInt(4) + 5;
		int count = 1;

		int[] totalSpaces = new int[SIZE * SIZE];
		do
		{
			int vacantSpaces = 0;
			int cellNum = 0;
			int arrayNum = 0;

			// Parses through grid and finds empty spaces
			for (int r = 0; r < SIZE; r++)
				for (int c = 0; c < SIZE; c++)
				{
					if (grid[r][c] == null)
					{
						cellNum = r * SIZE + c; // convert to a one dimensional
												// list
						totalSpaces[vacantSpaces] = cellNum;
						vacantSpaces++;
					}
				}
			if (vacantSpaces == 0)
			{
				System.out.println("grid is full");

			}
			arrayNum = random.nextInt(vacantSpaces);
			cellNum = totalSpaces[arrayNum];

			int row = cellNum / SIZE;
			int col = cellNum % SIZE;
			ForestItem token = new ForestItem(ForestItem.TOKEN_ICON);
			grid[row][col] = token;
			count++;
		} while (count <= tokensAdded);

	}

	public void inflictTokenFear()
	{
		player.setCourage(player.getCourage() - 10);
	}

	public boolean isPanicking()
	{
		boolean panic = false;
		if (player.getCourage() <= 25)
		{
			Random random = new Random();
			int i = random.nextInt(2);
			if (i == 1)
				panic = true;
		}
		return panic;
	}

	public int panicMove()
	{
		TextDisplay.alert("The player panics and moves before you decide");
		Random random = new Random();
		return random.nextInt(9) + 1;
	}

	public boolean adjacentToToken()
	{
		boolean tokensFound = false;
		TextDisplay.debug("Forest.isAdjacentToken()");
		System.out.println(playerRow + playerColumn);
		for (int r = playerRow - 1; r <= playerRow + 1; r++)
		{
			for (int c = playerColumn - 1; c <= playerColumn + 1; c++)
			{

				if (inBorders(r, c))
				{
					if (grid[r][c] != null
							&& grid[r][c].getAppearance() == ForestItem.TOKEN_ICON)
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean inBorders(int r, int c)
	{
		boolean withinBorders = true;
		if (r < 0 || r >= 10 || c < 0 || c >= 10)
			withinBorders = false;
		return withinBorders;
	}

	public void movePlayer(int n)
	{
		int dx = (n - 1) % 3 - 1;
		int dy = -((n - 1) / 3 - 1);

		int x = playerColumn + dx;
		int y = playerRow + dy;
		if (x < 0 || x > SIZE - 1 || y < 0 || y > SIZE - 1
				|| grid[y][x] != null
				&& grid[y][x].getAppearance() != ForestItem.EXIT_ICON)
		{
			TextDisplay.alert("Either you're trying to punch a token or run into a wall,\nyou're not going anywhere");
		} else
		{

			grid[y][x] = grid[playerRow][playerColumn];
			grid[playerRow][playerColumn] = null;
			playerColumn = x;
			playerRow = y;
		}
	}
}