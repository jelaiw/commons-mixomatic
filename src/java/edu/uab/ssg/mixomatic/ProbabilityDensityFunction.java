package edu.uab.ssg.mixomatic;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 1/6/05
 */

public interface ProbabilityDensityFunction {
	double getValue(double x);
	double getLambda0();
	double getR();
	double getS();
}
