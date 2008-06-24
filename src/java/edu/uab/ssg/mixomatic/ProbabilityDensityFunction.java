package edu.uab.ssg.mixomatic;

/**
 * This interface represents the mix-o-matic probability density function.
 *
 * @author Jelai Wang
 * @version $Rev$ $LastChangedDate$ $LastChangedBy$ 1/6/05
 */

public interface ProbabilityDensityFunction {
	/**
	 * Evaluate the mix-o-matic probability density function for the
	 * given mixture model at x.
	 *
	 * @param model The mixture model.
	 * @param x The value at which to evaluate the function. This is a
	 * p-value, so it must take a value between zero and one.
	 * @return The value of the function at x given the mixture model.
	 */

	double evaluate(MixtureModel model, double x);
}
