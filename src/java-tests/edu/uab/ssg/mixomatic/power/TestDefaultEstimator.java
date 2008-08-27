package edu.uab.ssg.mixomatic.power;

import edu.uab.ssg.mixomatic.*;
import edu.uab.ssg.mixomatic.helper.DefaultEstimate;
import junit.framework.TestCase;
import junit.framework.Assert;

/**
 *	@author Jelai Wang
 *	@version $Rev: 83 $ $LastChangedDate: 2008-08-11 14:17:18 -0500 (Mon, 11 Aug 2008) $ $LastChangedBy: jelaiw $ 5/22/06
 */

public final class TestDefaultEstimator extends TestCase {
	public void testBug153() { // See HDB-104 in JIRA.
		// Borrowed numbers from testCR().
		DefaultEstimator estimator = new DefaultEstimator();
		double lambda0 = 0., r = 1.39502, s = 3.64042;
		MixtureModel.Estimate estimate = new DefaultEstimate(lambda0, r, s, new double[12488]);
		double n = estimator.calculateEqualGroupSampleSize(5, 5);
		// Test one extreme, lambda = 0.
		int[] counts = estimator.bootstrap(estimate, n, 10, 0.05);
		Assert.assertEquals(0, counts[0]); // A
		Assert.assertEquals(0, counts[2]); // C
		// Test the other extreme, lambda = 1.
		estimate = new DefaultEstimate(1., r, s, new double[12488]);
		counts = estimator.bootstrap(estimate, n, 10, 0.05);
		Assert.assertEquals(0, counts[1]); // B
		Assert.assertEquals(0, counts[3]); // D
	}
	
	public void testCR() {
		double lambda0 = 0.88092, r = 1.39502, s = 3.64042;
		MixtureModel.Estimate model = new DefaultEstimate(lambda0, r, s, new double[12488]);
		DefaultEstimator estimator = new DefaultEstimator();
		BootstrapEstimator.Estimate estimate = null;
		int N1 = 5, N2 = 5;
		// Extrapolate to bigger sample size.
		estimate = estimator.estimateProportions(model, N1, N2, 10, 0.01);
		Assert.assertEquals(0.630357, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.893865, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.127006, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
		// Extrapolate to smaller sample size and different threshold.
		estimate = estimator.estimateProportions(model, N1, N2, 3, 0.1);
		Assert.assertEquals(0.039072, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.872720, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.029884, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
	}

	public void testMountz() {
		double lambda0 = 0.605, r = 0.539, s = 1.844; // From paper.
		MixtureModel.Estimate model = new DefaultEstimate(lambda0, r, s, new double[12625]);
		DefaultEstimator estimator = new DefaultEstimator();
		int N1 = 3, N2 = 3;
		BootstrapEstimator.Estimate estimate = null;
		// Extrapolate to bigger sample size.
		estimate = estimator.estimateProportions(model, N1, N2, 10, 0.01);
		Assert.assertEquals(0.972166, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.777475, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.564522, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
		// Extrapolate to smaller sample size and different threshold.
		estimate = estimator.estimateProportions(model, N1, N2, 2, 0.1);
		Assert.assertEquals(0.556282, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.630003, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.190483, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
	}

	public void testCD4() {
		double lambda0 = 0.799, r = 0.943, s = 2.496; // From Gary Gadbury.
		MixtureModel.Estimate model = new DefaultEstimate(lambda0, r, s, new double[12548]);
		DefaultEstimator estimator = new DefaultEstimator();
		int N1 = 5, N2 = 5;
		BootstrapEstimator.Estimate estimate = null;
		// Extrapolate to bigger sample size.
		estimate = estimator.estimateProportions(model, N1, N2, 40, 0.00001);
		Assert.assertEquals(0.999928, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.854870, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.325098, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
		// Extrapolate to smaller sample size and different threshold.
		estimate = estimator.estimateProportions(model, N1, N2, 4, 0.1);
		Assert.assertEquals(0.296248, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.810597, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.165829, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
	}

	public void testObesity() {
		double lambda0 = 0.6897603, r = 0.3241158, s = 1.8341034; // From Gary Gadbury.
		MixtureModel.Estimate model = new DefaultEstimate(lambda0, r, s, new double[63149]);
		DefaultEstimator estimator = new DefaultEstimator();
		int N1 = 19, N2 = 19;
		BootstrapEstimator.Estimate estimate = null;
		// Extrapolate to bigger sample size.
		estimate = estimator.estimateProportions(model, N1, N2, 20, 0.0001);
		Assert.assertEquals(0.996688, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.704188, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.073869, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
		// Extrapolate to smaller sample size and different threshold.
		estimate = estimator.estimateProportions(model, N1, N2, 10, 0.1);
		Assert.assertEquals(0.625616, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.761153, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.371744, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
	}

	public void testUnequalSampleSize() { // Modified from Mountz example in paper.
		double lambda0 = 0.605, r = 0.539, s = 1.844; 
		MixtureModel.Estimate model = new DefaultEstimate(lambda0, r, s, new double[12625]);
		DefaultEstimator estimator = new DefaultEstimator();
		int N1 = 10, N2 = 5;
		// Expected value calculated by hand.
		Assert.assertEquals(20./3., estimator.calculateEqualGroupSampleSize(N1, N2), 20./3. * 0.01);
		BootstrapEstimator.Estimate estimate = null;
		// Extrapolate to bigger sample size.
		estimate = estimator.estimateProportions(model, N1, N2, 10, 0.01);
		Assert.assertEquals(0.942083, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.668527, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.248788, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
		// Extrapolate to smaller sample size and different threshold.
		estimate = estimator.estimateProportions(model, N1, N2, 2, 0.1);
		Assert.assertEquals(0.092809, estimate.getTP(), 2. * estimate.getStandardErrorForTP());
		Assert.assertEquals(0.583646, estimate.getTN(), 2. * estimate.getStandardErrorForTN());
		Assert.assertEquals(0.015652, estimate.getEDR(), 2. * estimate.getStandardErrorForEDR());
	}
}	
