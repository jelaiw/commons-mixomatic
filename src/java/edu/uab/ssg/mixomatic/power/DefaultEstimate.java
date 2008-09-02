package edu.uab.ssg.mixomatic.power;

import com.imsl.stat.Summary;

/**
 * @author Jelai Wang
 */

/* package private */ final class DefaultEstimate implements BootstrapEstimator.Estimate {
	private BootstrapEstimator.Configuration configuration;
	private int sampleSize;
	private double significanceLevel;
	private double[] tp, tn, edr;

	/* package private */ DefaultEstimate(BootstrapEstimator.Configuration configuration, int sampleSize, double significanceLevel, double[] tp, double[] tn, double[] edr) {
		if (configuration == null)
			throw new NullPointerException("configuration");
		this.configuration = configuration;
		if (sampleSize < 0)
			throw new IllegalArgumentException(String.valueOf(sampleSize));
		this.sampleSize = sampleSize;
		if (significanceLevel < 0. || significanceLevel > 1.)
			throw new IllegalArgumentException(String.valueOf(significanceLevel));
		this.significanceLevel = significanceLevel;
		if (tp == null)
			throw new NullPointerException("tp");
		if (tn == null)
			throw new NullPointerException("tn");
		if (edr == null)
			throw new NullPointerException("edr");
		if (tp.length != tn.length || tp.length != edr.length || tn.length != edr.length)
			throw new IllegalArgumentException(tp.length + " " + tn.length + " " + edr.length);
		this.tp = tp; 
		this.tn = tn;
	   	this.edr = edr;
	}

	public BootstrapEstimator.Configuration getConfiguration() { return configuration; }
	public int getSampleSize() { return sampleSize; }
	public double getSignificanceLevel() { return significanceLevel; }
	public double getTP() { return Summary.mean(tp); }
	public double getTN() { return Summary.mean(tn); }
	public double getEDR() { return Summary.mean(edr); }
	public double getStandardErrorForTP() { return Summary.sampleStandardDeviation(tp); }
	public double getStandardErrorForTN() { return Summary.sampleStandardDeviation(tn); }
	public double getStandardErrorForEDR() { return Summary.sampleStandardDeviation(edr); }

	public String toString() {
		String EOL = System.getProperty("line.separator");
		StringBuffer buffer = new StringBuffer();
		buffer.append("sample size = ").append(getSampleSize()).append(EOL);
		buffer.append("significance level = ").append(getSignificanceLevel()).append(EOL);
		buffer.append("TP = ").append(getTP()).append(EOL);
		buffer.append("TN = ").append(getTN()).append(EOL);
		buffer.append("EDR = ").append(getEDR()).append(EOL);
		return buffer.toString().trim();
	}
}
