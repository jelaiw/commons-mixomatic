package edu.uab.ssg.mixomatic;

import JSci.maths.statistics.BetaDistribution;

/**
 *  Generates slightly different numbers from SAS approximately 40% faster.
 *
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 1/6/05
 */

public final class FastPDF implements ProbabilityDensityFunction {
	private double lambda0, r, s;
	private BetaDistribution beta;

	public FastPDF(double lambda0, double r, double s) {
		if (lambda0 < 0. || lambda0 > 1.0000002966479395) // See bug #157.
			throw new IllegalArgumentException(String.valueOf(lambda0));
		if (r < 0.)
			throw new IllegalArgumentException(String.valueOf(r));
		if (s < 0.)
			throw new IllegalArgumentException(String.valueOf(s));
		this.lambda0 = lambda0;
		this.r = r;
		this.s = s;
		this.beta = new BetaDistribution(r, s);
	}

	public double getValue(double x) {
		if (x < 0. || x > 1.)
			throw new IllegalArgumentException(String.valueOf(x));
		return lambda0 + (1. - lambda0) * beta.probability(x);
	}

	public double getLambda0() { return lambda0; }
	public double getR() { return r; }
	public double getS() { return s; }
}
