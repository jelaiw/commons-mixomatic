package edu.uab.ssg.mixomatic.power.plot;

import edu.uab.ssg.mixomatic.power.BootstrapEstimator;
import java.util.List;

/**
 * <p>
 * A plot of EDR estimates at various sample sizes and thresholds 
 * for significance.
 * </p>
 *
 * <img alt="Example of an EDR plot." src="doc-files/EDRPlot-1.png"/>
 *
 * @author Jelai Wang
 */
public final class EDRPlot extends AbstractPlot {
	/**
	 * Construct plot given bootstrap estimates of the proportions of interest.
	 * @param estimates The bootstrap estimates for EDR, TP, and TN at various
	 * sample sizes and thresholds.
	 */
	public EDRPlot(List<BootstrapEstimator.Estimate> estimates) {
		super(estimates, "EDR at Various Sample Sizes and Thresholds", new AbstractPlot.ProportionHandler() {
			public double getProportionOfInterest(BootstrapEstimator.Estimate estimate) {
				return estimate.getEDR();
			}
		});
	}
}
