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
		Assert.assertTrue(forest.inBorders(5));
	}

	@Test
	public void inBordersUpperEdgeTest()
	{
		Assert.assertTrue(forest.inBorders(9));
		Assert.assertFalse(forest.inBorders(10));
	}
	
	@Test
	public void inBordersLowerEdgeTest()
	{
		Assert.assertTrue(forest.inBorders(0));
		Assert.assertFalse(forest.inBorders(-1));
	}
}
