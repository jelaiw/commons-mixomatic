package edu.uab.ssg.mixomatic.helper;

import edu.uab.ssg.mixomatic.*;
import java.util.*;

/**
 * A helper for optimizer configuration that allows the implementor 
 * to subclass to provide the set of feasible points for mathematical
 * optimization as lower and upper bounds and the grid search space 
 * for a simple grid search to find a starting point.
 *
 * @author Jelai Wang
 */
public abstract class AbstractConfiguration {
	/**
	 * An array specifying the lower bounds for lambda0, r, and s.
	 * The the bounds are expected in the following order: { lambda0, r, s}.
	 */
	protected double[] lowerBounds;

	/**
	 * An array specifying the upper bounds for lambda0, r, and s.
	 * The the bounds are expected in the following order: { lambda0, r, s}.
	 */
	protected double[] upperBounds;

	/**
	 * An array specifying grid search points for lambda0.
	 * The points must be between 0 and 1, because lambda0 is a proportion,
	 * and within the set of feasible points specified by the lower and
	 * upper bounds.
	 */
	protected double[] lambda0;

	/**
	 * An array specifying grid search points for r.
	 * The points must be positive, because r is a beta distribution 
	 * parameter, and within the set of feasible points specified by 
	 * the lower and upper bounds.
	 */
	protected double[] r;

	/**
	 * An array specifying grid search points for s.
	 * The points must be positive, because s is a beta distribution 
	 * parameter, and within the set of feasible points specified by 
	 * the lower and upper bounds.
	 */
	protected double[] s;

	/**
	 * Constructs the configuration.
	 */
	protected AbstractConfiguration() {
	}

	/**
	 * Returns the lower bounds.
	 */
	public OptimizerConfiguration.LowerBounds getLowerBounds() {
		return new OptimizerConfiguration.LowerBounds() {
			public double getLambda0() { return lowerBounds[0]; }
			public double getR() { return lowerBounds[1]; }
			public double getS() { return lowerBounds[2]; }
		};
	}

	/**
	 * Returns the upper bounds.
	 */
	public OptimizerConfiguration.UpperBounds getUpperBounds() {
		return new OptimizerConfiguration.UpperBounds() {
			public double getLambda0() { return upperBounds[0]; }
			public double getR() { return upperBounds[1]; }
			public double getS() { return upperBounds[2]; }
		};
	}

	/**
	 * Finds an optimizer starting point by grid search.
	 */
	public OptimizerConfiguration.StartingPoint findStartingPoint(double[] sample) {
		if (sample == null)
			throw new NullPointerException("sample");
		if (sample.length < 1)
			throw new IllegalArgumentException(String.valueOf(sample.length));

		final double[] bestGuess = { Double.NaN, Double.NaN, Double.NaN }; // lambda0, r, s
		double max = -Double.MAX_VALUE; // See API for Double.MIN_VALUE.
		for (int i = 0; i < lambda0.length; i++) {
			for (int j = 0; j < r.length; j++) {
				for (int k = 0; k < s.length; k++) {
					MixtureModel model = new DefaultModel(lambda0[i], r[j], s[k]);
					ProbabilityDensityFunction function = new edu.uab.ssg.mixomatic.jsci.DefaultProbabilityDensityFunction(); // Faster than JMSL implementation.
					double L = LogLikelihoodFunction.evaluate(model, function, sample);
					if (L > max) {
						max = L; // LOOK!
						bestGuess[0] = lambda0[i];
						bestGuess[1] = r[j];
						bestGuess[2] = s[k];
					}
				}
			}
		}

		return new OptimizerConfiguration.StartingPoint() {
			public double getLambda0() { return bestGuess[0]; }
			public double getR() { return bestGuess[1]; }
			public double getS() { return bestGuess[2]; }
			public String toString() { return bestGuess[0] + " " + bestGuess[1] + " " + bestGuess[2]; }
		};
	}

	/**
	 * Returns a string representation of the configuration.
	 */
	public String toString() {
		String EOL = System.getProperty("line.separator");
		StringBuffer buffer = new StringBuffer();
		buffer.append("lambda0 bounds: [").append(lowerBounds[0]).append(",").append(upperBounds[0]).append("]").append(EOL);
		buffer.append("r bounds: [").append(lowerBounds[1]).append(",").append(upperBounds[1]).append("]").append(EOL);
		buffer.append("s bounds: [").append(lowerBounds[2]).append(",").append(upperBounds[2]).append("]").append(EOL);
		return buffer.toString();
	}
}
