package edu.uab.ssg.mixomatic;

/**
 * This checked exception is thrown when a MixtureModel.Estimator
 * implementation is unable to estimate the parameters for a
 * given sample distribution of p-values.
 *
 * This can occur for a distribution of p-values with little or no
 * "signal", that is, the shape of the distribution is close to uniform,
 * because many estimator implementations rely on mathematical optimization,
 * which can have problems converging on a maximimum when the likelihood
 * surface is "flat".
 *
 * @author Jelai Wang
 * @version $Rev$ $LastChangedDate$ $LastChangedBy$ 1/7/05
 */

public class MixomaticException extends Exception {
	protected double[] sample;

	/**
	 * Constructs an exception with the causal exception from the 
	 * estimator implementation and the sample distribution of p-values
	 * that was used to estimate the mixture model parameters.
	 *
	 * @param cause The exception from the underlying estimator implementation,
	 * if available. The client programmer is expected to "wrap" lower level 
	 * exceptions that cannot be handled in this way.
	 * @param sample The sample distribution of p-values used to estimate
	 * the mixture model parameters.
	 */

	public MixomaticException(Throwable cause, double[] sample) {
		super(cause);
		if (sample == null)
			throw new NullPointerException("sample");
		this.sample = (double[]) sample.clone();	
	}

	/**
	 * Returns the sample distribution of p-values.
	 * @return The sample distribution of p-values.
	 */

	public double[] getSample() { return (double[]) sample.clone(); }
}
