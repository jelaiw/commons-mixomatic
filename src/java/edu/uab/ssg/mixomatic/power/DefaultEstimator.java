package edu.uab.ssg.mixomatic.power;

import edu.uab.ssg.mixomatic.MixtureModel;

/**
 * This class implements the parametric bootstrap procedure, described in
 * the paper at <a href="http://dx.doi.org/10.1191/0962280204sm369ra">http://dx.doi.org/10.1191/0962280204sm369ra</a>, for obtaining
 * estimates of the expected discovery rate (EDR), the proportion of true 
 * positives (TP), and the proportion of true negatives (TN).
 *
 * @author Jelai Wang
 */

public final class DefaultEstimator implements BootstrapEstimator {
	BootstrapEstimator.Configuration configuration;

	/**
	 * Construct an estimator with M = 100 and default choices for p-value 
	 * adjuster and random number generator implementations.
	 */
	public DefaultEstimator() {
		this(100, new edu.uab.ssg.mixomatic.power.jmsl.TTestPValueAdjuster(), new edu.uab.ssg.mixomatic.power.colt.RandomAdapter());
	}

	/**
	 * Construct an estimator with a custom configuration.
	 * @param numberOfIterations The number of bootstrap iterations.
	 * Called M in the paper.
	 */
	public DefaultEstimator(int numberOfIterations, BootstrapEstimator.PValueAdjuster adjuster, BootstrapEstimator.RandomNumberGenerator rng) {
		this.configuration = new DefaultConfiguration(numberOfIterations, adjuster, rng);
	}

	public BootstrapEstimator.Configuration getConfiguration() { return configuration; }

	public Estimate estimateProportions(MixtureModel.Estimate model, int N1, int N2, int n_, double significanceLevel) {
		if (model == null)
			throw new NullPointerException("model");
		if (N1 < 1)
			throw new IllegalArgumentException(String.valueOf(N1));
		if (N2 < 1)
			throw new IllegalArgumentException(String.valueOf(N2));
		if (n_ < 1)
			throw new IllegalArgumentException(String.valueOf(n_));
		if (significanceLevel < 0. || significanceLevel > 1.)
			throw new IllegalArgumentException(String.valueOf(significanceLevel));
		double n = calculateEqualGroupSampleSize(N1, N2);
		int numberOfIterations = configuration.getNumberOfIterations();
		double[] tp = new double[numberOfIterations];
		double[] tn = new double[numberOfIterations];
		double[] edr = new double[numberOfIterations];

		// A, B, C, and D are the quantities of interest described in
		// Table 1 in the reference paper.
		for (int i = 0; i < numberOfIterations; i++) {
			int[] counts = bootstrap(model, n, n_, significanceLevel);
			int A = counts[0], B = counts[1], C = counts[2], D = counts[3];
			tp[i] = ((double) D) / (C + D);
			tn[i] = ((double) A) / (A + B);
			edr[i] = ((double) D) / (B + D);
		}
		return new DefaultEstimate(configuration, model, n, n_, significanceLevel, tp, tn, edr);
	}

	/* package private */ double calculateEqualGroupSampleSize(int N1, int N2) {
		if (N1 != N2) 
			return 2. / (1. / N1 + 1. / N2);
		else
			return N1;
	}

	/* package private */ int[] bootstrap(MixtureModel.Estimate model, double n, int n_, double significanceLevel) {
		RandomNumberGenerator rng = configuration.getRandomNumberGenerator();
		PValueAdjuster adjuster = configuration.getPValueAdjuster();
		int numberOfPValues = model.getSample().length;
		double lambda0 = model.getLambda0();
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

		double r = model.getR();
		double s = model.getS();
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
