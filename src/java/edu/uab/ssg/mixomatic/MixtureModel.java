package edu.uab.ssg.mixomatic;

/**
 * Implementations of this interface represent mix-o-matic mixture
 * models, each defined by parameters lambda0, r, and s.
 *
 * Each implementation models a distribution of p-values as a mixture 
 * of one uniform distribution (specified by lambda0) and one beta 
 * distribution (specified by beta distribution parameters r and s). 
 * More details are available in the paper at 
 * http://dx.doi.org/10.1016/S0167-9473(01)00046-9.
 *
 * @author Jelai Wang
 */

public interface MixtureModel {
	/**
	 * Returns lambda0, the area "under the curve" for the uniform component
	 * of the mixture model.
	 *
	 * @return A value between zero and one representing the proportion 
	 * of the total area of the uniform component.
	 */

	double getLambda0();

	/**
	 * Returns r, the first distributional shape parameter for the beta
	 * component of the mixture model.
	 *
	 * @return A positive number representing the first beta parameter.
	 */

	double getR();

	/**
	 * Returns s, the second distributional shape parameter for the beta
	 * component of the mixture model.
	 *
	 * @return A positive number representing the second beta parameter.
	 */

	double getS();

	/**
	 * Implementations of this interface represent mixture models
	 * estimated from sample data using the mix-o-matic procedure.
	 */

	interface Estimate extends MixtureModel {

		/**
		 * Returns the sample distribution of p-values used by the 
		 * mix-o-matic procedure to estimate the lambda0, r, and s 
		 * parameters of the mixture model.
		 *
		 * @return The sample distribution of p-values.
		 */

		double[] getSample();
	}

	/**
	 * Implementations of this interface represent mix-o-matic estimators,
	 * that is, given a sample distribution of p-values, an estimator
	 * will apply the mix-o-matic procedure and return estimates of 
	 * the mixture model parameters lambda0, r, and s.
	 */

	interface Estimator {

		/**
		 * Estimate the parameters of the mixture model by applying the
		 * mix-o-matic procedure to the sample distribution of p-values.
		 *
		 * @param sample The sample distribution of p-values as a double
		 * array, where each double in the array takes a value between 
		 * zero and one.
		 * @return The estimated mixture model.
		 */

		Estimate estimateParameters(double[] sample) throws MixomaticException;
	}
}
