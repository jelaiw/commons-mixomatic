package edu.uab.ssg.mixomatic;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 4/4/06
 */

public final class LogLikelihoodFunction {
	private LogLikelihoodFunction() {
	}

	public static double evaluate(MixtureModel model, ProbabilityDensityFunction function, double[] x) {
		if (model == null)
			throw new NullPointerException("model");
		if (function == null)
			throw new NullPointerException("function");
		if (x == null)
			throw new NullPointerException("x");
		if (x.length < 1)
			throw new IllegalArgumentException(String.valueOf(x.length));
		double sum = 0.;
		for (int i = 0; i < x.length; i++) {
			sum += Math.log(function.evaluate(model, x[i]));
		}
		return sum;
	}
}
