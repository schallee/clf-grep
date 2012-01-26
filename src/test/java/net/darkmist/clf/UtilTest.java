package net.darkmist.clf;

import java.util.Arrays;
import java.util.Comparator;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UtilTest extends TestCase
{
	private static final Class<UtilTest> CLASS = UtilTest.class;
	@SuppressWarnings("unused")
	private static final String CLASS_NAME = CLASS.getName();
	@SuppressWarnings("unused")
	private static final Log logger = LogFactory.getLog(CLASS);
	private static final Comparator<Integer> comparator = Util.getComparableComparator();

	@Override
	protected void setUp() throws Exception
	{
	}

	public void testAThenB() throws Exception
	{
		Integer[] a = new Integer[]{1,2,3,4,5};
		Integer[] b = new Integer[]{6,7,8,9};
		Integer[] expected = new Integer[]{1,2,3,4,5,6,7,8,9};
		Integer[] actual = new Integer[a.length+b.length];

		Util.merge(a,b,actual,comparator);
		assertTrue(Arrays.equals(actual,expected));
	}

	public void testBThenA() throws Exception
	{
		Integer[] a = new Integer[]{6,7,8,9};
		Integer[] b = new Integer[]{1,2,3,4,5};
		Integer[] expected = new Integer[]{1,2,3,4,5,6,7,8,9};
		Integer[] actual = new Integer[a.length+b.length];

		Util.merge(a,b,actual,comparator);
		assertTrue(Arrays.equals(actual,expected));
	}

	public void testInterleavedAB() throws Exception
	{
		Integer[] a = new Integer[]{1,3,5,7,9};
		Integer[] b = new Integer[]{2,4,6,8};
		Integer[] expected = new Integer[]{1,2,3,4,5,6,7,8,9};
		Integer[] actual = new Integer[a.length+b.length];

		Util.merge(a,b,actual,comparator);
		assertTrue(Arrays.equals(actual,expected));
	}

	public void testInterleavedBA() throws Exception
	{
		Integer[] b = new Integer[]{1,3,5,7,9};
		Integer[] a = new Integer[]{2,4,6,8};
		Integer[] expected = new Integer[]{1,2,3,4,5,6,7,8,9};
		Integer[] actual = new Integer[a.length+b.length];

		Util.merge(a,b,actual,comparator);
		assertTrue(Arrays.equals(actual,expected));
	}

	public void testTailInterleavedAB() throws Exception
	{
		Integer[] a = new Integer[]{1,2,3,5,7,9};
		Integer[] b = new Integer[]{4,6,8};
		Integer[] expected = new Integer[]{1,2,3,4,5,6,7,8,9};
		Integer[] actual = new Integer[a.length+b.length];

		Util.merge(a,b,actual,comparator);
		assertTrue(Arrays.equals(actual,expected));
	}

	public void testTailInterleavedBA() throws Exception
	{
		Integer[] b = new Integer[]{1,2,3,5,7,9};
		Integer[] a = new Integer[]{4,6,8};
		Integer[] expected = new Integer[]{1,2,3,4,5,6,7,8,9};
		Integer[] actual = new Integer[a.length+b.length];

		Util.merge(a,b,actual,comparator);
		assertTrue(Arrays.equals(actual,expected));
	}

	@Override
	protected void tearDown()
	{
	}

	public static Test suite()
	{
		return new TestSuite(CLASS);
	}

	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(suite());
	}
}
