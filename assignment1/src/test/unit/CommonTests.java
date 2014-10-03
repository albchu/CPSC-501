package test.unit;

import org.junit.Test;

import junit.framework.Assert;
import main.utilities.Common;
import main.utilities.MissingExternalDataException;

public class CommonTests
{
	private String expectedFile = "src/test/resources/testfile";
	private String expectedFileBad = "src/test/resources/testfilewhichdoesntexist";

	@Test
	public void readFromFileTest()
	{
		Assert.assertTrue(Common.readFromFile(expectedFile).contains("Test Passed"));
	}
	
	@Test(expected = MissingExternalDataException.class)
	public void readFromFileDNETest()
	{
		Assert.assertTrue(Common.readFromFile(expectedFileBad).isEmpty());
	}
}
