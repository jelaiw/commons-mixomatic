package edu.uab.ssg.mixomatic.power;

public final class BootstrapEstimator {
	public interface PValueAdjuster {
		double adjustPValue(double pvalue, double n, int n_);
	}
}
