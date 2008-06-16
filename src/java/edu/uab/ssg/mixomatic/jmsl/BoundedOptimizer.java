package edu.uab.ssg.mixomatic.jmsl;

import edu.uab.ssg.mixomatic.*;
import com.imsl.math.MinConNLP;
import com.imsl.IMSLException;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 4/10/06
 */

public final class BoundedOptimizer {
	public interface LowerBounds {
		double getLambda0();
		double getR();
		double getS();
	}

	public interface UpperBounds {
		double getLambda0();
		double getR();
		double getS();
	}

	public interface StartingPoint {
		double getLambda0();
		double getR();
		double getS();
	}

	public interface Configuration {
		LowerBounds getLowerBounds();
		UpperBounds getUpperBounds();
		StartingPoint findStartingPoint(double[] sample);
	}

	public static final BoundedOptimizer.Configuration DEFAULT = new DefaultConfiguration();
	public static final BoundedOptimizer.Configuration RESTRICTED = new RestrictedConfiguration();

	private Configuration config;

	public BoundedOptimizer() {
		this.config = DEFAULT;
	}

	public BoundedOptimizer(Configuration config) {
		if (config == null)
			throw new NullPointerException("config");
		this.config = config;
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

		MinConNLP solver = new MinConNLP(0, 0, 3);
		// Tell solver about starting point.
		BoundedOptimizer.StartingPoint guess = config.findStartingPoint(copy);
		solver.setGuess(new double[] { guess.getLambda0(), guess.getR(), guess.getS() });
		// Tell solver about set of feasible points.
		BoundedOptimizer.LowerBounds lb = config.getLowerBounds();
		solver.setXlowerBound(new double[] { lb.getLambda0(), lb.getR(), lb.getS() });
		BoundedOptimizer.UpperBounds ub = config.getUpperBounds();
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
					ProbabilityDensityFunction function = new edu.uab.ssg.mixomatic.jsci.DefaultPDF(); // Faster than JMSL implementation.
					double L = LogLikelihoodFunction.evaluate(model, function, copy);
					return -L; // NOTE SIGN.
				}
			});
		}
		catch (IMSLException e) { // See MinConNLP.solve() API.
			throw new MixomaticException(e, copy);
		}

		// Check post-conditions.
		if (tmp[0] < 0. || tmp[0] > 1.) // lambda0
			throw new IllegalStateException(String.valueOf(tmp[0]));
		if (tmp[1] < 0.) // r
			throw new IllegalStateException(String.valueOf(tmp[1]));
		if (tmp[2] < 0.) // s
			throw new IllegalStateException(String.valueOf(tmp[2]));

		return new Estimate(tmp[0], tmp[1], tmp[2], copy);
	}

	public final class Estimate implements MixtureModel.Estimate {
		private double lambda0, r, s;
		private double[] sample;

		private Estimate(double lambda0, double r, double s, double[] sample) {
			this.lambda0 = lambda0;
			this.r = r;
			this.s = s;
			this.sample = sample;
		}

		public double getLambda0() { return lambda0; }
		public double getR() { return r; }
		public double getS() { return s; }
		public double[] getSample() { return (double[]) sample.clone(); }

		public String toString() {
			String EOL = System.getProperty("line.separator");
			StringBuffer buffer = new StringBuffer();
			buffer.append("lambda0 = ").append(lambda0).append(EOL);
			buffer.append("r = ").append(r).append(EOL);
			buffer.append("s = ").append(s).append(EOL);
			buffer.append("configuration = ").append(config.getClass().getName()).append(EOL);
			buffer.append("sample size = ").append(sample.length).append(EOL);
			return buffer.toString().trim();
		}
	}
}
