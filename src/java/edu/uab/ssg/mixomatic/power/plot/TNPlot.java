package edu.uab.ssg.mixomatic.power.plot;

import edu.uab.ssg.mixomatic.power.BootstrapEstimator;
import java.util.List;

/**
 * @author Jelai Wang
 */
public final class TNPlot extends AbstractPlot {
	public TNPlot(List<BootstrapEstimator.Estimate> estimates) {
		super(estimates, "TN at Various Sample Sizes and Thresholds", new AbstractPlot.ProportionHandler() {
			public double getProportionOfInterest(BootstrapEstimator.Estimate estimate) {
				return estimate.getTN();
			}
		});
	}
}
