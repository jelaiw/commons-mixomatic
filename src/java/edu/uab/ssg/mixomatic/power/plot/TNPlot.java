package edu.uab.ssg.mixomatic.power.plot;

import edu.uab.ssg.mixomatic.power.BootstrapEstimator;
import java.util.List;

/**
 * <p>A plot of TN estimates at various sample sizes and thresholds 
 * for significance.</p>
 *
 * <img alt="Example of a TN plot." src="doc-files/TNPlot-1.png"/>
 *
 * @author Jelai Wang
 */
public final class TNPlot extends AbstractPlot {
	/**
	 * Constructs a plot given bootstrap estimates of the proportions of
	 * interest.
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
