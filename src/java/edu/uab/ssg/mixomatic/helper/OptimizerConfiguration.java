package edu.uab.ssg.mixomatic.helper;

/**
 * This helper interface is intended to assist implementors of the
 * MixtureModel.Estimator interface whose implementation is based
 * on mathematical optimization.
 *
 * It allows those implementors to abstract configuration details 
 * that are common across different implementations, encouraging
 * re-use. This configuration abstraction represents the set of 
 * feasible points that the mixture model parameter estimates 
 * are allowed to take as a set of lower and upper bounds. It also 
 * includes a mechanism that allows a configuration implementor to 
 * suggest a reasonable starting point given a sample 
 * distribution of p-values.
 *
 * @author Jelai Wang
 */

public interface OptimizerConfiguration {
	/**
	 * Return the lower bounds of &lambda;<sub><span>0</span></sub>, r, and s.
	 */
	LowerBounds getLowerBounds();

	/**
	 * Return the upper bounds of &lambda;<sub><span>0</span></sub>, r, and s.
	 */
	UpperBounds getUpperBounds();

	/**
	 * Find and return a "reasonable" starting point for the optimizer.
	 * This is typically implemented as a grid search, method of
	 * moments estimate, or pre-specified points (ignoring the
	 * sample distribution of p-values).
	 * @param sample The sample distribution of p-values.
	 * @return The starting point for the optimizer.
	 */
	StartingPoint findStartingPoint(double[] sample);

	/**
	 * This interface represents the lower bounds that the mixture model
	 * parameter estimates are allowed to take.
	 */
	public interface LowerBounds {
		/**
		 * Return the lower bound of &lambda;<sub><span>0</span></sub>.
		 */
		double getLambda0();

		/**
		 * Return the lower bound of r.
		 */
		double getR();

		/**
		 * Return the lower bound of s.
		 */
		double getS();
	}

	/**
	 * This interface represents the upper bounds that the mixture model
	 * parameter estimates are allowed to take.
	 */
	public interface UpperBounds {
		/**
		 * Return the upper bound of &lambda;<sub><span>0</span></sub>.
		 */
		double getLambda0();

		/**
		 * Return the upper bound of r.
		 */
		double getR();

		/**
		 * Return the upper bound of s.
		 */
		double getS();
	}

	/**
	 * This interface represents the starting point of the optimizer.
	 */
	public interface StartingPoint {
		/**
		 * Return the starting value of &lambda;<sub><span>0</span></sub>.
		 */
		double getLambda0();

		/**
		 * Return the starting value of r.
		 */
		double getR();

		/**
		 * Return the starting value of s.
		 */
		double getS();
	}
}
