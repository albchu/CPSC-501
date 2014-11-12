package main;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import objectGenerator.ObjectGenerator;
import utilities.TextDisplay;
import utilities.Utilities;

public class Driver {
	private List<String> mainMenuText;
	private List<String> YesNoMenu;
	private Scanner userInput = new Scanner(System.in);
	private ObjectGenerator objGen;
	private Serializer serializer;
	private String host;
	
	public static void main(String[] args) {
		Driver driver = new Driver();
		driver.mainMenu();
	}
	public Driver()
	{
		YesNoMenu = Utilities
				.readToList("src/objectGenerator/assets/YesNo.txt");
		mainMenuText = Utilities.readToList("src/objectGenerator/assets/MainMenuText.txt");
		objGen = new ObjectGenerator();
		serializer = new Serializer();
		host = "localhost";
	}
	
	public void mainMenu() {
		switch (Driver.menuSelect(userInput, mainMenuText)) {
		case (1):
			objGen.objGeneratorMenu();
			break;
		case (2):
			TextDisplay
					.display("Generated objects: " + objGen.createdObjects().size());
			TextDisplay.display(objGen.createdObjects());
			TextDisplay.display(TextDisplay.repeatChar("#", 50));
			break;
		case (3):
			serializer.toFile(objGen.getObjList());
			TextDisplay.display("Output serialized documents to root");
			break;

		case (4):
			TextDisplay.display("Now sending program to :" + host);
			System.exit(0);

		case (5):
			TextDisplay.display("Now terminating program");
		System.exit(0);
		}
		mainMenu();
	}
	
	/**
	 * Do not return until a valid int is returned
	 * @return
	 */
	public static int getNextInt(Scanner userInput)
	{
		int input = -1;
		while(true)
		{
			try {
				input = userInput.nextInt();
			} catch (InputMismatchException e) {
				TextDisplay.display("Invalid selection, try again.");
				userInput.next();
			}
			return input;
		}
	}
	
	/**
	 * Print a menu and do not return until valid option is selected
	 * 
	 * @param menuList
	 * @return
	 */
	public static int menuSelect(Scanner userInput, List<String> menuList) {
		TextDisplay.display(menuList);
		int input = -1;
		do {
			TextDisplay.display("Please input a valid entry:");
			input = Driver.getNextInt(userInput);
		} while (input < 0 || input > menuList.size());
		TextDisplay.display("You have selected: " + input);
		TextDisplay.display(TextDisplay.repeatChar("#", 50));
		return input;
	}
}
