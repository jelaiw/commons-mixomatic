package edu.uab.ssg.mixomatic.flanagan;

import edu.uab.ssg.mixomatic.*;
import edu.uab.ssg.mixomatic.helper.*;
import flanagan.math.*;

/**
 * This class implements a maximum likelihood estimator of the mixture 
 * model parameters using mathematical optimization.
 *
 * Specifically, this implementation is based on the constrained
 * Nelder-Mead Maximisation class in the Flanagan Java Scientific
 * Library at http://www.ee.ucl.ac.uk/~mflanaga/java/.
 *
 * @author Jelai Wang
 * @version 7/8/08
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
		OptimizerConfiguration.StartingPoint startingPoint = configuration.findStartingPoint(sample);
		double ftol = 1E-3; // Reduced tolerance.

		OptimizerConfiguration.LowerBounds lb = configuration.getLowerBounds();
		OptimizerConfiguration.UpperBounds ub = configuration.getUpperBounds();
		// Set up constraints, see http://www.ee.ucl.ac.uk/~mflanaga/java/Maximisation.html#constraint for details.
		optimizer.addConstraint(0, -1, lb.getLambda0());
		optimizer.addConstraint(0, 1, ub.getLambda0());
		optimizer.addConstraint(1, -1, lb.getR());
		optimizer.addConstraint(1, 1, ub.getR());
		optimizer.addConstraint(2, -1, lb.getS());
		optimizer.addConstraint(2, 1, ub.getS());

		optimizer.nelderMead(f, new double[] { startingPoint.getLambda0(), startingPoint.getR(), startingPoint.getS() }, ftol);

		double[] tmp = optimizer.getParamValues();
		return new DefaultEstimate(tmp[0], tmp[1], tmp[2], sample);
	}
}
