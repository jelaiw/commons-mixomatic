package edu.uab.ssg.mixomatic.power;

public final class BootstrapEstimator {
	public interface Estimate {
		int getSampleSize();
		double getSignificanceLevel();
		double getTP();
		double getTN();
		double getEDR();
		double getStandardErrorForTP();
		double getStandardErrorForTN();
		double getStandardErrorForEDR();
	}

	public interface PValueAdjuster {
		double adjustPValue(double pvalue, double n, int n_);
	}

	public interface RandomNumberGenerator {
		double nextBinomial(int n, double p);
		double nextUniform();
		double nextBeta(double r, double s);
	}
}
