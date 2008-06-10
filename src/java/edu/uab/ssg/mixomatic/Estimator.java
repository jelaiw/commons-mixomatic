package edu.uab.ssg.mixomatic;

import com.imsl.math.MinConNLP;
import com.imsl.IMSLException;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 4/10/06
 */

public final class Estimator {
	private OptimizationModel model;

	public Estimator(OptimizationModel model) {
		if (model == null)
			throw new NullPointerException("model");
		this.model = model;	
	}

	public OptimizationModel getModel() { return model; }

	public Estimate getEstimate(double[] pValues) throws MixomaticException {
		if (pValues == null)
			throw new NullPointerException("pValues");
		if (pValues.length < 1)
			throw new IllegalArgumentException(String.valueOf(pValues.length));
		// Make defensive copy.	
		final double[] copyOfPValues = (double[]) pValues.clone(); 
		// Additional validation of p-values.
		for (int i = 0; i < copyOfPValues.length; i++) {
			if (copyOfPValues[i] < 0. || copyOfPValues[i] > 1.)
				throw new IllegalArgumentException(i + ", " + copyOfPValues[i]);
			if (Double.isNaN(copyOfPValues[i]))	
				throw new IllegalArgumentException(i + ", " + copyOfPValues[i]);
		}

		MinConNLP solver = new MinConNLP(0, 0, 3);
		// Set up the solver constraints.
		OptimizationModel.InitialGuess guess = model.getGuess(copyOfPValues);
		solver.setGuess(new double[] { guess.getLambda0(), guess.getR(), guess.getS() });
		OptimizationModel.LowerBounds lb = model.getLowerBounds();
		solver.setXlowerBound(new double[] { lb.getLambda0(), lb.getR(), lb.getS() });
		OptimizationModel.UpperBounds ub = model.getUpperBounds();
		solver.setXupperBound(new double[] { ub.getLambda0(), ub.getR(), ub.getS() });
		// Workaround for bug #9.
		solver.setFunctionPrecision(2.2e-12); 
		
		double[] tmp = null;
		try {
			tmp = solver.solve(new MinConNLP.Function() {
				public double f(double[] x, int iact, boolean[] ierr) {
					ProbabilityDensityFunction f = new edu.uab.ssg.mixomatic.jmsl.DefaultPDF(x[0], x[1], x[2]); // lambda0, r, s.
					double d = LogLikelihoodFunction.evaluate(f, copyOfPValues);
					return -d; // NOTE SIGN.
				}
			});
		}
		catch (IMSLException e) { // See MinConNLP.solve() API.
			throw new MixomaticException(e, model, copyOfPValues);
		}

		// Check post-conditions.
		if (tmp[0] < 0. || tmp[0] > 1.) // lambda0
			throw new IllegalStateException(String.valueOf(tmp[0]));
		if (tmp[1] < 0.) // r
			throw new IllegalStateException(String.valueOf(tmp[1]));
		if (tmp[2] < 0.) // s
			throw new IllegalStateException(String.valueOf(tmp[2]));

		return new Estimate(tmp[0], tmp[1], tmp[2], copyOfPValues);
	}

	public final class Estimate {
		private double lambda0, r, s;
		private double[] pValues;

		private Estimate(double lambda0, double r, double s, double[] pValues) {
			this.lambda0 = lambda0;
			this.r = r;
			this.s = s;
			this.pValues = pValues;
		}

		public double getLambda0() { return lambda0; }
		public double getR() { return r; }
		public double getS() { return s; }
		public OptimizationModel getModel() { return model; }
		public double[] getPValues() { return (double[]) pValues.clone(); }

		public String toString() {
			String EOL = System.getProperty("line.separator");
			StringBuffer buffer = new StringBuffer();
			buffer.append("lambda0 = ").append(lambda0).append(EOL);
			buffer.append("r = ").append(r).append(EOL);
			buffer.append("s = ").append(s).append(EOL);
			buffer.append("model = ").append(model.getClass().getName()).append(EOL);
			buffer.append("number of p-values = ").append(pValues.length).append(EOL);
			return buffer.toString().trim();
		}
	}
}
