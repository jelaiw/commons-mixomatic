package edu.uab.ssg.mixomatic.power;

import edu.uab.ssg.mixomatic.MixtureModel;

/**
 * This interface defines an abstraction for a bootstrap estimator of
 * the proportions of interest, true positive (TP), true negative (TN), 
 * and expected discovery rate (EDR), described in the paper.
 *
 * <p>Gadbury GL, Page GP, Edwards J, Kayo T, Weindruch R, Permana PA,
 * Mountz J, Allison DB. Power and Sample Size Estimation in High Dimensional 
 * Biology. <i>Stat Meth Med Res</i> 2004, 13:325-338.</p>
 * <p>DOI: <a href="http://dx.doi.org/10.1191/0962280204sm369ra">10.1191/0962280204sm369ra</a> (<a href="http://smm.sagepub.com/cgi/content/short/13/4/325">Alternative link</a>).</p>
 *
 * @author Jelai Wang
 */

public interface BootstrapEstimator {
	/**
	 * Estimate the proportions of interest, TP, TN, and EDR, at the
	 * user-specified sample size and significance level.
	 * Assumes the client programmer has conducted many, valid two-group
	 * hypothesis tests to obtain a sample distribution of p-values, from 
	 * those tests, and fitted a mixture model, using a MixtureModel.Estimator,
	 * to this distribution of p-values.
	 * If N1 != N2, then an equal group sample size, called n in the paper, 
	 * is calculated and used in subsequent calculations.
	 *
	 * @param estimate The mixture model estimate.
	 * @param N1 The sample size of the first group in the two-group
	 * hypothesis test.
	 * @param N2 The sample size of the second group in the two-group
	 * hypothesis test.
	 * @param n_ The sample size at which to calculate the estimates.
	 * Called n* in the paper.
	 * @param significanceLevel The signficance level at which to calculate 
	 * the estimates. 
	 * Called threshold &tau; in the paper.
	 */
	Estimate estimateProportions(MixtureModel.Estimate estimate, int N1, int N2, int n_, double significanceLevel);

	/**
	 * The point and standard error estimates for TP, TN, and EDR.
	 */
	public interface Estimate {
		/**
		 * Return the estimator configuration.
		 */
		Configuration getConfiguration();

		/**
		 * Return the mixture model estimate given as an argument.
		 */
		MixtureModel.Estimate getModel();

		/**
		 * Return the equal group sample size, calculated as 
		 * 2 / (1 / N1 + 1 / N2) if N1 != N2.
		 */
		double getEqualGroupSampleSize();

		/**
		 * Return the sample size at which the estimates were calculated.
		 * Called n* in the paper.
		 */
		int getSampleSize();

		/**
		 * Return the significance level at which the estimates were calculated.
		 * Called threshold &tau; in the paper.
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

	/**
	 * An implementation of the algorithm for "adjusting" a p-value from 
	 * a hypothesis test to the "adjusted" p-value that would have been 
	 * produced from the same hypothesis test conducted at a different 
	 * sample size.
	 * The paper uses a t-test when describing this algorithm "though a
	 * p-value from any valid test can be used as long as it can be
	 * back-transformed to the test statistic that produced it".
	 */
	public interface PValueAdjuster {
		/**
		 * Adjust the user-supplied p-value, produced from a test with
		 * sample size n, to a new p-value, as if produced from a test
		 * with new sample size, n_ (called n* in the paper).
		 */
		double adjustPValue(double pvalue, double n, int n_);
	}

	/**
	 * The bootstrap procedure described in the paper requires a random
	 * number generator that provides random numbers from the binomial, 
	 * uniform, and beta distributions.
	 */
	public interface RandomNumberGenerator {
		/**
		 * Return a random number from the binomial distribution.
		 * @param n The number of Bernoulli trials to perform.
		 * @param p The probability of success for each trial.
		 */
		int nextBinomial(int n, double p);

		/**
		 * Return a random number from the uniform distribution 
		 * between 0 and 1.
		 */
		double nextUniform();

		/**
		 * Return a random number from the beta distribution
		 * with parameters r and s.
		 */
		double nextBeta(double r, double s);
	}

	/**
	 * Return the estimator configuration.
	 */
	Configuration getConfiguration();

	public interface Configuration {
		/**
		 * Return the number of bootstrap iterations.
		 */
		int getNumberOfIterations();
		PValueAdjuster getPValueAdjuster();
		RandomNumberGenerator getRandomNumberGenerator();
	}
}
