package edu.uab.ssg.mixomatic.jmsl;

import edu.uab.ssg.mixomatic.*;
import edu.uab.ssg.mixomatic.helper.LooseModel;
import edu.uab.ssg.mixomatic.helper.DefaultEstimate;
import com.imsl.math.MinConNLP;
import com.imsl.IMSLException;

/**
 * Another maximum likelihood estimator of the mix-o-matic mixture model 
 * using mathematical optimization.
 *
 * Like the BoundedOptimizer class, this implementation is based on 
 * the MinConNLP class available in the JMSL library at 
 * http://www.vni.com. However, it uses both constraint functions 
 * and lower and upper bounds to define the set of feasible points 
 * for optimization. Despite effort devoted to tuning and tweaking, this
 * implementation does not perform as well as the BoundedOptimizer.
 *
 * @author Jelai Wang
 */
public final class ConstrainedOptimizer implements MixtureModel.Estimator {
	/**
	 * Constructs the optimizer.
	 */
	public ConstrainedOptimizer() {
	}

	public MixtureModel.Estimate estimateParameters(double[] sample) throws MixomaticException {
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

		// Four constraints, no equality constraints, three variables.
		MinConNLP solver = new MinConNLP(4, 0, 3);
		// Set "hard" bounds on parameter estimates.
		// Note the JMSL API hacks for > 0 and positive infinity.
		solver.setXlowerBound(new double[] { 0., 1.7e-8, 1.7e-8 });
		solver.setXupperBound(new double[] { 1., 1.79e308, 1.79e308 });
		// Pick a "reasonable" fixed starting point. Or use grid search??
		solver.setGuess(new double[] { 0.8, 0.9, 1.5 });
		// Workaround for JIRA issue HDB-9.
//		solver.setFunctionPrecision(2.2e-12); 
		// Don't need to set this any more after setting bounds?
//		solver.setTolerance(1E-3);
		
		double[] tmp = null;
		try {
			tmp = solver.solve(new MinConNLP.Function() {
				public double f(double[] x, int iact, boolean[] ierr) {
					ierr[0] = false;
					if (iact == 0) {
						MixtureModel model = new LooseModel(x[0], x[1], x[2]);
						ProbabilityDensityFunction function = new edu.uab.ssg.mixomatic.jsci.DefaultProbabilityDensityFunction(); // Faster than JMSL implementation.
						double L = LogLikelihoodFunction.evaluate(model, function, copy);
						return -L; // NOTE SIGN.
					}
					else { // Define feasible points with constraint functions.
						if (iact == 1) {
//							System.out.println("Constraint #1: " + x[0]);
							return x[0];
						}
						else if (iact == 2) {
//							System.out.println("Constraint #2: " + (1. - x[0]));
							return 1. - x[0];
						}
						else if (iact == 3) {
//							System.out.println("Constraint #3: " + x[1]);
							return x[1];
						}
						else if (iact == 4) {
//							System.out.println("Constraint #4: " + x[2]);
							return x[2];
						}
						else {
							ierr[0] = true;
							return Double.NaN;
						}
					}
				}
			});
		}
		catch (IMSLException e) { // See MinConNLP.solve() API.
			throw new MixomaticException(e, copy);
		}
		return new DefaultEstimate(tmp[0], tmp[1], tmp[2], copy);
	}
}
