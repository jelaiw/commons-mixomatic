package edu.uab.ssg.mixomatic.jsci;

import edu.uab.ssg.mixomatic.ProbabilityDensityFunction;
import edu.uab.ssg.mixomatic.MixtureModel;
import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.Random;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 4/4/06
 */

public final class TestDefaultPDF extends TestCase {
	public void testNaively() {
		double lambda0 = 0.5, r = 1.5, s = 2.75;
		ProbabilityDensityFunction f = new DefaultPDF(lambda0, r, s);
		double x = 0.05;
		Assert.assertEquals(1.09408 , f.evaluate(x), 1.09408 * 0.01);
		MixtureModel model = f.getModel();
		Assert.assertTrue(Double.compare(0.5, model.getLambda0()) == 0);
		Assert.assertTrue(Double.compare(1.5, model.getR()) == 0);
		Assert.assertTrue(Double.compare(2.75, model.getS()) == 0);
	}

	public void testBadArgs() {
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
			f.evaluate(-0.01);
			Assert.fail("x is out of range!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			ProbabilityDensityFunction f = new DefaultPDF(0.2, 1., 1.);
			f.evaluate(5.02);
			Assert.fail("x is out of range!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
	}

	/*
	public void testPerformance() {
		double lambda0 = 0.8, r = 1.5, s = 2.75;
		// Create a set of random x values on the interval [0,1].
		Random random = new Random();
		double[] x = new double[10000];
		for (int i = 0; i < x.length; i++) {
			x[i] = random.nextDouble();
		}

		long start = System.currentTimeMillis();
		double[] baseline = evaluate(new DefaultPDF(lambda0, r, s), x);
		System.out.println(System.currentTimeMillis() - start);
	}

	private double[] evaluate(ProbabilityDensityFunction f, double[] x) {
		double[] tmp = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			tmp[i] = f.evaluate(x[i]);
		}
		return tmp;
	}
	*/
}
