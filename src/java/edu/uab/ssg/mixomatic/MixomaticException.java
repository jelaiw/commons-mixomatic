package edu.uab.ssg.mixomatic;

/**
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 1/7/05
 */

public final class MixomaticException extends Exception {
	private OptimizationModel model;
	private double[] pValues;

	public MixomaticException(Throwable cause, OptimizationModel model, double[] pValues) {
		super(cause);
		if (model == null)
			throw new NullPointerException("model");
		if (pValues == null)
			throw new NullPointerException("pValues");
		this.model = model;	
		this.pValues = (double[]) pValues.clone();	
	}

	public OptimizationModel getModel() { return model; }
	public double[] getPValues() { return (double[]) pValues.clone(); }
}
