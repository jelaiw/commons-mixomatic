package edu.uab.ssg.mixomatic.jmsl;

import edu.uab.ssg.mixomatic.*;
import edu.uab.ssg.mixomatic.helper.*;
import com.imsl.math.MinConNLP;
import com.imsl.IMSLException;

/**
 * This class implements a maximum likelihood estimator of the mixture 
 * model parameters using mathematical optimization.
 *
 * Specifically this implementation is based on the MinConNLP class
 * available in the JMSL library at http://www.vni.com. It allows the
 * client programmer to modify runtime behavior by providing the
 * lower and upper bounds of the mixture model parameters and the
 * starting point of the optimizer.
 *
 * @author Jelai Wang
 * @version $Rev$ $LastChangedDate$ $LastChangedBy$ 4/10/06
 */

public final class BoundedOptimizer implements MixtureModel.Estimator {
	/**
	 * This is the default configuration, with bounds specified by 
	 * <tt>0 &lt; &lambda;&#8320; &lt; 1</tt>,
	 * <tt>r &gt; 0</tt>,
	 * <tt>s &gt; 0</tt>,
	 * and a custom grid search to find a starting point.
	 */
	public static final OptimizerConfiguration DEFAULT = new DefaultConfiguration();

	/**
	 * This is a "restricted" configuration, with bounds specified by 
	 * <tt>0 &lt; &lambda;&#8320; &lt; 1</tt>,
	 * <tt>0 &lt; r &lt; 1</tt>,
	 * <tt>s &gt; 1</tt>,
	 * and a custom grid search to find a starting point.
	 * This implementation attempts to restrict the set of feasible points
	 * such that the optimizer will tend to converge on a mixture model
	 * that makes theoretical sense even if the sample distribution of
	 * p-values has an unusual shape.
	 */
	public static final OptimizerConfiguration RESTRICTED = new RestrictedConfiguration();

	private OptimizerConfiguration configuration;

	/**
	 * Constructs a mix-o-matic optimizer using the default configuration.
	 */
	public BoundedOptimizer() {
		this.configuration = DEFAULT;
	}

	/**
	 * Constructs a mix-o-matic optimizer using a user-supplied configuration.
	 */
	public BoundedOptimizer(OptimizerConfiguration configuration) {
		if (configuration == null)
			throw new NullPointerException("configuration");
		this.configuration = configuration;
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

		MinConNLP solver = new MinConNLP(0, 0, 3); // No constraint functions.
		// Tell solver about starting point.
		OptimizerConfiguration.StartingPoint guess = configuration.findStartingPoint(copy);
		solver.setGuess(new double[] { guess.getLambda0(), guess.getR(), guess.getS() });
		// Tell solver about set of feasible points.
		OptimizerConfiguration.LowerBounds lb = configuration.getLowerBounds();
		solver.setXlowerBound(new double[] { lb.getLambda0(), lb.getR(), lb.getS() });
		OptimizerConfiguration.UpperBounds ub = configuration.getUpperBounds();
		solver.setXupperBound(new double[] { ub.getLambda0(), ub.getR(), ub.getS() });
		// Workaround for JIRA issue HDB-9.
		solver.setFunctionPrecision(2.2e-12); 
		
		double[] tmp = null;
		try {
			tmp = solver.solve(new MinConNLP.Function() {
				public double f(double[] x, int iact, boolean[] ierr) {
					if (iact != 0)
						throw new IllegalStateException(String.valueOf(iact));
					ierr[0] = false;
					MixtureModel model = new LooseModel(x[0], x[1], x[2]);
					ProbabilityDensityFunction function = new edu.uab.ssg.mixomatic.jsci.DefaultProbabilityDensityFunction(); // Faster than JMSL implementation.
					double L = LogLikelihoodFunction.evaluate(model, function, copy);
					return -L; // NOTE SIGN.
				}
			});
		}
		catch (IMSLException e) { // See MinConNLP.solve() API.
			throw new MixomaticException(e, copy);
		}
		return new DefaultEstimate(tmp[0], tmp[1], tmp[2], copy);
	}
}
