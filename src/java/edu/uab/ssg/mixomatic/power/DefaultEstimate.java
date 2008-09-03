package edu.uab.ssg.mixomatic.power;

import edu.uab.ssg.mixomatic.MixtureModel;
import com.imsl.stat.Summary;

/**
 * @author Jelai Wang
 */

/* package private */ final class DefaultEstimate implements BootstrapEstimator.Estimate {
	private BootstrapEstimator.Configuration configuration;
	private MixtureModel.Estimate model;
	private double equalGroupSampleSize;
	private int sampleSize;
	private double significanceLevel;
	private double[] tp, tn, edr;

	/* package private */ DefaultEstimate(BootstrapEstimator.Configuration configuration, MixtureModel.Estimate model, double equalGroupSampleSize, int sampleSize, double significanceLevel, double[] tp, double[] tn, double[] edr) {
		if (configuration == null)
			throw new NullPointerException("configuration");
		if (model == null)
			throw new NullPointerException("model");
		if (equalGroupSampleSize < 1.)
			throw new IllegalArgumentException(String.valueOf(equalGroupSampleSize));
		if (sampleSize < 1)
			throw new IllegalArgumentException(String.valueOf(sampleSize));
		if (significanceLevel < 0. || significanceLevel > 1.)
			throw new IllegalArgumentException(String.valueOf(significanceLevel));
		if (tp == null)
			throw new NullPointerException("tp");
		if (tn == null)
			throw new NullPointerException("tn");
		if (edr == null)
			throw new NullPointerException("edr");
		if (tp.length != tn.length || tp.length != edr.length || tn.length != edr.length)
			throw new IllegalArgumentException(tp.length + " " + tn.length + " " + edr.length);
		this.configuration = configuration;
		this.model = model;
		this.equalGroupSampleSize = equalGroupSampleSize;
		this.sampleSize = sampleSize;
		this.significanceLevel = significanceLevel;
		this.tp = tp; 
		this.tn = tn;
	   	this.edr = edr;
	}

	public BootstrapEstimator.Configuration getConfiguration() { return configuration; }
	public MixtureModel.Estimate getModel() { return model; }
	public double getEqualGroupSampleSize() { return equalGroupSampleSize; }
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
		buffer.append("estimator configuration").append(EOL);
		buffer.append("-----------------------").append(EOL);
		buffer.append(getConfiguration()).append(EOL);
		buffer.append("mixture model estimate").append(EOL);
		buffer.append("----------------------").append(EOL);
		buffer.append(getModel()).append(EOL);
		buffer.append("estimates for proportions of interest").append(EOL);
		buffer.append("-------------------------------------").append(EOL);
		buffer.append("equal group sample size = ").append(getEqualGroupSampleSize()).append(EOL);
		buffer.append("sample size = ").append(getSampleSize()).append(EOL);
		buffer.append("significance level = ").append(getSignificanceLevel()).append(EOL);
		buffer.append("TP = ").append(getTP()).append(EOL);
		buffer.append("standard error TP = ").append(getStandardErrorForTP()).append(EOL);
		buffer.append("TN = ").append(getTN()).append(EOL);
		buffer.append("standard error TN = ").append(getStandardErrorForTN()).append(EOL);
		buffer.append("EDR = ").append(getEDR()).append(EOL);
		buffer.append("standard error EDR = ").append(getStandardErrorForEDR()).append(EOL);
		return buffer.toString().trim();
	}
}
