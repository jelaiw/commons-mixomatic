package edu.uab.ssg.mixomatic.jsci;

import edu.uab.ssg.mixomatic.ProbabilityDensityFunction;
import edu.uab.ssg.mixomatic.MixtureModel;
import JSci.maths.statistics.BetaDistribution;

/**
 *  This class implements the mix-o-matic probability density function,
 *  defined by model parameters lambda0, r, and s, using the beta
 *  probability density function available in the JSci library.
 *
 * @author Jelai Wang
 * @version $Rev$ $LastChangedDate$ $LastChangedBy$ 1/6/05
 */

public final class DefaultPDF implements ProbabilityDensityFunction {
	private double lambda0, r, s;
	private BetaDistribution beta;

	public DefaultPDF(double lambda0, double r, double s) {
		if (lambda0 < 0. || lambda0 > 1.)
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

	public double evaluate(double x) {
		if (x < 0. || x > 1.)
			throw new IllegalArgumentException(String.valueOf(x));
		return lambda0 + (1. - lambda0) * beta.probability(x); // Should we rewrite this expression to avoid cancellation?
	}

	public MixtureModel getModel() {
		return new MixtureModel() {
			public double getLambda0() { return lambda0; }
			public double getR() { return r; }
			public double getS() { return s; }
		};
	}
}
