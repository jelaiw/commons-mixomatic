package edu.uab.ssg.mixomatic.jmsl;

import edu.uab.ssg.mixomatic.*;
import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;
import java.util.*;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 4/10/06
 */

public final class TestBoundedOptimizer extends TestCase {
	public void testBug9() throws MixomaticException, IOException { // See HDB-9 in JIRA. This p-value distribution has a funny mound-shape.
		MixtureModel.Estimator estimator = new BoundedOptimizer();
		double[] pValues = getPValues("edu/uab/ssg/mixomatic/pvalues6.txt");
		MixtureModel.Estimate estimate = estimator.estimateParameters(pValues);
		Assert.assertEquals(0.575266479, estimate.getLambda0(), 0.575266479 * 0.01);
		Assert.assertEquals(5.545681846, estimate.getR(), 5.545681846 * 0.01);
		Assert.assertEquals(8.152365616, estimate.getS(), 8.152365616 * 0.01);
		Assert.assertTrue(Arrays.equals(pValues, estimate.getSample()) && !(pValues == estimate.getSample()));
	}

	public void testBug157() throws MixomaticException, IOException { // See HDB-105 in JIRA.
		MixtureModel.Estimator estimator = new BoundedOptimizer(BoundedOptimizer.RESTRICTED);
		double[] pValues = getPValues("edu/uab/ssg/mixomatic/npr_columbia_pvalues.txt");
		MixtureModel.Estimate estimate = estimator.estimateParameters(pValues);
		Assert.assertEquals(1.0, estimate.getLambda0(), 1.0 * 0.01);
		Assert.assertEquals(0.999999983, estimate.getR(), 0.999999983 * 0.01);
		Assert.assertEquals(1.000000017, estimate.getS(), 1.000000017 * 0.01);
		Assert.assertTrue(Arrays.equals(pValues, estimate.getSample()) && !(pValues == estimate.getSample()));
	}
	
	public void testDefaultModel() throws MixomaticException, IOException {
		MixtureModel.Estimator estimator = new BoundedOptimizer();
		double[] pValues = getPValues("edu/uab/ssg/mixomatic/pvalues.txt");
		MixtureModel.Estimate estimate = estimator.estimateParameters(pValues);
		Assert.assertEquals(0.880918, estimate.getLambda0(), 0.880918 * 0.01);
		Assert.assertEquals(1.395028, estimate.getR(), 1.395028 * 0.01);
		Assert.assertEquals(3.640435, estimate.getS(), 3.640435 * 0.01);
		Assert.assertTrue(Arrays.equals(pValues, estimate.getSample()) && !(pValues == estimate.getSample()));
	}

	public void testRestrictedModel() throws MixomaticException, IOException {
		MixtureModel.Estimator estimator = new BoundedOptimizer(BoundedOptimizer.RESTRICTED);
		double[] pValues = getPValues("edu/uab/ssg/mixomatic/pvalues.txt");
		MixtureModel.Estimate estimate = estimator.estimateParameters(pValues);
		Assert.assertEquals(0.848136, estimate.getLambda0(), 0.848136 * 0.01);
		Assert.assertEquals(1., estimate.getR(), 1.* 0.01);
		Assert.assertEquals(2.094765, estimate.getS(), 2.094765 * 0.01);
		Assert.assertTrue(Arrays.equals(pValues, estimate.getSample()) && !(pValues == estimate.getSample()));
	}

	public void testBadArguments() throws MixomaticException {
		try {
			MixtureModel.Estimator estimator = new BoundedOptimizer(null);
			Assert.fail("configuration can't be null!");
		}
		catch (NullPointerException e) {
			Assert.assertTrue(true);
		}

		try {
			MixtureModel.Estimator estimator = new BoundedOptimizer();
			estimator.estimateParameters(null);
			Assert.fail("p-values can't be null!");
		}
		catch (NullPointerException e) {
			Assert.assertTrue(true);
		}

		try {
			MixtureModel.Estimator estimator = new BoundedOptimizer();
			estimator.estimateParameters(new double[0]);
			Assert.fail("p-values can't be empty!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			MixtureModel.Estimator estimator = new BoundedOptimizer(BoundedOptimizer.RESTRICTED);
			estimator.estimateParameters(new double[] { 0.1, -0.5, 0.5 });
			Assert.fail("at least one p-value out of bounds!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
	}

	public void testNaN() throws MixomaticException, IOException {
		MixtureModel.Estimator estimator = new BoundedOptimizer();
		// User must remove Double.NaN values from input array first.
		try {
			estimator.estimateParameters(new double[] { 0.01, Double.NaN, 0.05 });
			Assert.fail("p-values can't contain Double.NaN values!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
	}

	public void testException() throws IOException {
		MixtureModel.Estimator estimator = new BoundedOptimizer();
		double[] pValues = getPValues("edu/uab/ssg/mixomatic/exon4.txt"); // LOOK!
		try {
			estimator.estimateParameters(pValues);
			Assert.fail("this set of p-values expected not to optimize!");
		}
		catch (MixomaticException e) {
			Assert.assertTrue(Arrays.equals(pValues, e.getSample()));
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
