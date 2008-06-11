package edu.uab.ssg.mixomatic;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;
import java.util.*;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 4/10/06
 */

public final class TestEstimator extends TestCase {
	public void testBug157() throws MixomaticException, IOException { // See HDB-105 in JIRA.
		OptimizationModel model = new RestrictedModel();
		Estimator estimator = new Estimator(new RestrictedModel());
		double[] pValues = getPValues("edu/uab/ssg/mixomatic/npr_columbia_pvalues.txt");
		Estimator.Estimate estimate = estimator.getEstimate(pValues);
		Assert.assertEquals(1.0, estimate.getLambda0(), 1.0 * 0.01);
		Assert.assertEquals(0.999999983, estimate.getR(), 0.999999983 * 0.01);
		Assert.assertEquals(1.000000017, estimate.getS(), 1.000000017 * 0.01);
		Assert.assertEquals(model, estimate.getModel());
		Assert.assertTrue(Arrays.equals(pValues, estimate.getPValues()) && !(pValues == estimate.getPValues()));
	}
	
	public void testDefaultModel() throws MixomaticException, IOException {
		OptimizationModel model = new DefaultModel();
		Estimator estimator = new Estimator(model);
		Assert.assertEquals(model, estimator.getModel());

		double[] pValues = getPValues("edu/uab/ssg/mixomatic/pvalues.txt");
		Estimator.Estimate estimate = estimator.getEstimate(pValues);
		Assert.assertEquals(0.880918, estimate.getLambda0(), 0.880918 * 0.01);
		Assert.assertEquals(1.395028, estimate.getR(), 1.395028 * 0.01);
		Assert.assertEquals(3.640435, estimate.getS(), 3.640435 * 0.01);
		Assert.assertEquals(model, estimate.getModel());
		Assert.assertTrue(Arrays.equals(pValues, estimate.getPValues()) && !(pValues == estimate.getPValues()));
	}

	public void testRestrictedModel() throws MixomaticException, IOException {
		OptimizationModel model = new RestrictedModel();
		Estimator estimator = new Estimator(model);
		Assert.assertEquals(model, estimator.getModel());

		double[] pValues = getPValues("edu/uab/ssg/mixomatic/pvalues.txt");
		Estimator.Estimate estimate = estimator.getEstimate(pValues);
		Assert.assertEquals(0.848136, estimate.getLambda0(), 0.848136 * 0.01);
		Assert.assertEquals(1., estimate.getR(), 1.* 0.01);
		Assert.assertEquals(2.094765, estimate.getS(), 2.094765 * 0.01);
		Assert.assertEquals(model, estimate.getModel());
		Assert.assertTrue(Arrays.equals(pValues, estimate.getPValues()) && !(pValues == estimate.getPValues()));
	}

	public void testBadArguments() throws MixomaticException {
		try {
			Estimator estimator = new Estimator(null);
			Assert.fail("model can't be null!");
		}
		catch (NullPointerException e) {
			Assert.assertTrue(true);
		}

		try {
			Estimator estimator = new Estimator(new DefaultModel());
			estimator.getEstimate(null);
			Assert.fail("p-values can't be null!");
		}
		catch (NullPointerException e) {
			Assert.assertTrue(true);
		}

		try {
			Estimator estimator = new Estimator(new DefaultModel());
			estimator.getEstimate(new double[0]);
			Assert.fail("p-values can't be empty!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			Estimator estimator = new Estimator(new RestrictedModel());
			estimator.getEstimate(new double[] { 0.1, -0.5, 0.5 });
			Assert.fail("at least one p-value out of bounds!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
	}

	public void testNaN() throws MixomaticException, IOException {
		OptimizationModel model = new DefaultModel();
		Estimator estimator = new Estimator(model);
		Assert.assertEquals(model, estimator.getModel());
		// User must remove Double.NaN values from input array first.
		try {
			estimator.getEstimate(new double[] { 0.01, Double.NaN, 0.05 });
			Assert.fail("p-values can't contain Double.NaN values!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
	}

	public void testException() throws IOException {
		OptimizationModel model = new DefaultModel();
		Estimator estimator = new Estimator(model);
		Assert.assertEquals(model, estimator.getModel());

		double[] pValues = getPValues("edu/uab/ssg/mixomatic/exon4.txt"); // LOOK!
		try {
			estimator.getEstimate(pValues);
			Assert.fail("this set of p-values expected not to optimize!");
		}
		catch (MixomaticException e) {
			Assert.assertEquals(model, e.getModel());
			Assert.assertTrue(Arrays.equals(pValues, e.getPValues()));
		}
	}

	private double[] getPValues(String resource) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(resource)));
		List<Double> list = new ArrayList<Double>();
		String line = null;
		while ((line = in.readLine()) != null) {
			list.add(Double.valueOf(line));
		}
		return asArray(list);
	}

	private double[] asArray(List<Double> list) {
		double[] tmp = new double[list.size()];	
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = list.get(i).doubleValue();
		}
		return tmp;
	}
}	
