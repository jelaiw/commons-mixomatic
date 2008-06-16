package edu.uab.ssg.mixomatic.jsci;

import edu.uab.ssg.mixomatic.ProbabilityDensityFunction;
import edu.uab.ssg.mixomatic.MixtureModel;
import JSci.maths.statistics.BetaDistribution;

/**
 *  This class implements the mix-o-matic probability density function,
 *  defined by a user-supplied mixture model, and is based on the
 *  probability density function available in the JSci library.
 *
 * @author Jelai Wang
 * @version $Rev$ $LastChangedDate$ $LastChangedBy$ 1/6/05
 */

public final class DefaultPDF implements ProbabilityDensityFunction {
	public DefaultPDF() {
	}

	public double evaluate(MixtureModel model, double x) {
		if (model == null)
			throw new NullPointerException("model");
		if (x < 0. || x > 1.)
			throw new IllegalArgumentException(String.valueOf(x));
		double lambda0 = model.getLambda0();
		double r = model.getR(), s = model.getS();
		BetaDistribution beta = new BetaDistribution(r, s);
		return lambda0 + (1. - lambda0) * beta.probability(x); // Should we rewrite this expression to avoid cancellation error?
	}
}
