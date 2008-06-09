package edu.uab.ssg.mixomatic;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 4/4/06
 */

public final class LogLikelihoodFunction {
	private ProbabilityDensityFunction f;

	public LogLikelihoodFunction(double lambda0, double r, double s) {
		if (lambda0 < 0. || lambda0 > 1.0000002966479395) // See bug #157.
			throw new IllegalArgumentException(String.valueOf(lambda0));
		if (r < 0.)
			throw new IllegalArgumentException(String.valueOf(r));
		if (s < 0.)
			throw new IllegalArgumentException(String.valueOf(s));
		this.f = new edu.uab.ssg.mixomatic.jmsl.DefaultPDF(lambda0, r, s);
	}

	public double getValue(double[] x) {
		if (x == null)
			throw new NullPointerException("x");
		if (x.length < 1)
			throw new IllegalArgumentException(String.valueOf(x.length));
		double sum = 0.;
		for (int i = 0; i < x.length; i++) {
			sum += Math.log(f.evaluate(x[i]));
		}
		return sum;
	}

	public double getLambda0() { return f.getModel().getLambda0(); }
	public double getR() { return f.getModel().getR(); }
	public double getS() { return f.getModel().getS(); }
}
