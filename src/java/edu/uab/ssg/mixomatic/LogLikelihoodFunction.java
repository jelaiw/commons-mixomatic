package edu.uab.ssg.mixomatic;

/**
 * This class implements the mix-o-matic log-likelihood function, which is
 * the product of the mix-o-matic probability density function for each 
 * p-value in the sample distribution.
 *
 * This function is described in detail at
 * http://dx.doi.org/10.1016/S0167-9473(01)00046-9.
 *
 * @author Jelai Wang
 * @version $Rev$ $LastChangedDate$ $LastChangedBy$ 4/4/06
 */

public final class LogLikelihoodFunction {
	private LogLikelihoodFunction() {
	}

	/**
	 * Evaluate the log-likelihood function for a given mixture model using
	 * a user-supplied probability density function implementation and the
	 * sample distribution of p-values.
	 *
	 * @param model The mixture model.
	 * @param function The mix-o-matic probability density function.
	 * @param x The sample distribution of p-values as a double array. Each
	 * element of this array is a p-value and must take a value between
	 * zero and one.
	 * @return The value of the log-likelihood function for the given mixture
	 * model and sample distribution of p-values.
	 */

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
