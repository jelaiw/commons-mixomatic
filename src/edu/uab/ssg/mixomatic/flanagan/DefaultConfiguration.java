package edu.uab.ssg.mixomatic.flanagan;

import edu.uab.ssg.mixomatic.helper.*;
import edu.uab.ssg.mixomatic.ProbabilityDensityFunction;
import edu.uab.ssg.mixomatic.LogLikelihoodFunction;
import edu.uab.ssg.mixomatic.MixtureModel;
import java.util.*;

/**
 *	@author Jelai Wang
 */

/* package private */ final class DefaultConfiguration extends AbstractConfiguration implements OptimizerConfiguration {
	/* package private */ DefaultConfiguration() {
		lowerBounds = new double[] { 0., 0., 0. }; // lambda0, r, s
		upperBounds = new double[] { 1., Double.MAX_VALUE, Double.MAX_VALUE };
		// Define grid search space.
		lambda0 = new double[] { 0.6, 0.8, 0.9 };
		r = new double[] { 0.5, 1., 1.5, 2. };
		s = new double[] { 0.75, 1.75, 2.75, 3.75 };
	}
}
