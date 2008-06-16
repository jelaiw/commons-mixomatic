package edu.uab.ssg.mixomatic;

/**
 * @author Jelai Wang
 */

public final class DefaultModel implements MixtureModel {
	private double lambda0;
	private double r, s;

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
	public String toString() { return lambda0 + " " + r + " " + s; }
}
