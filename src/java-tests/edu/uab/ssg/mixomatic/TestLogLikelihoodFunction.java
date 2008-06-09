package edu.uab.ssg.mixomatic;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 4/4/06
 */

public final class TestLogLikelihoodFunction extends TestCase {
	public void testNaively() throws IOException {
		double[] pValues = new double[12488];
		BufferedReader in = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("edu/uab/ssg/mixomatic/pvalues.txt")));
		for (int i = 0; i < pValues.length; i++) {
			String text = in.readLine();
			pValues[i] = Double.parseDouble(text);
		}

		LogLikelihoodFunction f = new LogLikelihoodFunction(0.8, 1.5, 2.5);
		double likelihood = f.getValue(pValues);
		// Calculated in code at T0.  This isn't expected to change over time.
		Assert.assertEquals(39.976442, f.getValue(pValues), 39.976442 * 0.01);
	}

	public void testBadArgs() {
		try {
			LogLikelihoodFunction f = new LogLikelihoodFunction(-0.05, 1., 1.);
			Assert.fail("lambda0 is out of range!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			LogLikelihoodFunction f = new LogLikelihoodFunction(1.01, 1., 1.);
			Assert.fail("lambda0 is out of range!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			LogLikelihoodFunction f = new LogLikelihoodFunction(0.8, -1., 1.);
			Assert.fail("r is out of range!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			LogLikelihoodFunction f = new LogLikelihoodFunction(0.8, 1., -1.);
			Assert.fail("s is out of range!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			LogLikelihoodFunction f = new LogLikelihoodFunction(0.2, 1., 1.);
			f.getValue(null);
			Assert.fail("x can't be null.");
		}
		catch (NullPointerException e) {
			Assert.assertTrue(true);
		}

		try {
			LogLikelihoodFunction f = new LogLikelihoodFunction(0.2, 1., 1.);
			f.getValue(new double[0]);
			Assert.fail("x can't be empty.");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
	}
}
