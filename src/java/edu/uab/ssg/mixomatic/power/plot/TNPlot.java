package edu.uab.ssg.mixomatic.power.plot;

import edu.uab.ssg.mixomatic.power.BootstrapEstimator;
import java.util.List;

/**
 * A plot of TN estimates at various sample sizes and thresholds 
 * for significance.
 *
 * @author Jelai Wang
 */
public final class TNPlot extends AbstractPlot {
	/**
	 * Construct plot given bootstrap estimates of the proportions of interest.
	 * @param estimates The bootstrap estimates for EDR, TP, and TN at various
	 * sample sizes and thresholds.
	 */
	public TNPlot(List<BootstrapEstimator.Estimate> estimates) {
		super(estimates, "TN at Various Sample Sizes and Thresholds", new AbstractPlot.ProportionHandler() {
			public double getProportionOfInterest(BootstrapEstimator.Estimate estimate) {
				return estimate.getTN();
			}
		});
	}
}
