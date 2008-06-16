package edu.uab.ssg.mixomatic.jmsl;

import edu.uab.ssg.mixomatic.ProbabilityDensityFunction;
import edu.uab.ssg.mixomatic.LogLikelihoodFunction;
import edu.uab.ssg.mixomatic.MixtureModel;
import java.util.*;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 4/5/06
 */

public final class RestrictedConfiguration implements BoundedOptimizer.Configuration {
	// Note the JMSL API hacks for > 0 and positive infinity.
	private double[] lowerBounds = { 0., 1.7e-8, 1. + 1.7e-8 }; // lambda0, r, s
	private double[] upperBounds = { 1., 1. - 1.7e-8, 1.79e308 };

	/* package private */ RestrictedConfiguration() {
	}

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
		double[] lambda0 = { 0.6, 0.8, 0.9 };
		double[] r = { 0.5, 0.75, 0.9 };
		double[] s = { 1.25, 1.75, 2.25, 2.75, 3.25, 3.75 };

		final double[] bestGuess = { Double.NaN, Double.NaN, Double.NaN }; // lambda0, r, s
		double max = -Double.MAX_VALUE; // See API for Double.MIN_VALUE.
		for (int i = 0; i < lambda0.length; i++) {
			for (int j = 0; j < r.length; j++) {
				for (int k = 0; k < s.length; k++) {
					MixtureModel model = new LooseModel(lambda0[i], r[j], s[k]);
					ProbabilityDensityFunction function = new edu.uab.ssg.mixomatic.jsci.DefaultPDF(); // Faster than JMSL implementation.
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
