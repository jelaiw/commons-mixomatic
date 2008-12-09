package edu.uab.ssg.mixomatic.power.apache;

import edu.uab.ssg.mixomatic.power.BootstrapEstimator;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.apache.commons.math.MathException;

/**
 * A p-value adjuster for the t-test based on org.apache.commons.math.distribution.TDistribution in Apache Commons Math.
 *
 * @author Jelai Wang
 */
public final class TTestPValueAdjuster implements BootstrapEstimator.PValueAdjuster {
	private TDistribution dist = new TDistributionImpl(Double.NaN);

	/**
	 * Constructs the adjuster.
	 */
	public TTestPValueAdjuster() {
	}

	public double adjustPValue(double pvalue, double n, int n_) {
		// Figure out the test statistic, t, corresponding to this p-value.
		// In other words, "back transform" the p-value to the t-statistic.
		double df = 2. * n - 2.;
		dist.setDegreesOfFreedom(df);
		double t = Double.NaN;
		try {
			t = dist.inverseCumulativeProbability(pvalue / 2.); // NOTE: this is a two-tailed t-test, so we first divide the p-value by two to get the test statistic corresponding to one of the tail areas available for rejection.
		}
		catch (MathException e) {
			throw new RuntimeException(e);
		}
		// Calculate an adjusted t-statistic using the new sample size, n_.
		double df_ = 2. * n_ - 2.;
		double t_ = t * Math.sqrt(n_ / n);
		dist.setDegreesOfFreedom(df_);

		// Return the adjusted p-value. For a two-tailed t-test, it is equal 
		// to the total tail area available for rejection of the hypothesis.
		try {
			return 2. * dist.cumulativeProbability(t_);
		}
		catch (MathException e) {
			throw new RuntimeException(e);
		}
	}
}
