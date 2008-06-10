package edu.uab.ssg.mixomatic;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 4/4/06
 */

public final class LogLikelihoodFunction {
	private LogLikelihoodFunction() {
	}

	public static double evaluate(ProbabilityDensityFunction f, double[] x) {
		if (f == null)
			throw new NullPointerException("f");
		if (x == null)
			throw new NullPointerException("x");
		if (x.length < 1)
			throw new IllegalArgumentException(String.valueOf(x.length));
		double sum = 0.;
		for (int i = 0; i < x.length; i++) {
			sum += Math.log(f.evaluate(x[i]));
		}
		return sum;
	}
}
