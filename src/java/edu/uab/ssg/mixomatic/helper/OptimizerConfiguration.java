package edu.uab.ssg.mixomatic.helper;

/**
 * An optimization configuration is intended to assist implementors 
 * of the MixtureModel.Estimator interface whose implementation is 
 * based on mathematical optimization.
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
	 * Returns the lower bounds of &lambda;<sub>0</sub>, r, and s.
	 */
	LowerBounds getLowerBounds();

	/**
	 * Returns the upper bounds of &lambda;<sub>0</sub>, r, and s.
	 */
	UpperBounds getUpperBounds();

	/**
	 * Finds a "reasonable" starting point for the optimizer.
	 * This is typically implemented as a grid search, method of
	 * moments estimate, or pre-specified points (ignoring the
	 * sample distribution of p-values).
	 * @param sample The sample distribution of p-values.
	 * @return The starting point for the optimizer.
	 */
	StartingPoint findStartingPoint(double[] sample);

	/**
	 * The lower bounds that the mixture model parameter estimates 
	 * are allowed to take.
	 */
	public interface LowerBounds {
		/**
		 * Returns the lower bound of &lambda;<sub>0</sub>.
		 */
		double getLambda0();

		/**
		 * Returns the lower bound of r.
		 */
		double getR();

		/**
		 * Returns the lower bound of s.
		 */
		double getS();
	}

	/**
	 * The upper bounds that the mixture model parameter estimates are 
	 * allowed to take.
	 */
	public interface UpperBounds {
		/**
		 * Returns the upper bound of &lambda;<sub>0</sub>.
		 */
		double getLambda0();

		/**
		 * Returns the upper bound of r.
		 */
		double getR();

		/**
		 * Returns the upper bound of s.
		 */
		double getS();
	}

	/**
	 * The starting point of the optimizer.
	 */
	public interface StartingPoint {
		/**
		 * Returns the starting value of &lambda;<sub>0</sub>.
		 */
		double getLambda0();

		/**
		 * Returns the starting value of r.
		 */
		double getR();

		/**
		 * Returns the starting value of s.
		 */
		double getS();
	}
}
