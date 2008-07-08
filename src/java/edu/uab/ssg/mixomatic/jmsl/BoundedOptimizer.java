package edu.uab.ssg.mixomatic.jmsl;

import edu.uab.ssg.mixomatic.*;
import edu.uab.ssg.mixomatic.helper.LooseModel;
import edu.uab.ssg.mixomatic.helper.DefaultEstimate;
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
	 * This interface represents the lower bounds that the mixture model
	 * parameter estimates are allowed to take.
	 */
	public interface LowerBounds {
		/**
		 * Return the lower bound of &lambda;&#8320;.
		 */
		double getLambda0();

		/**
		 * Return the lower bound of r.
		 */
		double getR();

		/**
		 * Return the lower bound of s.
		 */
		double getS();
	}

	/**
	 * This interface represents the upper bounds that the mixture model
	 * parameter estimates are allowed to take.
	 */
	public interface UpperBounds {
		/**
		 * Return the upper bound of &lambda;&#8320;.
		 */
		double getLambda0();

		/**
		 * Return the upper bound of r.
		 */
		double getR();

		/**
		 * Return the upper bound of s.
		 */
		double getS();
	}

	/**
	 * This interface represents the starting point of the optimizer.
	 */
	public interface StartingPoint {
		/**
		 * Return the starting value of &lambda;&#8320;.
		 */
		double getLambda0();

		/**
		 * Return the starting value of r.
		 */
		double getR();

		/**
		 * Return the starting value of s.
		 */
		double getS();
	}

	/**
	 * This interface represents the set of feasible points that the
	 * mixture model parameter estimates are allowed to take by "bundling"
	 * together a set of lower and upper bounds and a mechanism for
	 * finding a starting point given a sample distribution of p-values.
	 */
	public interface Configuration {
		/**
		 * Return the lower bounds of &lambda;&#8320;, r, and s.
		 */
		LowerBounds getLowerBounds();

		/**
		 * Return the upper bounds of &lambda;&#8320;, r, and s.
		 */
		UpperBounds getUpperBounds();

		/**
		 * Find and return a "reasonable" starting point for the optimizer.
		 * This is typically implemented as a grid search, method of
		 * moments estimate, or pre-specified points (ignoring the
		 * sample distribution of p-values).
		 * @param sample The sample distribution of p-values.
		 * @return The starting point for the optimizer.
		 */
		StartingPoint findStartingPoint(double[] sample);
	}

	/**
	 * This is the default configuration, with bounds specified by 
	 * <tt>0 &lt; &lambda;&#8320; &lt; 1</tt>,
	 * <tt>r &gt; 0</tt>,
	 * <tt> s &gt; 0</tt>,
	 * and a custom grid search to find a starting point.
	 */
	public static final BoundedOptimizer.Configuration DEFAULT = new DefaultConfiguration();

	/**
	 * This is a "restricted" configuration, with bounds specified by 
	 * <tt>0 &lt; &lambda;&#8320; &lt; 1</tt>,
	 * <tt>0 &lt; r &lt; 1</tt>,
	 * <tt> s &gt; 1</tt>,
	 * and a custom grid search to find a starting point.
	 * This implementation attempts to restrict the set of feasible points
	 * such that the optimizer will tend to converge on a mixture model
	 * that makes theoretical sense even if the sample distribution of
	 * p-values has an unusual shape.
	 */
	public static final BoundedOptimizer.Configuration RESTRICTED = new RestrictedConfiguration();

	private Configuration configuration;

	/**
	 * Constructs a mix-o-matic optimizer using the default configuration.
	 */
	public BoundedOptimizer() {
		this.configuration = DEFAULT;
	}

	/**
	 * Constructs a mix-o-matic optimizer using a user-supplied configuration.
	 */
	public BoundedOptimizer(Configuration configuration) {
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
		BoundedOptimizer.StartingPoint guess = configuration.findStartingPoint(copy);
		solver.setGuess(new double[] { guess.getLambda0(), guess.getR(), guess.getS() });
		// Tell solver about set of feasible points.
		BoundedOptimizer.LowerBounds lb = configuration.getLowerBounds();
		solver.setXlowerBound(new double[] { lb.getLambda0(), lb.getR(), lb.getS() });
		BoundedOptimizer.UpperBounds ub = configuration.getUpperBounds();
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
