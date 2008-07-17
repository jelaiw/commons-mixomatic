package edu.uab.ssg.mixomatic.flanagan;

import edu.uab.ssg.mixomatic.helper.*;
import edu.uab.ssg.mixomatic.ProbabilityDensityFunction;
import edu.uab.ssg.mixomatic.LogLikelihoodFunction;
import edu.uab.ssg.mixomatic.MixtureModel;
import java.util.*;

/**
 *	@author Jelai Wang
 *	@version $Rev: 69 $ $LastChangedDate: 2008-07-17 14:23:29 -0500 (Thu, 17 Jul 2008) $ $LastChangedBy: jelai $ 4/5/06
 */

/* package private */ final class RestrictedConfiguration extends AbstractConfiguration implements OptimizerConfiguration {
	/* package private */ RestrictedConfiguration() {
		lowerBounds = new double[] { 0., 0., 1. }; // lambda0, r, s
		upperBounds = new double[] { 1., 1., Double.MAX_VALUE };
		// Define the grid search space.
		lambda0 = new double[] { 0.6, 0.8, 0.9 };
		r = new double[] { 0.5, 0.75, 0.9 };
		s = new double[] { 1.25, 1.75, 2.25, 2.75, 3.25, 3.75 };
	}
}
