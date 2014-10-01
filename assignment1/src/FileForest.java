import java.io.*;

public class FileForest
{

    public FileForest()
    {
    }

    public static ForestItem[][] fileRead(int i, int j)
    {
        ForestItem aforestitem[][] = new ForestItem[i][j];
        Object obj = null;
        Object obj1 = null;
        try
        {
            FileReader filereader = new FileReader("input.txt");
            BufferedReader bufferedreader = new BufferedReader(filereader);
            String s = bufferedreader.readLine();
            int k = 0;
            for(; s != null; s = bufferedreader.readLine())
            {
                for(int l = 0; l < j; l++)
                {
                    char c = s.charAt(l);
                    if(c == ' ')
                    {
                        aforestitem[k][l] = null;
                        continue;
                    }
                    if(c == 'E')
                        aforestitem[k][l] = new ForestItem(c);
                    else
                        aforestitem[k][l] = new ForestItem(c);
                }

                k++;
            }

        }
        catch(FileNotFoundException filenotfoundexception)
        {
            System.out.println("File called input.txt not in the current directory");
        }
        catch(IOException ioexception)
        {
            System.out.println("General file input error occured.");
            ioexception.printStackTrace();
        }
        return aforestitem;
    }

    private static final char EMPTY = 32;
    private static final char EXIT = 69;
    private static final int SIZE = 10;
}
