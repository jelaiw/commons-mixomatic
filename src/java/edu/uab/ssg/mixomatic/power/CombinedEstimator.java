package edu.uab.ssg.mixomatic.power;

/**
 * This interface defines an abstraction for a "combined" estimator, that
 * is, an estimator for the proportions of interest, true positive (TP),
 * true negative (TN), and expected discovery rate (EDR) described in
 * the paper at http://dx.doi.org/10.1191/0962280204sm369ra.
 *
 * @author Jelai Wang
 * @version $Rev$ $LastChangedDate$ $LastChangedBy$ 5/22/2006
 */

public interface CombinedEstimator {
	/**
	 * Estimate the proportions of interest, TP, TN, and EDR at the
	 * user-specified sample size and threshold.
	 *
	 * @param newN The sample size at which to calculate the estimates.
	 * Called "n*" in the reference paper. Also known as the "extrapolated"
	 * sample size.
	 * @param threshold The threshold at which to calculate the estimates.
	 * Called &tau; in the reference paper.
	 * @param numberOfIterations The number of bootstrap iterations to perform.
	 * Called "M" in the reference paper and set to M = 100 in the three 
	 * examples presented therein.
	 */
	Estimates estimateProportions(int newN, double threshold, int numberOfIterations);

	/**
	 * An instance of this class represents the combined point and standard
	 * error estimates for TP, TN, and EDR.
	 */
	public interface Estimates {
		/**
		 * Returns the equal group sample size "n".
		 */
		double getEqualGroupSampleSize();

		/**
		 * Returns the "extrapolated" sample size at which the estimates
		 * were calculated. 
		 * Also known as "newN".
		 */
		int getExtrapolatedSampleSize();

		/**
		 * Returns the threshold at which the estimates were calculated.
		 */
		double getThreshold();

		/**
		 * Returns the point estimate for the proportion of true positives (TP).
		 */
		double getTP();

		/**
		 * Returns the point estimate for the proportion of true negatives (TN).
		 */
		double getTN();

		/**
		 * Returns the point estimate for the expected discovery rate (EDR).
		 */
		double getEDR();

		/**
		 * Returns the standard error for the proportion of true positives (TP).
		 */
		double getStandardErrorForTP();

		/**
		 * Returns the standard error for the proportion of true negatives (TN).
		 */
		double getStandardErrorForTN();

		/**
		 * Returns the standard error for the expected discovery rate (EDR).
		 */
		double getStandardErrorForEDR();
	}
}
