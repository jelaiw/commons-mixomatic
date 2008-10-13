package edu.uab.ssg.mixomatic;

/**
 * A mix-o-matic mixture model implementation for general purpose use.
 *
 * @author Jelai Wang
 */

public final class DefaultModel implements MixtureModel {
	private double lambda0;
	private double r, s;

	/**
	 * Constructs a mixture model with a uniform component specified by
	 * lambda0 and a beta component specified by shape parameters r and s.
	 *
	 * @param lambda0 The area "under the curve" for the uniform component of the mixture model. Because it is a proportion, the value must be between zero and one.
	 * @param r The first shape parameter for the beta component, must be a positive number.
	 * @param s The second shape parameter for the beta component, must be a positive number.
	 */

	public DefaultModel(double lambda0, double r, double s) {
		if (lambda0 < 0. || lambda0 > 1.)
			throw new IllegalArgumentException(String.valueOf(lambda0));
		if (r < 0.)
			throw new IllegalArgumentException(String.valueOf(r));
		if (s < 0.)
			throw new IllegalArgumentException(String.valueOf(s));
		this.lambda0 = lambda0;
		this.r = r;
		this.s = s;
	}

	public double getLambda0() { return lambda0; }
	public double getR() { return r; }
	public double getS() { return s; }

	/**
	 * Returns a string representation of this mixture model.
	 */

	public String toString() { return lambda0 + " " + r + " " + s; }
}
