package edu.uab.ssg.mixomatic;

import java.util.*;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 4/5/06
 */

public final class DefaultModel implements OptimizationModel {
	// Note the JMSL API hacks for > 0 and positive infinity.
	private double[] lowerBounds = { 0., 1.7e-8, 1.7e-8 }; // lambda0, r, s
	private double[] upperBounds = { 1., 1.79e308, 1.79e308 };

	public boolean equals(Object o) {
		if (o instanceof DefaultModel) {
			DefaultModel tmp = (DefaultModel) o;
			return Arrays.equals(lowerBounds, tmp.lowerBounds) && Arrays.equals(upperBounds, tmp.upperBounds);
		}
		return false;
	}

	public int hashCode() { // See Bloch CH3.
		int tmp = 17;
		for (int i = 0; i < lowerBounds.length; i++) {
			long l = Double.doubleToLongBits(lowerBounds[i]);
			tmp = 37 * tmp + (int)(l ^ (l >>> 32));
		}
		for (int i = 0; i < upperBounds.length; i++) {
			long l = Double.doubleToLongBits(upperBounds[i]);
			tmp = 37 * tmp + (int)(l ^ (l >>> 32));
		}
		return tmp;
	}

	public String toString() {
		String EOL = System.getProperty("line.separator");
		StringBuffer buffer = new StringBuffer();
		buffer.append("lambda0 bounds: [").append(lowerBounds[0]).append(",").append(upperBounds[0]).append("]").append(EOL);
		buffer.append("r bounds: [").append(lowerBounds[1]).append(",").append(upperBounds[1]).append("]").append(EOL);
		buffer.append("s bounds: [").append(lowerBounds[2]).append(",").append(upperBounds[2]).append("]").append(EOL);
		return buffer.toString();
	}
	
	public OptimizationModel.LowerBounds getLowerBounds() {
		return new OptimizationModel.LowerBounds() {
			public double getLambda0() { return lowerBounds[0]; }
			public double getR() { return lowerBounds[1]; }
			public double getS() { return lowerBounds[2]; }
		};
	}

	public OptimizationModel.UpperBounds getUpperBounds() {
		return new OptimizationModel.UpperBounds() {
			public double getLambda0() { return upperBounds[0]; }
			public double getR() { return upperBounds[1]; }
			public double getS() { return upperBounds[2]; }
		};
	}

	public OptimizationModel.InitialGuess getGuess(double[] x) {
		if (x == null)
			throw new NullPointerException("x");
		if (x.length < 1)
			throw new IllegalArgumentException(String.valueOf(x.length));
		// Find the best starting values for the optimizer by grid search.
		double[] lambda0 = { 0.6, 0.8, 0.9 };
		double[] r = { 0.5, 1., 1.5, 2. };
		double[] s = { 0.75, 1.75, 2.75, 3.75 };

		final double[] bestGuess = { -1., -1., -1. }; // lambda0, r, s
		double max = -Double.MAX_VALUE; // See API for Double.MIN_VALUE.
		for (int i = 0; i < lambda0.length; i++) {
			for (int j = 0; j < r.length; j++) {
				for (int k = 0; k < s.length; k++) {
					LogLikelihoodFunction f = new LogLikelihoodFunction(lambda0[i], r[j], s[k]);
					double likelihood = f.getValue(x);
					if (likelihood > max) {
						max = likelihood; // LOOK!
						bestGuess[0] = lambda0[i];
						bestGuess[1] = r[j];
						bestGuess[2] = s[k];
					}
				}
			}
		}

		return new InitialGuess() {
			public double getLambda0() { return bestGuess[0]; }
			public double getR() { return bestGuess[1]; }
			public double getS() { return bestGuess[2]; }
		};
	}
}
