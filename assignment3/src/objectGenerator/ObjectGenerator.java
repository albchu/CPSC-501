package objectGenerator;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import utilities.TextDisplay;
import utilities.Utilities;

public class ObjectGenerator {
	private List<String> mainMenuText;
	private List<String> RetryText;
	private Scanner userInput = new Scanner(System.in);
	private List<Object> objList;

	public ObjectGenerator() {
		mainMenuText = Utilities.readToList("src/objectGenerator/assets/MainMenuText.txt");
		RetryText = Utilities.readToList("src/objectGenerator/assets/MainMenuText.txt");
		objList = new ArrayList<Object>();
	}

	public void mainMenu() {
		switch (menuSelect(mainMenuText)) {
		case (1):
			objList.add(createSimplePrimitive());
			break;
		case (2):

		case (3):

		case (4):

		case (5):

		}
		retry();
	}
	
	public Object createSimplePrimitive()
	{
		Object obj = null;
		return obj;
	}

	public void retry()
	{
		TextDisplay.display("Would you like to create another object?");
		switch (menuSelect(RetryText)) {
		case (1):
			mainMenu();
		break;
		case (2):
			serialize();
		break;
		}
	}
	public void serialize()
	{
		TextDisplay.display("Would you like to serialize objects?");
		switch (menuSelect(RetryText)) {
		case (1):
			serializeList(objList);
		break;
		case (2):
			System.exit(0);
		break;
		}
	}
	
	/**
	 * Serializes all elements in the parameter list
	 * @param list
	 */
	public void serializeList(List<Object> list)
	{
		TextDisplay.display("This isnt implemented");
	}
	
	/**
	 * Print a menu and do not return until valid option is selected
	 * 
	 * @param menuList
	 * @return
	 */
	public int menuSelect(List<String> menuList) {
		for (String line : menuList)
			TextDisplay.display(line);
		int input = -1;
		do {
			TextDisplay.display("Please input a valid entry:");
			try {
				input = userInput.nextInt();
			} catch (InputMismatchException e) {
				TextDisplay.display("Invalid selection, try again.");
				userInput.next();
			}
		} while (input < 0 || input > menuList.size());
		TextDisplay.display("You have selected: " + input);
		return input;
	}

	public static void main(String[] args) {
		ObjectGenerator objGen = new ObjectGenerator();
		TextDisplay.display("Object Generator:");
		objGen.mainMenu();
	}
}
