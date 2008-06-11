package edu.uab.ssg.mixomatic.jmsl;

import edu.uab.ssg.mixomatic.ProbabilityDensityFunction;
import edu.uab.ssg.mixomatic.MixtureModel;
import com.imsl.stat.Cdf;

/**
 *  This class implements the mix-o-matic probability density function,
 *  defined by model parameters lambda0, r, and s, using the beta
 *  probability density function available in the JMSL.
 *
 *  This class does not check the model parameters for validity and is
 *  not appropriate for general purpose use. It is primarily used by
 *  mathematical optimizer implementations that may ask for the
 *  probability density function to be evaluated at values of lambda0,
 *  r, and s that are (slightly) out of bounds as part of its algorithm.
 *
 *  See JIRA issue HDB-105 for examples of the MinConNLP optimizer
 *  trying a lambda0 value of 1.0000002966479395 on the way to
 *  finding a solution.
*
 *	@author Jelai Wang
 *	@version $Rev: 42 $ $LastChangedDate: 2008-06-09 17:30:50 -0500 (Mon, 09 Jun 2008) $ $LastChangedBy: jelai $ 1/6/05
 */

public final class LoosePDF implements ProbabilityDensityFunction {
	private double lambda0, r, s;

	public LoosePDF(double lambda0, double r, double s) {
		this.lambda0 = lambda0;
		this.r = r;
		this.s = s;
	}

	public double evaluate(double x) {
		if (x < 0. || x > 1.)
			throw new IllegalArgumentException(String.valueOf(x));
		return lambda0 + (1. - lambda0) * Cdf.betaProb(x, r, s); // Rewrite to avoid cancellation error?
	}

	public MixtureModel getModel() {
		return new MixtureModel() {
			public double getLambda0() { return lambda0; }
			public double getR() { return r; }
			public double getS() { return s; }
		};
	}
}
