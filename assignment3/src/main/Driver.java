package main;

import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import objectGenerator.ObjectGenerator;
import utilities.TextDisplay;
import utilities.Utilities;

public class Driver
{
	private List<String> mainMenuText;
	private List<String> YesNoMenu;
	private List<String> serverConfig;
	private List<File> serialized;
	private Scanner userInput = new Scanner(System.in);
	private ObjectGenerator objGen;
	private Serializer serializer;
	private String host;
	private int port;

	public static void main(String[] args)
	{
		Driver driver = new Driver();
		driver.mainMenu();
	}

	public Driver()
	{
		YesNoMenu = Utilities.readToList("src/objectGenerator/assets/YesNo.txt");
		mainMenuText = Utilities.readToList("src/objectGenerator/assets/MainMenuText.txt");
		serverConfig = Utilities.readToList("src/objectGenerator/assets/serverConfig.txt");
		objGen = new ObjectGenerator();
		serializer = new Serializer();
		host = serverConfig.get(0);
		port = Integer.parseInt(serverConfig.get(1));
	}

	public void mainMenu()
	{
		switch (Driver.menuSelect(userInput, mainMenuText))
		{
			case (1):
				objGen.objGeneratorMenu();
				break;
			case (2):
				TextDisplay.display("Generated objects: " + objGen.createdObjects().size());
				TextDisplay.display(objGen.createdObjects());
				TextDisplay.display(TextDisplay.repeatChar("#", 50));
				break;
			case (3):
				serialized = serializer.toFile(objGen.getObjList());
				TextDisplay.display("Output serialized documents to root");
				break;

			case (4):
				launchClient(host, port, serialized);
				break;
			case (5):
				launchServer(host, port, "downloaded");
				break;
			case (6):
				TextDisplay.display("PLACEHOLDER FOR DESERIALIZING SHIT");
				System.exit(0);

			case (7):
				TextDisplay.display("Now terminating program");
				System.exit(0);
		}
		mainMenu();
	}

	public static void launchServer(String host, int port, String dirPath)
	{
		TextDisplay.display("Now accepting serialized files from: " + host + " and port: " + port);

		(new File(dirPath)).mkdirs(); // Create the directory to download files
										// to
		ServerSocket serverSocket;
		try
		{
			serverSocket = new ServerSocket(port);
			Socket socket = serverSocket.accept();

			BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			DataInputStream dis = new DataInputStream(bis);

			int filesCount = dis.readInt();
			File[] files = new File[filesCount];
			TextDisplay.display("This is the number of files to be downloaded: " + filesCount);
			for (int i = 0; i < filesCount; i++)
			{
				long fileLength = dis.readLong();
				String fileName = dis.readUTF();
				
				files[i] = new File(dirPath + "/" + fileName);
				TextDisplay.display("Downloading file: " + fileName + " to " + dirPath);
				FileOutputStream fos = new FileOutputStream(files[i]);
				BufferedOutputStream bos = new BufferedOutputStream(fos);

				for (int j = 0; j < fileLength; j++)
					bos.write(bis.read());

				bos.close();
			}

			dis.close();
			serverSocket.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void launchClient(String host, int port, List<File> files)
	{
		TextDisplay.display("Now sending serialized files to: " + host + " and port: " + port);

		Socket socket;
		try
		{
			socket = new Socket(InetAddress.getByName(host), port);

			BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
			DataOutputStream dos = new DataOutputStream(bos);

			dos.writeInt(files.size());

			for (File file : files)
			{
				long length = file.length();
				dos.writeLong(length);

				String name = file.getName();
				dos.writeUTF(name);

				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);

				int theByte = 0;
				while ((theByte = bis.read()) != -1)
					bos.write(theByte);

				bis.close();
			}

			dos.close();
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		} catch (ConnectException e)
		{
			TextDisplay.display("No server to connect to, please prepare accepting side before attempting to send.");
			return;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Do not return until a valid int is returned
	 * 
	 * @return
	 */
	public static int getNextInt(Scanner userInput)
	{
		int input = -1;
		while (true)
		{
			try
			{
				input = userInput.nextInt();
			} catch (InputMismatchException e)
			{
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
	public static int menuSelect(Scanner userInput, List<String> menuList)
	{
		TextDisplay.display(menuList);
		int input = -1;
		do
		{
			TextDisplay.display("Please input a valid entry:");
			input = Driver.getNextInt(userInput);
		} while (input < 0 || input > menuList.size());
		TextDisplay.display("You have selected: " + input);
		TextDisplay.display(TextDisplay.repeatChar("#", 50));
		return input;
	}
}
