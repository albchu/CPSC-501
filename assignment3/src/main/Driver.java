package main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import objectGenerator.ObjectGenerator;
import utilities.TextDisplay;
import utilities.Utilities;

public class Driver {
	private List<String> mainMenuText;
	private List<String> YesNoMenu;
	private List<String> serverConfig;
	private Scanner userInput = new Scanner(System.in);
	private ObjectGenerator objGen;
	private Serializer serializer;
	private String host;
	private int port;
	
	public static void main(String[] args) {
		Driver driver = new Driver();
		driver.mainMenu();
	}
	public Driver()
	{
		YesNoMenu = Utilities
				.readToList("src/objectGenerator/assets/YesNo.txt");
		mainMenuText = Utilities.readToList("src/objectGenerator/assets/MainMenuText.txt");
		serverConfig = Utilities.readToList("src/objectGenerator/assets/serverConfig.txt");
		objGen = new ObjectGenerator();
		serializer = new Serializer();
		host = serverConfig.get(0);
		port = Integer.parseInt(serverConfig.get(1));
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
			launchServer(host, port);
		case (5):
			TextDisplay.display("Now accepting serialized files on: " + host + " and port: " + port);
		
		case (6):
			TextDisplay.display("Now terminating program");
		System.exit(0);
		}
		mainMenu();
	}
	
	public static void launchServer(String host, int port, List<File> servingFiles) throws IOException
	{
		TextDisplay.display("Now serving serialized files on: " + host + " and port: " + port);
		ServerSocket servsock = new ServerSocket(port);
		while (true) {
			Socket sock = servsock.accept();
			TextDisplay.display("Accepted connection from " + sock.getInetAddress());
			for(File file: servingFiles)
			{
				byte[] byteArray = new byte[(int) file.length()];
				BufferedInputStream bis;
				try {
					bis = new BufferedInputStream(
							new FileInputStream(file));
					bis.read(byteArray, 0, byteArray.length);
					OutputStream os = sock.getOutputStream();
					os.write(byteArray, 0, byteArray.length);
					os.flush();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			sock.close();
		}
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
