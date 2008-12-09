package edu.uab.ssg.mixomatic.power.jmsl;

import edu.uab.ssg.mixomatic.power.BootstrapEstimator;
import com.imsl.stat.Cdf;

/**
 * A p-value adjuster for the t-test based on com.imsl.stat.Cdf in the JMSL.
 *
 * @author Jelai Wang
 */
public final class TTestPValueAdjuster implements BootstrapEstimator.PValueAdjuster {
	/**
	 * Constructs the adjuster.
	 */
	public TTestPValueAdjuster() {
	}

	public double adjustPValue(double pvalue, double n, int n_) {
		// Figure out the test statistic, t, corresponding to this p-value.
		// In other words, "back transform" the p-value to the t-statistic.
		double df = 2. * n - 2.;
		double t = Cdf.inverseStudentsT(pvalue / 2., df); // NOTE: this is a two-tailed t-test, so we first divide the p-value by two to get the test statistic corresponding to one of the tail areas available for rejection.
		// Calculate an adjusted t-statistic using the new sample size, n_.
		double df_ = 2. * n_ - 2.;
		double t_ = t * Math.sqrt(n_ / n);
		// Return the adjusted p-value. For a two-tailed t-test, it is equal 
		// to the total tail area available for rejection of the hypothesis.
		return 2. * Cdf.studentsT(t_, df_);
	}
}
