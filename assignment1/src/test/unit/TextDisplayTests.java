package test.unit;

import main.TextDisplay;

import org.junit.Assert;
import org.junit.Test;

public class TextDisplayTests 
{
	
	@Test
	public void wrapStringTest()
	{
		String actual = "Hello world, my name is Albert Chu. I am a programmer.";
		String expected = "Hello world,\nmy name is\nAlbert Chu.\nI am a\nprogrammer.";
		Assert.assertEquals(expected, TextDisplay.wrapString(actual, "\n", 12));
	}
	
	@Test
	public void repeatCharTest()
	{
		Assert.assertEquals("----------", TextDisplay.repeatChar('-', 10));
	}
}
