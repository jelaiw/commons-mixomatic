package edu.uab.ssg.mixomatic.power.jmsl;

import edu.uab.ssg.mixomatic.power.CombinedEstimator;
import edu.uab.ssg.mixomatic.*;
import junit.framework.TestCase;
import junit.framework.Assert;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 5/22/06
 */

public final class TestBootstrapEstimator extends TestCase {
	public void testBug153() { // See HDB-104 in JIRA.
		// Borrowed numbers from testCR().
		BootstrapEstimator estimator = null;
		double lambda0 = 0., r = 1.39502, s = 3.64042;
		MixtureModel model = new DefaultModel(lambda0, r, s);
		int n1 = 5, n2 = 5;
		int k = 12488;
		// Test one extreme, lambda = 0.
		estimator = new BootstrapEstimator(model, k, n1, n2);
		int[] counts = estimator.bootstrap(10, 0.05);
		Assert.assertEquals(0, counts[0]); // A
		Assert.assertEquals(0, counts[2]); // C
		// Test the other extreme, lambda = 1.
		model = new DefaultModel(1., r, s);
		estimator = new BootstrapEstimator(model, k, n1, n2);
		counts = estimator.bootstrap(10, 0.05);
		Assert.assertEquals(0, counts[1]); // B
		Assert.assertEquals(0, counts[3]); // D
	}
	
	public void testCR() {
		double lambda0 = 0.88092, r = 1.39502, s = 3.64042;
		MixtureModel model = new DefaultModel(lambda0, r, s);
		int n1 = 5, n2 = 5;
		int k = 12488;
		CombinedEstimator estimator = new BootstrapEstimator(model, k, n1, n2);
		CombinedEstimator.Estimates estimate = null;
		// Extrapolate to bigger sample size.
		estimate = estimator.estimateProportions(10, 0.01, 100);
		Assert.assertEquals(0.630357, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.893865, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.127006, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
		// Extrapolate to smaller sample size.
		estimate = estimator.estimateProportions(3, 0.1, 100);
		Assert.assertEquals(0.039072, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.872720, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.029884, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
	}

	public void testMountz() {
		double lambda0 = 0.605, r = 0.539, s = 1.844; // From paper.
		MixtureModel model = new DefaultModel(lambda0, r, s);
		int n1 = 3, n2 = 3;
		int k = 12625;
		CombinedEstimator estimator = new BootstrapEstimator(model, k, n1, n2);
		CombinedEstimator.Estimates estimate = null;
		// Extrapolate to bigger sample size.
		estimate = estimator.estimateProportions(10, 0.01, 100);
		Assert.assertEquals(0.972166, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.777475, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.564522, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
		// Extrapolate to smaller sample size.
		estimate = estimator.estimateProportions(2, 0.1, 100);
		Assert.assertEquals(0.556282, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.630003, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.190483, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
	}

	public void testCD4() {
		double lambda0 = 0.799, r = 0.943, s = 2.496; // From Gary Gadbury.
		MixtureModel model = new DefaultModel(lambda0, r, s);
		int n1 = 5, n2 = 5;
		int k = 12548;
		CombinedEstimator estimator = new BootstrapEstimator(model, k, n1, n2);
		CombinedEstimator.Estimates estimate = null;
		// Extrapolate to bigger sample size.
		estimate = estimator.estimateProportions(40, 0.00001, 100);
		Assert.assertEquals(0.999928, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.854870, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.325098, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
		// Extrapolate to smaller sample size.
		estimate = estimator.estimateProportions(4, 0.1, 100);
		Assert.assertEquals(0.296248, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.810597, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.165829, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
	}

	public void testObesity() {
		double lambda0 = 0.6897603, r = 0.3241158, s = 1.8341034; // From Gary Gadbury.
		MixtureModel model = new DefaultModel(lambda0, r, s);
		int n1 = 19, n2 = 19;
		int k = 63149;
		CombinedEstimator estimator = new BootstrapEstimator(model, k, n1, n2);
		CombinedEstimator.Estimates estimate = null;
		// Extrapolate to bigger sample size.
		estimate = estimator.estimateProportions(20, 0.0001, 100);
		Assert.assertEquals(0.996688, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.704188, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.073869, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
		// Extrapolate to smaller sample size.
		estimate = estimator.estimateProportions(10, 0.1, 100);
		Assert.assertEquals(0.625616, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.761153, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.371744, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
	}

	public void testUnequalSampleSize() { // Modified from Mountz example in paper.
		double lambda0 = 0.605, r = 0.539, s = 1.844; 
		MixtureModel model = new DefaultModel(lambda0, r, s);
		int n1 = 10, n2 = 5; 
		int k = 12625;
		CombinedEstimator estimator = new BootstrapEstimator(model, k, n1, n2);
		CombinedEstimator.Estimates estimate = null;
		// Extrapolate to bigger sample size.
		estimate = estimator.estimateProportions(10, 0.01, 100);
		Assert.assertEquals(0.942083, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.668527, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.248788, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
		// Extrapolate to smaller sample size.
		estimate = estimator.estimateProportions(2, 0.1, 100);
		Assert.assertEquals(0.092809, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.583646, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.015652, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
	}
}	
