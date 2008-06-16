package edu.uab.ssg.mixomatic.jmsl;

import edu.uab.ssg.mixomatic.MixtureModel;

/**
 * This class implements the mix-o-matic mixture model, but does not 
 * check the model parameters for validity, and is not appropriate 
 * for general purpose use. 
 *
 * It was written to be used by mathematical optimizer implementations 
 * that may ask for the probability density function to be evaluated 
 * at values of lambda0, r, and s that are (slightly) out of bounds 
 * as part of its search algorithm.
 * 
 * See JIRA issue HDB-105 for examples of the MinConNLP optimizer
 * trying a lambda0 value of 1.0000002966479395 on the way to
 * finding a solution.
 *
 * @author Jelai Wang
 */

/* package private */ final class LooseModel implements MixtureModel {
	private double lambda0;
	private double r, s;

	/* package private */ LooseModel(double lambda0, double r, double s) {
		this.lambda0 = lambda0;
		this.r = r;
		this.s = s;
	}

	public double getLambda0() { return lambda0; }
	public double getR() { return r; }
	public double getS() { return s; }
	public String toString() { return lambda0 + " " + r + " " + s; }
}
