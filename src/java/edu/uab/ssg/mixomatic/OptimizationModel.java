package edu.uab.ssg.mixomatic;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 1/29/04
 */

public interface OptimizationModel {
	LowerBounds getLowerBounds();
	UpperBounds getUpperBounds();
	InitialGuess getGuess(double[] x);

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

	public interface InitialGuess {
		double getLambda0();
		double getR();
		double getS();
	}
}
