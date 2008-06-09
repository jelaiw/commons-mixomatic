package edu.uab.ssg.mixomatic;

import com.imsl.math.Sfun;

/**
 *  Exactly the same as the SAS prototype implementation.
 *
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 1/6/05
 */

public final class DefaultPDF implements ProbabilityDensityFunction {
	private double lambda0, r, s;
	private double gamma;

	public DefaultPDF(double lambda0, double r, double s) {
		if (lambda0 < 0. || lambda0 > 1.0000002966479395) // See bug #157.
			throw new IllegalArgumentException(String.valueOf(lambda0));
		if (r < 0.)
			throw new IllegalArgumentException(String.valueOf(r));
		if (s < 0.)
			throw new IllegalArgumentException(String.valueOf(s));
		this.lambda0 = lambda0;
		this.r = r;
		this.s = s;
		this.gamma = Sfun.gamma(r) * Sfun.gamma(s) / Sfun.gamma(r + s);
	}

	public double getValue(double x) {
		if (x < 0. || x > 1.)
			throw new IllegalArgumentException(String.valueOf(x));
		double numerator = Math.pow(x, r - 1.) * Math.pow(1. - x, s - 1.);
		return lambda0 + ((1. - lambda0) * numerator / gamma);
	}

	public double getLambda0() { return lambda0; }
	public double getR() { return r; }
	public double getS() { return s; }
}
