package edu.uab.ssg.mixomatic.helper;

import edu.uab.ssg.mixomatic.MixtureModel;

/**
 * This helper class implements the mix-o-matic mixture model, but does 
 * not check the model parameters for validity, and is not appropriate 
 * for general purpose use. 
 *
 * It was written to be used by mathematical optimizer implementations 
 * that may ask for the probability density function to be evaluated 
 * at values of lambda0, r, and s that are (slightly) out of bounds 
 * as part of its search algorithm.
 * 
 * <p>See JIRA issue HDB-105 for examples of the MinConNLP optimizer
 * trying a lambda0 value of 1.0000002966479395 on the way to
 * finding a solution.</p>
 *
 * @author Jelai Wang
 */

public final class LooseModel implements MixtureModel {
	private double lambda0;
	private double r, s;

	/**
	 * Constructs a mixture model with a uniform component specified by 
	 * lambda0 and a beta component specified by shape parameters 
	 * r and s, but does not check the values for validity.
	 */

	public LooseModel(double lambda0, double r, double s) {
		this.lambda0 = lambda0;
		this.r = r;
		this.s = s;
	}

	public double getLambda0() { return lambda0; }
	public double getR() { return r; }
	public double getS() { return s; }
	public String toString() { return lambda0 + " " + r + " " + s; }
}
