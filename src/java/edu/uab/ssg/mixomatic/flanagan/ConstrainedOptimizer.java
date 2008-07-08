package edu.uab.ssg.mixomatic.flanagan;

import edu.uab.ssg.mixomatic.*;
import edu.uab.ssg.mixomatic.helper.LooseModel;
import flanagan.math.*;

/**
 * This class implements a maximum likelihood estimator of the mixture 
 * model parameters using mathematical optimization.
 *
 * @author Jelai Wang
 * @version 7/8/08
 */

public final class ConstrainedOptimizer implements MixtureModel.Estimator {
	public ConstrainedOptimizer() {
	}

	public MixtureModel.Estimate estimateParameters(final double[] sample) throws MixomaticException {
		if (sample == null)
			throw new NullPointerException("sample");
		if (sample.length < 1)
			throw new IllegalArgumentException(String.valueOf(sample.length));
		// Make defensive copy.	
		final double[] copy = (double[]) sample.clone(); 
		// Assert validity of sample p-values, 0 < p < 1.
		for (int i = 0; i < copy.length; i++) {
			if (copy[i] < 0. || copy[i] > 1.)
				throw new IllegalArgumentException(i + ", " + copy[i]);
			if (Double.isNaN(copy[i]))	
				throw new IllegalArgumentException(i + ", " + copy[i]);
		}

		MaximisationFunction f = new MaximisationFunction() {
			public double function(double[] x) {
				MixtureModel model = new LooseModel(x[0], x[1], x[2]);
				return LogLikelihoodFunction.evaluate(model, new edu.uab.ssg.mixomatic.jsci.DefaultProbabilityDensityFunction(), sample);
			}
		};

		Maximisation optimizer = new Maximisation();
		double[] start = new double[] { 0.8, 1., 1.6 }; // Fixed starting point.
		double ftol = 1E-3; // Reduced tolerance.
		// Set up constraints.
		optimizer.addConstraint(0, -1, 0.); // lambda0 > 0
		optimizer.addConstraint(0, 1, 1.); // lambda0 < 1
		optimizer.addConstraint(1, -1, 0.); // r > 0
		optimizer.addConstraint(2, -1, 0.); // s > 0
		
		optimizer.nelderMead(f, start, ftol); // Begin optimization.

		double[] tmp = optimizer.getParamValues();
		return new DefaultEstimate(tmp[0], tmp[1], tmp[2], sample);
	}
}
