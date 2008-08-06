package edu.uab.ssg.mixomatic.power;

import edu.uab.ssg.mixomatic.MixtureModel;
import com.imsl.stat.*;

/**
 * This class implements the parametric bootstrap procedure, described in
 * the paper at http://dx.doi.org/10.1191/0962280204sm369ra, for obtaining
 * estimates of the expected discovery rate (EDR), "akin but not identical
 * to the notion of power", proportion of true positives (TP), and 
 * proportion of true negatives (TN).
 *
 * The client programmer is expected to have conducted a valid two-group
 * test of hypothesis obtaining a sample distribution of p-values. Then,
 * a mixture model is fitted, probably using a MixtureModel.Estimator,
 * to this distribution of p-values. From there, an instance of this
 * class can be constructed to estimate EDR, TP, and TN.
 *
 * @author Jelai Wang
 * @version $Rev$ $LastChangedDate$ $LastChangedBy$ 5/22/2006
 */

public final class CombinedEstimator {
	private MixtureModel model;
	private int numberOfPValues; // Referred to as 'k' in the spec.
	private double N; // Referred to as 'n' in the paper.

	/**
	 * Constructs an estimator for EDR, TP, and TN.
	 *
	 * @param model The mixture model, estimated from fitting the sample 
	 * distribution of p-values.
	 * @param numberOfPValues The total number of p-values. This quantity is
	 * called "k" in the reference paper.
	 * @param N1 The sample size of the first group in the two-group hypothesis test.
	 * @param N2 The sample size of the second group in the two-group hypothesis test.
	 */
	public CombinedEstimator(MixtureModel model, int numberOfPValues, int N1, int N2) {
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
		if (N1 != N2) // Calculate 'equivalent' equal group sample size.
			this.N = 2. / (1. / N1 + 1. / N2);
		else
			this.N = N1;	
	}

	/**
	 * Calculate estimates for EDR, TP, and TN using the parametric bootstrap
	 * procedure described in http://dx.doi.org/10.1191/0962280204sm369ra.
	 *
	 * @param newN The sample size at which to calculate the estimates. This
	 * quantity is called "n*" in the reference paper.
	 * @param threshold The threshold at which to calculate the estimates. This
	 * quantity is called &tau; in the reference paper.
	 * @param numberOfIterations The number of bootstrap iterations to perform.
	 * This tuning parameter is called "M" in the reference paper and was set
	 * to M = 100 in the three illustrative examples presented.
	 */
	public Estimates calculateEstimates(int newN, double threshold, int numberOfIterations) {
		if (newN < 1)
			throw new IllegalArgumentException(String.valueOf(newN));
		if (threshold < 0. || threshold > 1.)
			throw new IllegalArgumentException(String.valueOf(threshold));
		if (numberOfIterations < 1)
			throw new IllegalArgumentException(String.valueOf(numberOfIterations));

		// The 'number of iterations' is equal to the quantity 'M' in the paper.
		double[] tp = new double[numberOfIterations];
		double[] tn = new double[numberOfIterations];
		double[] edr = new double[numberOfIterations];

		for (int i = 0; i < numberOfIterations; i++) {
			int[] counts = bootstrap(newN, threshold);
			int A = counts[0], B = counts[1], C = counts[2], D = counts[3];
			tp[i] = ((double) D) / (C + D);
			tn[i] = ((double) A) / (A + B);
			edr[i] = ((double) D) / (B + D);
		}
		return new Estimates(newN, threshold, tp, tn, edr);
	}

	/**
	 * An instance of this class represents the estimates of EDR, TP, and TN
	 * from a call to calculateEstimates().
	 */
	public final class Estimates {
		private int newN;
		private double threshold;
		private double[] tp, tn, edr;

		private Estimates(int newN, double threshold, double[] tp, double[] tn, double[] edr) {
			this.newN = newN;
			this.threshold = threshold;
			this.tp = tp; this.tn = tn; this.edr = edr;
		}

