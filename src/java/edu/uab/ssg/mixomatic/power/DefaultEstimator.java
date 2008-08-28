package edu.uab.ssg.mixomatic.power;

import edu.uab.ssg.mixomatic.MixtureModel;

/**
 * This class implements the parametric bootstrap procedure, described in
 * the paper at http://dx.doi.org/10.1191/0962280204sm369ra, for obtaining
 * estimates of the expected discovery rate (EDR), the proportion of true 
 * positives (TP), and the proportion of true negatives (TN).
 *
 * The current implementation performs M = 100 bootstrap iterations and
 * uses JMSL-based implementations for the RandomNumberGenerator and
 * PValueAdjuster.
 *
 * @author Jelai Wang
 * @version $Rev: 94 $ $LastChangedDate: 2008-08-25 15:58:23 -0500 (Mon, 25 Aug 2008) $ $LastChangedBy: jelaiw $ 5/22/2006
 */

public final class DefaultEstimator implements BootstrapEstimator {
	private int numberOfIterations = 100; // Called M in the paper.
	private RandomNumberGenerator rng = new edu.uab.ssg.mixomatic.power.jmsl.RandomAdapter();
	private PValueAdjuster adjuster = new edu.uab.ssg.mixomatic.power.jmsl.TTestPValueAdjuster();

	public Estimate estimateProportions(MixtureModel.Estimate estimate, int N1, int N2, int n_, double significanceLevel) {
		if (estimate == null)
			throw new NullPointerException("estimate");
		if (N1 < 1)
			throw new IllegalArgumentException(String.valueOf(N1));
		if (N2 < 1)
			throw new IllegalArgumentException(String.valueOf(N2));
		if (n_ < 1)
			throw new IllegalArgumentException(String.valueOf(n_));
		if (significanceLevel < 0. || significanceLevel > 1.)
			throw new IllegalArgumentException(String.valueOf(significanceLevel));
		double n = calculateEqualGroupSampleSize(N1, N2);

		double[] tp = new double[numberOfIterations];
		double[] tn = new double[numberOfIterations];
		double[] edr = new double[numberOfIterations];

		// A, B, C, and D are the quantities of interest described in
		// Table 1 in the reference paper.
		for (int i = 0; i < numberOfIterations; i++) {
			int[] counts = bootstrap(estimate, n, n_, significanceLevel);
			int A = counts[0], B = counts[1], C = counts[2], D = counts[3];
			tp[i] = ((double) D) / (C + D);
			tn[i] = ((double) A) / (A + B);
			edr[i] = ((double) D) / (B + D);
		}
		return new DefaultEstimate(n_, significanceLevel, tp, tn, edr);
	}

	/* package private */ double calculateEqualGroupSampleSize(int N1, int N2) {
		if (N1 != N2) 
			return 2. / (1. / N1 + 1. / N2);
		else
			return N1;
	}

	/* package private */ int[] bootstrap(MixtureModel.Estimate estimate, double n, int n_, double significanceLevel) {
		int numberOfPValues = estimate.getSample().length;
		double lambda0 = estimate.getLambda0();
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

		double r = estimate.getR();
		double s = estimate.getS();
		// Number of genes for which the p-value comes from beta(r,s).
		int b = numberOfPValues - u; // B + D in paper.
		// p-values drawn from the beta(r,s) distribution.
		double[] pValuesFromBeta = new double[b];
		for (int i = 0; i < pValuesFromBeta.length; i++) {
			pValuesFromBeta[i] = rng.nextBeta(r, s);
		}

		double[] adjustedPValues = new double[pValuesFromBeta.length];
		for (int i = 0; i < adjustedPValues.length; i++) {
			adjustedPValues[i] = adjuster.adjustPValue(pValuesFromBeta[i], n, n_);
		}

		// Calculate estimates for A, B, C, and D as defined in the paper.
		int A = 0;
		for (int i = 0; i < pValuesFromUniform.length; i++) {
			if (pValuesFromUniform[i] > significanceLevel) A++;
		}
		int D = 0;
		for (int i = 0; i < adjustedPValues.length; i++) {
			if (adjustedPValues[i] < significanceLevel) D++;
		}
		// The variable declared as 'b' == B + D in the paper.
		int B = b - D;
		// The variable declared as 'u' == A + C in the paper.
		int C = u - A;

		return new int[] { A, B, C, D };
	}
}
