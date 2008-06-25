package edu.uab.ssg.mixomatic.jmsl;

import edu.uab.ssg.mixomatic.ProbabilityDensityFunction;
import edu.uab.ssg.mixomatic.MixtureModel;
import com.imsl.stat.Cdf;

/**
 * This class implements the mix-o-matic probability density function using 
 * the Cdf class available in the JMSL library at http://www.vni.com.
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
		return lambda0 + (1. - lambda0) * Cdf.betaProb(x, r, s); // Rewrite to avoid cancellation error?
	}
}
