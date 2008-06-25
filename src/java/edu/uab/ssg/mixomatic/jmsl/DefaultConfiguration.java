package edu.uab.ssg.mixomatic.jmsl;

import edu.uab.ssg.mixomatic.ProbabilityDensityFunction;
import edu.uab.ssg.mixomatic.LogLikelihoodFunction;
import edu.uab.ssg.mixomatic.MixtureModel;
import java.util.*;

/**
 *	@author Jelai Wang
 */

/* package private */ final class DefaultConfiguration extends AbstractConfiguration implements BoundedOptimizer.Configuration {
	/* package private */ DefaultConfiguration() {
		// Note the JMSL API hacks for > 0 and positive infinity.
		lowerBounds = new double[] { 0., 1.7e-8, 1.7e-8 }; // lambda0, r, s
		upperBounds = new double[] { 1., 1.79e308, 1.79e308 };
		// Define grid search space.
		lambda0 = new double[] { 0.6, 0.8, 0.9 };
		r = new double[] { 0.5, 1., 1.5, 2. };
		s = new double[] { 0.75, 1.75, 2.75, 3.75 };
	}
}
