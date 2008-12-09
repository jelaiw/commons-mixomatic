package edu.uab.ssg.mixomatic.helper;

import edu.uab.ssg.mixomatic.MixtureModel;

/**
 * A mixture model estimate implementation intended for re-use by mixture 
 * model estimator implementations.
 *
 * @author Jelai Wang
 */
public final class DefaultEstimate implements MixtureModel.Estimate {
	private double lambda0, r, s;
	private double[] sample;

	/**
	 * Constructs a mixture model estimate.
	 * @param lambda0 The estimate for lambda0.
	 * @param r The estimate for r.
	 * @param s The estimate for s.
	 * @param sample The sample distribution of p-values.
	 */
	public DefaultEstimate(double lambda0, double r, double s, double[] sample) {
		if (lambda0 < 0. || lambda0 > 1.)
			throw new IllegalArgumentException(String.valueOf(lambda0));
		if (r < 0.)
			throw new IllegalArgumentException(String.valueOf(r));
		if (s < 0.)
			throw new IllegalArgumentException(String.valueOf(s));
		if (sample == null)
			throw new NullPointerException("sample");
		this.lambda0 = lambda0;
		this.r = r;
		this.s = s;
		this.sample = sample;
	}

	public double getLambda0() { return lambda0; }
	public double getR() { return r; }
	public double getS() { return s; }
	public double[] getSample() { return (double[]) sample.clone(); }

	/**
	 * Returns a string representation of this mixture model estimate.
	 */
	public String toString() {
		String EOL = System.getProperty("line.separator");
		StringBuffer buffer = new StringBuffer();
		buffer.append("lambda0 = ").append(lambda0).append(EOL);
		buffer.append("r = ").append(r);
		buffer.append(", s = ").append(s).append(EOL);
		buffer.append("sample size = ").append(sample.length);
		return buffer.toString();
	}
}
