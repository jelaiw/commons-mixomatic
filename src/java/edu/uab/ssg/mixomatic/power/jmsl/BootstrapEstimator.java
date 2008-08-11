package edu.uab.ssg.mixomatic.power.jmsl;

import edu.uab.ssg.mixomatic.power.CombinedEstimator;
import edu.uab.ssg.mixomatic.MixtureModel;
import com.imsl.stat.*;

/**
 * This class implements the parametric bootstrap procedure, described in
 * the paper at http://dx.doi.org/10.1191/0962280204sm369ra, for obtaining
 * estimates of the expected discovery rate (EDR), a quantity "akin but 
 * not identical to the notion of power", the proportion of true positives 
 * (TP), and the proportion of true negatives (TN).
 *
 * The client programmer is expected to have conducted many, valid tests
 * of hypothesis to obtain a sample distribution of p-values (from those
 * tests) and fitted a mixture model, using a MixtureModel.Estimator,
 * to this distribution of p-values. Then, an instance of this class can be 
 * constructed to estimate EDR, TP, and TN.
 *
 * @author Jelai Wang
 * @version $Rev$ $LastChangedDate$ $LastChangedBy$ 5/22/2006
 */

public final class BootstrapEstimator implements CombinedEstimator {
	private MixtureModel model;
	private int numberOfPValues; // Referred to as 'k' in the spec.
	private double N; // Referred to as 'n' in the paper.

	/**
	 * Constructs an estimator given sample size parameters from 
	 * a two-group hypothesis test.
	 * If N1 and N2 are not equal, and "equivalent" equal group
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

	public CombinedEstimator.Estimates estimateProportions(int newN, double threshold, int numberOfIterations) {
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
		return new DefaultEstimates(newN, threshold, tp, tn, edr);
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

	/* package private */ final class DefaultEstimates implements CombinedEstimator.Estimates {
		private int newN;
		private double threshold;
		private double[] tp, tn, edr;

		/* package private */ DefaultEstimates(int newN, double threshold, double[] tp, double[] tn, double[] edr) {
			this.newN = newN;
			this.threshold = threshold;
			this.tp = tp; this.tn = tn; this.edr = edr;
		}

		public double getEqualGroupSampleSize() { return N; }
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
			buffer.append(model.toString()).append(EOL);
			buffer.append("Number of p-values = ").append(numberOfPValues).append(EOL);
			buffer.append("N = ").append(getEqualGroupSampleSize()).append(EOL);
			buffer.append("Extrapolated N = ").append(getExtrapolatedSampleSize()).append(EOL);
			buffer.append("threshold = ").append(getThreshold()).append(EOL);
			buffer.append("TP = ").append(getTP()).append(EOL);
			buffer.append("TN = ").append(getTN()).append(EOL);
			buffer.append("EDR = ").append(getEDR()).append(EOL);
			return buffer.toString().trim();
		}
	}
}
