package edu.uab.ssg.mixomatic.power;

import edu.uab.ssg.mixomatic.MixtureModel;

/**
 * This class implements the parametric bootstrap procedure, described in
 * the paper at http://dx.doi.org/10.1191/0962280204sm369ra, for obtaining
 * estimates of the expected discovery rate (EDR), the proportion of true 
 * positives (TP), and the proportion of true negatives (TN).
 *
 * The client programmer is expected to have conducted many, valid tests
 * of hypothesis to obtain a sample distribution of p-values (from those
 * tests) and fitted a mixture model, using a MixtureModel.Estimator,
 * to this distribution of p-values. Then, an instance of this class can be 
 * constructed to estimate EDR, TP, and TN.
 *
 * @author Jelai Wang
 * @version $Rev: 94 $ $LastChangedDate: 2008-08-25 15:58:23 -0500 (Mon, 25 Aug 2008) $ $LastChangedBy: jelaiw $ 5/22/2006
 */

public final class BootstrapEstimator {
	private MixtureModel model;
	private int numberOfPValues; // Referred to as 'k' in the spec.
	private double N; // Referred to as 'n' in the paper.

	/**
	 * Constructs an estimator given sample size parameters from 
	 * a two-group hypothesis test.
	 * If N1 and N2 are not equal, an "equivalent" equal group
	 * sample size is calculated and used in subsequent calculations.
	 *
	 * @param model The mixture model, estimated from the sample distribution 
	 * of p-values.
	 * @param numberOfPValues The total number of p-values. This quantity is
	 * called "k" in the reference paper.
	 * @param N1 The sample size of the first group in the hypothesis test.
	 * @param N2 The sample size of the second group in the hypothesis test.
	 */
	public BootstrapEstimator(MixtureModel model, int numberOfPValues, int N1, int N2) {
		if (model == null)
			throw new NullPointerException("model");
		if (numberOfPValues < 1) // Hmm.	
			throw new IllegalArgumentException(String.valueOf(numberOfPValues));
		if (N1 < 1)
			throw new IllegalArgumentException(String.valueOf(N1));
		if (N2 < 1)
			throw new IllegalArgumentException(String.valueOf(N2));
		this.model = model;
		this.numberOfPValues = numberOfPValues;	
		if (N1 != N2) // Calculate the 'equivalent' equal group sample size.
			this.N = 2. / (1. / N1 + 1. / N2);
		else
			this.N = N1;	
	}

	public interface Estimate {
		int getSampleSize();
		double getSignificanceLevel();
		double getTP();
		double getTN();
		double getEDR();
		double getStandardErrorForTP();
		double getStandardErrorForTN();
		double getStandardErrorForEDR();
	}

	public interface PValueAdjuster {
		double adjustPValue(double pvalue, double n, int n_);
	}

	public interface RandomNumberGenerator {
		int nextBinomial(int n, double p);
		double nextUniform();
		double nextBeta(double r, double s);
	}

	public Estimate estimateProportions(int newN, double threshold, int numberOfIterations) {
		if (newN < 1)
			throw new IllegalArgumentException(String.valueOf(newN));
		if (threshold < 0. || threshold > 1.)
			throw new IllegalArgumentException(String.valueOf(threshold));
		if (numberOfIterations < 1)
			throw new IllegalArgumentException(String.valueOf(numberOfIterations));

		double[] tp = new double[numberOfIterations];
		double[] tn = new double[numberOfIterations];
		double[] edr = new double[numberOfIterations];

		// A, B, C, and D are the quantities of interest described in
		// Table 1 in the reference paper.
		for (int i = 0; i < numberOfIterations; i++) {
			int[] counts = bootstrap(newN, threshold);
			int A = counts[0], B = counts[1], C = counts[2], D = counts[3];
			tp[i] = ((double) D) / (C + D);
			tn[i] = ((double) A) / (A + B);
			edr[i] = ((double) D) / (B + D);
		}
		return new DefaultEstimate(newN, threshold, tp, tn, edr);
	}

	/* package private */ int[] bootstrap(int newN, double threshold) {
		RandomNumberGenerator rng = new edu.uab.ssg.mixomatic.power.jmsl.RandomAdapter();
		PValueAdjuster adjuster = new edu.uab.ssg.mixomatic.power.jmsl.TTestPValueAdjuster();
		double lambda0 = model.getLambda0();
		double r = model.getR();
		double s = model.getS();
		// Number of genes for which the p-value comes from the uniform.
		int u = -1; // A + C in paper.
		if (lambda0 > 0. && lambda0 < 1.)
			u = rng.nextBinomial(numberOfPValues, lambda0); 
		else if (lambda0 == 0.) // See HDB-104 in JIRA.
			u = 0;
		else if (lambda0 == 1.)
			u = numberOfPValues;
		else
			throw new IllegalStateException(String.valueOf(lambda0));
			
		// p-values drawn from the uniform distribution.
		double[] pValuesFromUniform = new double[u];
		for (int i = 0; i < pValuesFromUniform.length; i++) {
			pValuesFromUniform[i] = rng.nextUniform();
		}

		// Number of genes for which the p-value comes from beta(r,s).
		int b = numberOfPValues - u; // B + D in paper.
		// p-values drawn from the beta(r,s) distribution.
		double[] pValuesFromBeta = new double[b];
		for (int i = 0; i < pValuesFromBeta.length; i++) {
			pValuesFromBeta[i] = rng.nextBeta(r, s);
		}

		double[] adjustedPValues = new double[pValuesFromBeta.length];
		for (int i = 0; i < adjustedPValues.length; i++) {
			adjustedPValues[i] = adjuster.adjustPValue(pValuesFromBeta[i], N, newN);
		}

		// Calculate estimates for A, B, C, and D as defined in the paper.
		int A = 0;
		for (int i = 0; i < pValuesFromUniform.length; i++) {
			if (pValuesFromUniform[i] > threshold) A++;
		}
		int D = 0;
		for (int i = 0; i < adjustedPValues.length; i++) {
			if (adjustedPValues[i] < threshold) D++;
		}
		// The variable declared as 'b' == B + D in the paper.
		int B = b - D;
		// The variable declared as 'u' == A + C in the paper.
		int C = u - A;

		return new int[] { A, B, C, D };
	}
}
