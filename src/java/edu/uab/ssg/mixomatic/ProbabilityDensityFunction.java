package edu.uab.ssg.mixomatic;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 1/6/05
 */

public interface ProbabilityDensityFunction {
	double evaluate(MixtureModel model, double x);
}
