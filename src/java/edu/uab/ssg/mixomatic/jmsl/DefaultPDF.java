package edu.uab.ssg.mixomatic.jmsl;

import edu.uab.ssg.mixomatic.ProbabilityDensityFunction;
import edu.uab.ssg.mixomatic.MixtureModel;
import com.imsl.stat.Cdf;

/**
 *  This class implements the mix-o-matic probability density function,
 *  defined by model parameters lambda0, r, and s, using the beta
 *  probability density function available in the JMSL.
 *
 *  This class strictly checks the model parameters for validity and is
 *  appropriate for general purpose use.
 *
 *	@author Jelai Wang
 *	@version $Rev$ $LastChangedDate$ $LastChangedBy$ 1/6/05
 */

public final class DefaultPDF implements ProbabilityDensityFunction {
	private LoosePDF pdf;

	public DefaultPDF(double lambda0, double r, double s) {
		if (lambda0 < 0. || lambda0 > 1.)
			throw new IllegalArgumentException(String.valueOf(lambda0));
		if (r < 0.)
			throw new IllegalArgumentException(String.valueOf(r));
		if (s < 0.)
			throw new IllegalArgumentException(String.valueOf(s));
		this.pdf = new LoosePDF(lambda0, r, s);
	}

	public double evaluate(double x) { return pdf.evaluate(x); }
	public MixtureModel getModel() { return pdf.getModel(); }
}
