package test.unit;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import main.CommandProcessor;
import main.Mode;

public class CommandProcessorTests
{
	private CommandProcessor cp;
	
	@Before
	public void setup()
	{
		cp = new CommandProcessor();
	}
	
	@Test
	public void cheatToggleTest()
	{
		boolean originalMode = Mode.cheat;
		cp.cheatToggle();
		Assert.assertEquals(!originalMode, Mode.cheat);
	}
	
	@Test
	public void debugToggleTest()
	{
		boolean originalMode = Mode.debug;
		cp.debugToggle();
		Assert.assertEquals(!originalMode, Mode.debug);
	}
}
