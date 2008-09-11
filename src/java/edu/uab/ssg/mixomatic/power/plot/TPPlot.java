package edu.uab.ssg.mixomatic.power.plot;

import edu.uab.ssg.mixomatic.power.BootstrapEstimator;
import java.util.List;

/**
 * A plot of TP estimates at various sample sizes and thresholds 
 * for significance.
 *
 * @author Jelai Wang
 */
public final class TPPlot extends AbstractPlot {
	/**
	 * Construct plot given bootstrap estimates of the proportions of interest.
	 */
	public TPPlot(List<BootstrapEstimator.Estimate> estimates) {
		super(estimates, "TP at Various Sample Sizes and Thresholds", new AbstractPlot.ProportionHandler() {
			public double getProportionOfInterest(BootstrapEstimator.Estimate estimate) {
				return estimate.getTP();
			}
		});
	}
}
