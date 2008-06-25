package edu.uab.ssg.mixomatic.jsci;

import edu.uab.ssg.mixomatic.ProbabilityDensityFunction;
import edu.uab.ssg.mixomatic.MixtureModel;
import JSci.maths.statistics.BetaDistribution;

/**
 * This class implements the mix-o-matic probability density function
 * using the BetaDistribution class available in the JSci library at
 * http://jsci.sourceforge.net/.
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
