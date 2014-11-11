package objectGenerator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import objectGenerator.templates.*;
import utilities.TextDisplay;
import utilities.Utilities;

public class ObjectGenerator {
	private List<String> mainMenuText;
	private List<String> YesNoMenu;
	private List<String> ObjectMenu;
	private List<String> ObjectGenerationMenu;
	private Scanner userInput = new Scanner(System.in);
	private List<Object> objList;

	public ObjectGenerator() {
		mainMenuText = Utilities
				.readToList("src/objectGenerator/assets/MainMenuText.txt");
		YesNoMenu = Utilities
				.readToList("src/objectGenerator/assets/YesNo.txt");
		ObjectMenu = Utilities
				.readToList("src/objectGenerator/assets/ObjectMenu.txt");
		ObjectGenerationMenu = Utilities
				.readToList("src/objectGenerator/assets/ObjectGenerationMenu.txt");
		// SimplePrimitiveText =
		// Utilities.readToList("src/objectGenerator/assets/MainMenuText.txt");
		objList = new ArrayList<Object>();
	}

	public void objGeneratorMenu() {
		switch (menuSelect(ObjectGenerationMenu)) {
		case (1):
			objList.add(createSimplePrimitive());
			break;
		case (2):
			objList.add(createSimpleObject());
			break;
		case (3):

		case (4):

		case (5):

		case (6):
			mainMenu();
		}
	}
	
	public void mainMenu() {
		switch (menuSelect(mainMenuText)) {
		case (1):
			objGeneratorMenu();
		break;
		case (2):
			TextDisplay.display("PLACEHOLDER FOR VIEW OBJS");
		break;
		case (3):
			TextDisplay.display("PLACEHOLDER FOR SERIALIZE AND SEND");
		break;
			
		case (4):
			TextDisplay.display("Now terminating program");
			System.exit(0);
		}
		mainMenu();
	}
	
	public Object createSimpleObject() {
		Object obj = new SimpleObjects();
		setFields(obj);
		return obj;
	}

	public Object createSimplePrimitive() {
		Object obj = new SimpleInts();
		setFields(obj);
		return obj;
	}

	public void setFields(Object obj) {
		TextDisplay.display("Field names");
		List<String> fieldList = getFieldList(obj);
		fieldList.add((fieldList.size() + 1) + ") Exit");
		int selection = -1;
		while (selection != (fieldList.size() + 1)) {
			selection = menuSelect(fieldList);
			if (selection > 0 && selection <= fieldList.size() - 1)
				setField(obj, selection - 1);
			else
				break;
		}
		objGeneratorMenu();
	}

	/**
	 * Allows the user to set a specific field
	 * 
	 * @param obj
	 * @param fieldIndex
	 */
	public void setField(Object obj, int fieldIndex) {
		Field field = obj.getClass().getDeclaredFields()[fieldIndex];
		Class<?> fieldType = field.getType();
		TextDisplay.display("You have selected field: " + field.getName());
		TextDisplay.display("The type is: " + fieldType.getName());
		if (fieldType.equals(int.class)) {
			setIntField(obj, fieldIndex);
		} else if (fieldType.equals(Object.class))
		{
			TextDisplay.display("Please select what object to choose:");
			switch (menuSelect(ObjectMenu)) {
			case (1):
				mainMenu();
				break;
			case (2):
				setIntField(obj, fieldIndex);
				break;
			case (3):
				menuSelect(createdObjects());
				break;
			}
		}
	}
	
	public void setIntField(Object obj, int fieldIndex)
	{
		TextDisplay.display("Please enter an int to set the field:");
		int input = 0;
		Field field = obj.getClass().getDeclaredFields()[fieldIndex];
		Class<?> fieldType = field.getType();
		if (!fieldType.equals(int.class) && !fieldType.equals(Object.class)) throw new RuntimeException("Cant set non int field to int");
		try {
			input = userInput.nextInt();
		} catch (InputMismatchException e) {
			TextDisplay.display("Invalid selection, try again.");
			userInput.next();
			setField(obj, fieldIndex);
		}
		field.setAccessible(true);
		
		try {
			field.set(obj, input);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		TextDisplay.display("Successfully set field: " + field.getName() + " to " + input);
	}

	/**
	 * Returns a list of all the objects created
	 * @return
	 */
	public List<String> createdObjects()
	{
		List<String> createdObjectClassNames = new ArrayList<String>();
		for(Object obj : objList)
			createdObjectClassNames.add(obj.toString());
		return createdObjectClassNames;
	}
		
	public List<String> getFieldList(Object obj) {
		List<String> fieldList = new ArrayList<String>();
		int i = 0;
		for (Field field : obj.getClass().getDeclaredFields())
			fieldList.add((++i) + ") " + field.getName());
		return fieldList;
	}

//	public void retry() {
//		TextDisplay.display("Would you like to create another object?");
//		switch (menuSelect(RetryText)) {
//		case (1):
//			objGeneratorMenu();
//			break;
//		case (2):
//			serialize();
//			break;
//		}
//	}

	public void serialize() {
		TextDisplay.display("Would you like to serialize objects?");
		switch (menuSelect(YesNoMenu)) {
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
	 * 
	 * @param list
	 */
	public void serializeList(List<Object> list) {
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
		TextDisplay.display(TextDisplay.repeatChar("#", 50));
		return input;
	}

	public static void main(String[] args) {
		ObjectGenerator objGen = new ObjectGenerator();
		TextDisplay.display("Object Generator:");
		objGen.mainMenu();
	}
}
