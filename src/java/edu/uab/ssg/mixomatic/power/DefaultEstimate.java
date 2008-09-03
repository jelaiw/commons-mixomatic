package edu.uab.ssg.mixomatic.power;

import edu.uab.ssg.mixomatic.MixtureModel;
import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;

/**
 * @author Jelai Wang
 */

/* package private */ final class DefaultEstimate implements BootstrapEstimator.Estimate {
	private BootstrapEstimator.Configuration configuration;
	private MixtureModel.Estimate model;
	private double equalGroupSampleSize;
	private int sampleSize;
	private double significanceLevel;
	private DoubleArrayList tp, tn, edr;

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
		this.tp = new DoubleArrayList(tp); 
		this.tn = new DoubleArrayList(tn); 
		this.edr = new DoubleArrayList(edr); 
	}

	public BootstrapEstimator.Configuration getConfiguration() { return configuration; }
	public MixtureModel.Estimate getModel() { return model; }
	public double getEqualGroupSampleSize() { return equalGroupSampleSize; }
	public int getSampleSize() { return sampleSize; }
	public double getSignificanceLevel() { return significanceLevel; }
	public double getTP() { return Descriptive.mean(tp); }
	public double getTN() { return Descriptive.mean(tn); }
	public double getEDR() { return Descriptive.mean(edr); }

	public double getStandardErrorForTP() { return sampleStandardDeviation(tp); }
	public double getStandardErrorForTN() { return sampleStandardDeviation(tn); }
	public double getStandardErrorForEDR() { return sampleStandardDeviation(edr); }

	// NOTE: We decided not to use Descriptive.sampleStandardDeviation().
	// For details, see literature regarding the correction factor for the 
	// bias introducted by taking the square root of the sample variance.
	private double sampleStandardDeviation(DoubleArrayList list) {
		double sampleVariance = Descriptive.sampleVariance(edr, getEDR());
//		return Descriptive.sampleStandardDeviation(list.size(), sampleVariance); 
		return Math.sqrt(sampleVariance);
	}

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
