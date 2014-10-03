package main.utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Common
{
	public static String readFromFile(String path)
	{
		StringBuilder sb = new StringBuilder();
		BufferedReader br;
		try
		{
			br = new BufferedReader(new FileReader(path));
			String line = br.readLine();

			while (line != null)
			{
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			br.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			throw new MissingExternalDataException("Could not find external data");
		}
		return sb.toString();
	}
}
