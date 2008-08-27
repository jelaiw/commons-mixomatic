package edu.uab.ssg.mixomatic.power;

import edu.uab.ssg.mixomatic.MixtureModel;

/**
 * This interface defines an abstraction for a bootstrap estimator of
 * the proportions of interest, true positive (TP), true negative (TN), 
 * and expected discovery rate (EDR), described in the paper at 
 * http://dx.doi.org/10.1191/0962280204sm369ra.
 *
 * @author Jelai Wang
 */

public interface BootstrapEstimator {
	/**
	 * Estimate the proportions of interest, TP, TN, and EDR at the
	 * user-specified sample size and significance level.
	 * Assumes the client programmer conducted many, valid two-group
	 * hypothesis tests to obtain a sample distribution of p-values (from 
	 * those tests) and fitted a mixture model, using a MixtureModel.Estimator,
	 * to this distribution of p-values.
	 * If N1 != N2, then an "equivalent" equal group sample size, called "n"
	 * in the paper, is calculated.
	 *
	 * @param estimate The mixture model estimate.
	 * @param N1 The sample size of the first group in the two-group
	 * hypothesis test.
	 * @param N2 The sample size of the second group in the two-group
	 * hypothesis test.
	 * @param n_ The sample size at which to calculate the estimates.
	 * Called "n*" in the paper.
	 * @param significanceLevel The signficance level at which to calculate 
	 * the estimates. This is the threshold called &tau; in the paper.
	 */
	Estimate estimateProportions(MixtureModel.Estimate estimate, int N1, int N2, int n_, double significanceLevel);

	/**
	 * The point and standard error estimates for TP, TN, and EDR.
	 */
	public interface Estimate {
		/**
		 * Return the sample size at which the estimates were calculated.
		 */
		int getSampleSize();

		/**
		 * Return the significance level at which the estimates were calculated.
		 */
		double getSignificanceLevel();

		/**
		 * Return the point estimate for the proportion of true positives (TP).
		 */
		double getTP();

		/**
		 * Return the point estimate for the proportion of true negatives (TN).
		 */
		double getTN();

		/**
		 * Return the point estimate for the expected discovery rate (EDR).
		 */
		double getEDR();

		/**
		 * Return the standard error for the proportion of true positives (TP).
		 */
		double getStandardErrorForTP();

		/**
		 * Return the standard error for the proportion of true negatives (TN).
		 */
		double getStandardErrorForTN();

		/**
		 * Return the standard error for the expected discovery rate (EDR).
		 */
		double getStandardErrorForEDR();
	}

	public interface PValueAdjuster {
		double adjustPValue(double pvalue, double n, int n_);
	}

	public interface RandomNumberGenerator {
		int nextBinomial(int n, double p);
		double nextUniform();
		double nextBeta(double r, double s);
	}
}
