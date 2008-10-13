package edu.uab.ssg.mixomatic;

/**
 * A mix-o-matic mixture model.
 *
 * Implementations should model a distribution of p-values as a mixture 
 * of one uniform distribution (specified by lambda0) and one beta 
 * distribution (specified by beta distribution parameters r and s). 
 *
 * More details are available in the paper at <a href="http://dx.doi.org/10.1016/S0167-9473(01)00046-9">http://dx.doi.org/10.1016/S0167-9473(01)00046-9</a>.
 *
 * @author Jelai Wang
 */
public interface MixtureModel {
	/**
	 * Returns lambda0, the area "under the curve" for the uniform component
	 * of this mixture model.
	 *
	 * @return A value between zero and one representing the proportion 
	 * of the total area in the uniform component.
	 */
	double getLambda0();

	/**
	 * Returns r, the first shape parameter for the beta component 
	 * of this mixture model.
	 *
	 * @return A positive number representing the first beta parameter.
	 */
	double getR();

	/**
	 * Returns s, the second shape parameter for the beta component 
	 * of this mixture model.
	 *
	 * @return A positive number representing the second beta parameter.
	 */
	double getS();

	/**
	 * A mixture model estimated from sample data using the 
	 * mix-o-matic procedure.
	 */
	interface Estimate extends MixtureModel {

		/**
		 * Returns the sample distribution of p-values used by the 
		 * mix-o-matic procedure to estimate the lambda0, r, and s 
		 * parameters of the mixture model.
		 */
		double[] getSample();
	}

	/**
	 * A mix-o-matic mixture model estimator.
	 *
	 * Given a sample distribution of p-values, an estimator
	 * will apply the mix-o-matic procedure and return estimates of 
	 * the mixture model parameters lambda0, r, and s.
	 */
	interface Estimator {

		/**
		 * Estimates the parameters of the mixture model by applying the
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
