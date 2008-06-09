package edu.uab.ssg.mixomatic;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.Random;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 4/4/06
 */

public final class TestProbabilityDensityFunction extends TestCase {
	public void testFastVersusDefaultPDF() {
		double lambda0 = 0.8, r = 1.5, s = 2.75;
		// Create a set of random x values on the interval [0,1].
		Random random = new Random();
		double[] x = new double[10000];
		for (int i = 0; i < x.length; i++) {
			x[i] = random.nextDouble();
		}

		long start = 0L;
		// Run the default implementation.
		start = System.currentTimeMillis();
		double[] baseline = getValues(new DefaultPDF(lambda0, r, s), x);
		System.out.println(System.currentTimeMillis() - start);
		// Run the fast implementation.
		start = System.currentTimeMillis();
		double[] fast = getValues(new FastPDF(lambda0, r, s), x);
		System.out.println(System.currentTimeMillis() - start);

		// Compare the results.
		for (int i = 0; i < baseline.length; i++) {
			Assert.assertEquals(baseline[i], fast[i], baseline[i] * 0.01);
		}
	}

	private double[] getValues(ProbabilityDensityFunction f, double[] x) {
		double[] tmp = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			tmp[i] = f.getValue(x[i]);
		}
		return tmp;
	}
	
	public void testFastPDF() {
		double lambda0 = 0.5, r = 1.5, s = 2.75;
		ProbabilityDensityFunction f = new FastPDF(lambda0, r, s);
		double x = 0.05;
		Assert.assertEquals(1.09408 , f.getValue(x), 1.09408 * 0.01);
	}

	public void testBadArgsFastPDF() {
		try {
			ProbabilityDensityFunction f = new FastPDF(-0.05, 1., 1.);
			Assert.fail("lambda0 is out of range!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			ProbabilityDensityFunction f = new FastPDF(1.01, 1., 1.);
			Assert.fail("lambda0 is out of range!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			ProbabilityDensityFunction f = new FastPDF(0.8, -1., 1.);
			Assert.fail("r is out of range!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			ProbabilityDensityFunction f = new FastPDF(0.8, 1., -1.);
			Assert.fail("s is out of range!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			ProbabilityDensityFunction f = new FastPDF(0.2, 1., 1.);
			f.getValue(-0.01);
			Assert.fail("x is out of range!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			ProbabilityDensityFunction f = new FastPDF(0.2, 1., 1.);
			f.getValue(5.02);
			Assert.fail("x is out of range!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
	}

	public void testDefaultPDF() {
		double lambda0 = 0.5, r = 1.5, s = 2.75;
		ProbabilityDensityFunction f = new DefaultPDF(lambda0, r, s);
		double x = 0.05;
		Assert.assertEquals(1.09408 , f.getValue(x), 1.09408 * 0.01);
	}

	public void testBadArgsDefaultPDF() {
		try {
			ProbabilityDensityFunction f = new DefaultPDF(-0.05, 1., 1.);
			Assert.fail("lambda0 is out of range!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			ProbabilityDensityFunction f = new DefaultPDF(1.01, 1., 1.);
			Assert.fail("lambda0 is out of range!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			ProbabilityDensityFunction f = new DefaultPDF(0.8, -1., 1.);
			Assert.fail("r is out of range!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			ProbabilityDensityFunction f = new DefaultPDF(0.8, 1., -1.);
			Assert.fail("s is out of range!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			ProbabilityDensityFunction f = new DefaultPDF(0.2, 1., 1.);
			f.getValue(-0.01);
			Assert.fail("x is out of range!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			ProbabilityDensityFunction f = new DefaultPDF(0.2, 1., 1.);
			f.getValue(5.02);
			Assert.fail("x is out of range!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
	}
}
