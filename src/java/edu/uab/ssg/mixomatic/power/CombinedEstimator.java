package edu.uab.ssg.mixomatic.power;

import com.imsl.stat.*;

/**
 * This class implements the parametric bootstrap procedure, described in
 * the paper at http://dx.doi.org/10.1191/0962280204sm369ra, to produce
 * estimates for EDR, TP, and TN.
 *
 * @author Jelai Wang
 * @version $Rev$ $LastChangedDate$ $LastChangedBy$ 5/22/2006
 */

public final class CombinedEstimator {
	private double lambda0, r, s;
	private int numberOfPValues; // Referred to as 'k' in the spec.
	private int N1, N2;
	private double N; // Referred to as 'n' in the paper.

	public CombinedEstimator(double lambda0, double r, double s, int numberOfPValues, int N1, int N2) {
		if (lambda0 < 0. || lambda0 > 1.)
			throw new IllegalArgumentException(String.valueOf(lambda0));
		if (r < 0.)
			throw new IllegalArgumentException(String.valueOf(r));
		if (s < 0.)
			throw new IllegalArgumentException(String.valueOf(s));
		if (numberOfPValues < 1) // Hmm.	
			throw new IllegalArgumentException(String.valueOf(numberOfPValues));
		if (N1 < 1)
			throw new IllegalArgumentException(String.valueOf(N1));
		if (N2 < 1)
			throw new IllegalArgumentException(String.valueOf(N2));
		this.lambda0 = lambda0;	this.r = r;	this.s = s;	
		this.numberOfPValues = numberOfPValues;	
		this.N1 = N1;	this.N2 = N2;	
		if (N1 != N2) // Calculate 'equivalent' equal group sample size.
			this.N = 2. / (1. / N1 + 1. / N2);
		else
			this.N = N1;	
	}

	// newN is referred to as 'n*' in the spec.
	// numberOfIterations is referred to as 'M' in the paper.
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

	public final class Estimates {
		private int newN;
		private double threshold;
		private double[] tp, tn, edr;

		private Estimates(int newN, double threshold, double[] tp, double[] tn, double[] edr) {
			this.newN = newN;
			this.threshold = threshold;
			this.tp = tp; this.tn = tn; this.edr = edr;
		}

		public double getEquivalentSampleSize() { return N; }
		public int getExtrapolatedSampleSize() { return newN; }
		public double getThreshold() { return threshold; }
		public double getTP() { return Summary.mean(tp); }
		public double getTN() { return Summary.mean(tn); }
		public double getEDR() { return Summary.mean(edr); }
		public double getStandardErrorForTP() { return Summary.sampleStandardDeviation(tp); }
		public double getStandardErrorForTN() { return Summary.sampleStandardDeviation(tn); }
		public double getStandardErrorForEDR() { return Summary.sampleStandardDeviation(edr); }

		public String toString() {
			String EOL = System.getProperty("line.separator");
			StringBuffer buffer = new StringBuffer();
			buffer.append("lambda0 = ").append(lambda0).append(", r = ").append(r).append(", s = ").append(s).append(EOL);
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
		// Number of genes for which the p-value comes from the uniform.
		int u = -1; // A + C in paper.
		if (lambda0 > 0. && lambda0 < 1.)
			u = random.nextBinomial(numberOfPValues, lambda0); 
		else if (lambda0 == 0.) // See issue HDB-104 in JIRA.
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
