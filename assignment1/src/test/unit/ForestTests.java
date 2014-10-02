package test.unit;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import main.Forest;

public class ForestTests
{
	private Forest forest;

	@Before
	public void setup()
	{
		forest = new Forest();
	}
	
	@Test
	public void inBordersTest()
	{
		Assert.assertEquals(true, forest.inBorders(5));
	}

	@Test
	public void inBordersUpperEdgeTest()
	{
		Assert.assertEquals(true, forest.inBorders(9));
		Assert.assertEquals(false, forest.inBorders(10));
	}
	
	@Test
	public void inBordersLowerEdgeTest()
	{
		Assert.assertEquals(true, forest.inBorders(0));
		Assert.assertEquals(false, forest.inBorders(-1));
	}
}
