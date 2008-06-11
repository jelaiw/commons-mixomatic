package edu.uab.ssg.mixomatic;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 1/7/05
 */

public class MixomaticException extends Exception {
	protected double[] sample;

	public MixomaticException(Throwable cause, double[] sample) {
		super(cause);
		if (sample == null)
			throw new NullPointerException("sample");
		this.sample = (double[]) sample.clone();	
	}

	public double[] getSample() { return (double[]) sample.clone(); }
}
