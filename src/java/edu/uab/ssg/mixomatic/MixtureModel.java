package edu.uab.ssg.mixomatic;

/**
 * @author Jelai Wang
 */

public interface MixtureModel {
	double getLambda0();
	double getR();
	double getS();

	interface Estimate extends MixtureModel {
		double[] getSample();
	}

	interface Estimator {
		Estimate estimateParameters(double[] sample);
	}
}
