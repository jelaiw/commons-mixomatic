package edu.uab.ssg.mixomatic.jmsl;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 4/6/06
 */

public final class TestConfiguration extends TestCase {
	public void testDefaultConfiguration() throws IOException {
		BoundedOptimizer.Configuration config = new DefaultConfiguration();
//		System.out.println(config);

		BoundedOptimizer.LowerBounds lb = config.getLowerBounds();
		Assert.assertEquals(0., lb.getLambda0(), 0.01);
		Assert.assertEquals(1.7e-8, lb.getR(), 1.7e-8 * 0.01);
		Assert.assertEquals(1.7e-8, lb.getS(), 1.7e-8 * 0.01);

		BoundedOptimizer.UpperBounds ub = config.getUpperBounds();
		Assert.assertEquals(1., ub.getLambda0(), 1. * 0.01);
		Assert.assertEquals(1.79e308, ub.getR(), 1.79e308 * 0.01);
		Assert.assertEquals(1.79e308, ub.getS(), 1.79e308 * 0.01);

		BoundedOptimizer.StartingPoint guess = config.findStartingPoint(getPValues());
		Assert.assertEquals(0.9, guess.getLambda0(), 0.9 * 0.01);
		Assert.assertEquals(1.5, guess.getR(), 1.5 * 0.01);
		Assert.assertEquals(3.75, guess.getS(), 3.75 * 0.01);
	}

	public void testBadArgsDefaultConfiguration() {
		BoundedOptimizer.Configuration config = new DefaultConfiguration();
		try {
			config.findStartingPoint(null);
			Assert.fail("sample data can't be null!");
		}
		catch (NullPointerException e) {
			Assert.assertTrue(true);
		}

		try {
			config.findStartingPoint(new double[0]);
			Assert.fail("sample data can't be empty!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
	}

	public void testRestrictedConfiguration() throws IOException {
		BoundedOptimizer.Configuration config = new RestrictedConfiguration();
//		System.out.println(config);

		BoundedOptimizer.LowerBounds lb = config.getLowerBounds();
		Assert.assertEquals(0., lb.getLambda0(), 0.01);
		Assert.assertEquals(1.7e-8, lb.getR(), 1.7e-8 * 0.01);
		Assert.assertEquals(1. + 1.7e-8, lb.getS(), (1. + 1.7e-8) * 0.01);

		BoundedOptimizer.UpperBounds ub = config.getUpperBounds();
		Assert.assertEquals(1., ub.getLambda0(), 1. * 0.01);
		Assert.assertEquals(1. - 1.7e-8, ub.getR(), (1. - 1.7e-8) * 0.01);
		Assert.assertEquals(1.79e308, ub.getS(), 1.79e308 * 0.01);

		BoundedOptimizer.StartingPoint guess = config.findStartingPoint(getPValues());
		Assert.assertEquals(0.9, guess.getLambda0(), 0.9 * 0.01);
		Assert.assertEquals(0.9, guess.getR(), 0.9 * 0.01);
		Assert.assertEquals(2.25, guess.getS(), 2.25 * 0.01);
	}

	public void testBadArgsRestrictedConfiguration() {
		BoundedOptimizer.Configuration config = new RestrictedConfiguration();
		try {
			config.findStartingPoint(null);
			Assert.fail("sample data can't be null!");
		}
		catch (NullPointerException e) {
			Assert.assertTrue(true);
		}

		try {
			config.findStartingPoint(new double[0]);
			Assert.fail("sample data can't be empty!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
	}

	public double[] getPValues() throws IOException {
		double[] tmp = new double[12488];
		BufferedReader in = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("edu/uab/ssg/mixomatic/pvalues.txt")));
		for (int i = 0; i < tmp.length; i++) {
			String text = in.readLine();
			tmp[i] = Double.parseDouble(text);
		}
		return tmp;
	}
}
