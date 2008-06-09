package edu.uab.ssg.mixomatic.jmsl;

import edu.uab.ssg.mixomatic.ProbabilityDensityFunction;
import edu.uab.ssg.mixomatic.MixtureModel;
import com.imsl.stat.Cdf;

/**
 *  This class implements the mix-o-matic probability density function,
 *  defined by model parameters lambda0, r, and s, using the beta
 *  probability density function available in the JMSL.
 *
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 1/6/05
 */

public final class DefaultPDF implements ProbabilityDensityFunction {
	private double lambda0, r, s;

	public DefaultPDF(double lambda0, double r, double s) {
		// This is a workaround for the MinConNLP issue described at http://www.ssg.uab.edu/jira/browse/HDB-105.
		// Obviously, we would like to assert 0 < lambda0 < 1 if we can.
		if (lambda0 < 0. || lambda0 > 1.0000002966479395)
			throw new IllegalArgumentException(String.valueOf(lambda0));
		if (r < 0.)
			throw new IllegalArgumentException(String.valueOf(r));
		if (s < 0.)
			throw new IllegalArgumentException(String.valueOf(s));
		this.lambda0 = lambda0;
		this.r = r;
		this.s = s;
	}

	public double evaluate(double x) {
		if (x < 0. || x > 1.)
			throw new IllegalArgumentException(String.valueOf(x));
		return lambda0 + (1. - lambda0) * Cdf.betaProb(x, r, s);
	}

	public MixtureModel getModel() {
		return new MixtureModel() {
			public double getLambda0() { return lambda0; }
			public double getR() { return r; }
			public double getS() { return s; }
		};
	}
}