		/**
		 * Returns the sample size called "n" in the calculation.
		 * This is the sample size of each group in the actual data 
		 * in the hypothesis test. If the sample sizes of these
		 * groups are not equal, an "equivalent" equal group sample
		 * size is calculated and used.
		 */
		public double getEquivalentSampleSize() { return N; }

		/**
		 * Returns the "extrapolated" sample size at which the estimates
		 * were calculated.
		 */
		public int getExtrapolatedSampleSize() { return newN; }

		/**
		 * Returns the threshold at which the estimates were calculated.
		 */
		public double getThreshold() { return threshold; }

		/**
		 * Returns the point estimate for the proportion of true positives (TP).
		 */
		public double getTP() { return Summary.mean(tp); }

		/**
		 * Returns the point estimate for the proportion of true negatives (TN).
		 */
		public double getTN() { return Summary.mean(tn); }

		/**
		 * Returns the point estimate for the expected discovery rate (EDR).
		 */
		public double getEDR() { return Summary.mean(edr); }

		/**
		 * Returns the standard error for the proportion of true positives (TP).
		 */
		public double getStandardErrorForTP() { return Summary.sampleStandardDeviation(tp); }

		/**
		 * Returns the standard error for the proportion of true negatives (TN).
		 */
		public double getStandardErrorForTN() { return Summary.sampleStandardDeviation(tn); }

		/**
		 * Returns the standard error for the expected discovery rate (EDR).
		 */
		public double getStandardErrorForEDR() { return Summary.sampleStandardDeviation(edr); }

		public String toString() {
			String EOL = System.getProperty("line.separator");
			StringBuffer buffer = new StringBuffer();
			buffer.append(model.toString()).append(EOL);
			buffer.append("Number of p-values = ").append(numberOfPValues).append(EOL);
			buffer.append("N = ").append(getEquivalentSampleSize()).append(EOL);
			buffer.append("Extrapolated N = ").append(getExtrapolatedSampleSize()).append(EOL);
			buffer.append("threshold = ").append(getThreshold()).append(EOL);
			buffer.append("TP = ").append(getTP()).append(EOL);
			buffer.append("TN = ").append(getTN()).append(EOL);
			buffer.append("EDR = ").append(getEDR()).append(EOL);
			return buffer.toString().trim();
		}
	}

	/* package private */ int[] bootstrap(int newN, double threshold) {
		Random random = new Random();
		double lambda0 = model.getLambda0();
		double r = model.getR();
		double s = model.getS();
		// Number of genes for which the p-value comes from the uniform.
		int u = -1; // A + C in paper.
		if (lambda0 > 0. && lambda0 < 1.)
			u = random.nextBinomial(numberOfPValues, lambda0); 
		else if (lambda0 == 0.) // See HDB-104 in JIRA.
			u = 0;
		else if (lambda0 == 1.)
			u = numberOfPValues;
		else
			throw new IllegalStateException(String.valueOf(lambda0));
			
		// p-values drawn from the uniform distribution.
		double[] pValuesFromUniform = new double[u];
		for (int i = 0; i < pValuesFromUniform.length; i++) {
			pValuesFromUniform[i] = random.nextDouble();
		}

		// Number of genes for which the p-value comes from beta(r,s).
		int b = numberOfPValues - u; // B + D in paper.
		// p-values drawn from the beta(r,s) distribution.
		double[] pValuesFromBeta = new double[b];
		for (int i = 0; i < pValuesFromBeta.length; i++) {
			pValuesFromBeta[i] = random.nextBeta(r, s);
		}

		double[] adjustedPValues = new double[pValuesFromBeta.length];
		for (int i = 0; i < adjustedPValues.length; i++) {
			adjustedPValues[i] = adjustT(pValuesFromBeta[i], N, newN);
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

	private double adjustT(double p, double N, double newN) { // Note N alias.
		double df = 2. * N - 2.;
		double t = Cdf.inverseStudentsT(p / 2., df); // Adjust for two tails.
		double newDF = 2. * newN - 2.;
		double newT = t * Math.sqrt(newN / N);
		return 2. * Cdf.studentsT(newT, newDF); // Adjust for two tails.
	}
}
