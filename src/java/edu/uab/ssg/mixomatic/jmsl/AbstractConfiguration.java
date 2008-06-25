package edu.uab.ssg.mixomatic.jmsl;

import edu.uab.ssg.mixomatic.ProbabilityDensityFunction;
import edu.uab.ssg.mixomatic.LogLikelihoodFunction;
import edu.uab.ssg.mixomatic.MixtureModel;
import java.util.*;

/**
 *	@author Jelai Wang
 */

/* package private */ abstract class AbstractConfiguration {
	protected double[] lowerBounds, upperBounds;
	protected double[] lambda0, r, s;

	public BoundedOptimizer.LowerBounds getLowerBounds() {
		return new BoundedOptimizer.LowerBounds() {
			public double getLambda0() { return lowerBounds[0]; }
			public double getR() { return lowerBounds[1]; }
			public double getS() { return lowerBounds[2]; }
		};
	}

	public BoundedOptimizer.UpperBounds getUpperBounds() {
		return new BoundedOptimizer.UpperBounds() {
			public double getLambda0() { return upperBounds[0]; }
			public double getR() { return upperBounds[1]; }
			public double getS() { return upperBounds[2]; }
		};
	}

	// Finds the best starting point by grid search.
	public BoundedOptimizer.StartingPoint findStartingPoint(double[] sample) {
		if (sample == null)
			throw new NullPointerException("sample");
		if (sample.length < 1)
			throw new IllegalArgumentException(String.valueOf(sample.length));

		final double[] bestGuess = { Double.NaN, Double.NaN, Double.NaN }; // lambda0, r, s
		double max = -Double.MAX_VALUE; // See API for Double.MIN_VALUE.
		for (int i = 0; i < lambda0.length; i++) {
			for (int j = 0; j < r.length; j++) {
				for (int k = 0; k < s.length; k++) {
					MixtureModel model = new LooseModel(lambda0[i], r[j], s[k]);
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

		return new BoundedOptimizer.StartingPoint() {
			public double getLambda0() { return bestGuess[0]; }
			public double getR() { return bestGuess[1]; }
			public double getS() { return bestGuess[2]; }
		};
	}

	public String toString() {
		String EOL = System.getProperty("line.separator");
		StringBuffer buffer = new StringBuffer();
		buffer.append("lambda0 bounds: [").append(lowerBounds[0]).append(",").append(upperBounds[0]).append("]").append(EOL);
		buffer.append("r bounds: [").append(lowerBounds[1]).append(",").append(upperBounds[1]).append("]").append(EOL);
		buffer.append("s bounds: [").append(lowerBounds[2]).append(",").append(upperBounds[2]).append("]").append(EOL);
		return buffer.toString();
	}
}
