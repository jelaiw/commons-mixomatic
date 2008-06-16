package edu.uab.ssg.mixomatic.jmsl;

import edu.uab.ssg.mixomatic.*;
import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.Random;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 4/4/06
 */

public final class TestDefaultPDF extends TestCase {
	public void testNaively() {
		MixtureModel model = new DefaultModel(0.5, 1.5, 2.75);
		ProbabilityDensityFunction function = new DefaultPDF();
		double x = 0.05;
		Assert.assertEquals(1.09408 , function.evaluate(model, x), 1.09408 * 0.01);
	}

	public void testBadArgs() {
		MixtureModel model = new DefaultModel(0.5, 1.5, 2.75);
		try {
			ProbabilityDensityFunction function = new DefaultPDF();
			function.evaluate(model, -0.01);
			Assert.fail("x is out of range!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			ProbabilityDensityFunction function = new DefaultPDF();
			function.evaluate(model, 5.02);
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
