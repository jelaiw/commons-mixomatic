package edu.uab.ssg.mixomatic.jmsl;

import edu.uab.ssg.mixomatic.ProbabilityDensityFunction;
import edu.uab.ssg.mixomatic.LogLikelihoodFunction;
import edu.uab.ssg.mixomatic.MixtureModel;
import java.util.*;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 4/5/06
 */

/* package private */ final class RestrictedConfiguration extends AbstractConfiguration implements BoundedOptimizer.Configuration {
	/* package private */ RestrictedConfiguration() {
		// Note the JMSL API hacks for > 0 and positive infinity.
		lowerBounds = new double[] { 0., 1.7e-8, 1. + 1.7e-8 }; // lambda0, r, s
		upperBounds = new double[] { 1., 1. - 1.7e-8, 1.79e308 };
		// Define the grid search space.
		lambda0 = new double[] { 0.6, 0.8, 0.9 };
		r = new double[] { 0.5, 0.75, 0.9 };
		s = new double[] { 1.25, 1.75, 2.25, 2.75, 3.25, 3.75 };
	}
}
