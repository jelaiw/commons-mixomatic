package edu.uab.ssg.mixomatic.power.plot;

import edu.uab.ssg.mixomatic.power.BootstrapEstimator;
import java.util.List;

/**
 * <p>A plot of TP estimates at various sample sizes and thresholds 
 * for significance.</p>
 *
 * <img alt="Example of a TP plot." src="doc-files/TPPlot-1.png"/>
 *
 * @author Jelai Wang
 */
public final class TPPlot extends AbstractPlot {
	/**
	 * Constructs a plot given bootstrap estimates of the proportions of
	 * interest.
	 * @param estimates The bootstrap estimates for EDR, TP, and TN at various
	 * sample sizes and thresholds.
	 */
	public TPPlot(List<BootstrapEstimator.Estimate> estimates) {
		super(estimates, "TP at Various Sample Sizes and Thresholds", new AbstractPlot.ProportionHandler() {
			public double getProportionOfInterest(BootstrapEstimator.Estimate estimate) {
				return estimate.getTP();
			}
		});
	}
}
