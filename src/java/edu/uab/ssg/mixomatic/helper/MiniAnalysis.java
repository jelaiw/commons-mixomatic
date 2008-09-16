package edu.uab.ssg.mixomatic.helper;

import edu.uab.ssg.mixomatic.*;
import edu.uab.ssg.mixomatic.flanagan.BoundedOptimizer;
import edu.uab.ssg.mixomatic.jsci.DefaultProbabilityDensityFunction;
import edu.uab.ssg.mixomatic.plot.Histogram;
import edu.uab.ssg.mixomatic.power.*;
import edu.uab.ssg.mixomatic.power.plot.CombinedPlot;
import edu.uab.ssg.mixomatic.power.plot.EDRPlot;
import edu.uab.ssg.mixomatic.power.plot.TPPlot;
import edu.uab.ssg.mixomatic.power.plot.TNPlot;
import java.io.*;
import java.util.*;

/**
 * A command-line "mini"-analysis program for the mix-o-matic procedure.
 *
 * This program takes an input file name, parses the named file for p-values,
 * fits the mix-o-matic mixture model to the p-values, outputs the estimates 
 * of the model parameters, plots a custom histogram, estimates the proportions
 * of interest (EDR, TP, TN) by parametric bootstrap at various, selected 
 * sample sizes and thresholds for significance, and plots the "combined"
 * plot at threshold = 0.05 and the individual EDR, TP, and TN plots at
 * the same thresholds (0.05, 0.001, 0.00001) selected in the paper. 
 *
 * @author Jelai Wang
 */

public final class MiniAnalysis {
	public static void main(String[] args) throws IOException, MixomaticException {
		// Parse input file.
		String inputFileName = args[0];
		PValueParser parser = new PValueParser();
		System.out.println("Reading input file: " + inputFileName);
		double[] pvalues = parser.parse(new FileInputStream(inputFileName), new PValueParser.BadFormatHandler() {
			public void handleBadPValue(String badPValue) {
				System.err.println("Skipped bad input: " + badPValue);
			}
		});

		// Parse N1 and N2.
		int N1 = Integer.parseInt(args[1]);
		int N2 = Integer.parseInt(args[2]);
		System.out.println("N1 = " + N1 + ", N2 = " + N2);

		System.out.println("Estimating mixture model.");
		MixtureModel.Estimator estimator = new BoundedOptimizer();
		MixtureModel.Estimate estimate = estimator.estimateParameters(pvalues);
		System.out.println(estimate);

		System.out.println("Creating histogram.");
		ProbabilityDensityFunction function = new DefaultProbabilityDensityFunction();
		Histogram histogram = new Histogram(pvalues);
		histogram.writePNG(new FileOutputStream("histogram.png"));

		System.out.println("Estimating proportions of interest by bootstrap.");
		// Estimate EDR, TP, and TN at threshold = 0.05.
		List<BootstrapEstimator.Estimate> estimates = estimateProportionsOfInterestAtVariousSampleSizes(estimate, N1, N2, 0.05);

		System.out.println("Creating combined plot.");
		CombinedPlot combined = new CombinedPlot(estimates);
		combined.writePNG(new FileOutputStream("combined.png"));

		System.out.println("More estimation of proportions of interest.");
		// Estimate EDR, TP, and TN at other selected thresholds.
		// These are the same three thresholds selected in the paper, pg 335.
		estimates.addAll(estimateProportionsOfInterestAtVariousSampleSizes(estimate, N1, N2, 0.001));
		estimates.addAll(estimateProportionsOfInterestAtVariousSampleSizes(estimate, N1, N2, 0.00001));

		System.out.println("Creating EDR, TP, and TN plots.");
		EDRPlot edrPlot = new EDRPlot(estimates);
		edrPlot.writePNG(new FileOutputStream("edr.png"));
		TPPlot tpPlot = new TPPlot(estimates);
		tpPlot.writePNG(new FileOutputStream("tp.png"));
		TNPlot tnPlot = new TNPlot(estimates);
		tnPlot.writePNG(new FileOutputStream("tn.png"));

		System.out.println("Done.");
	}

	private static List<BootstrapEstimator.Estimate> estimateProportionsOfInterestAtVariousSampleSizes(MixtureModel.Estimate model, int N1, int N2, double threshold) {
		int[] n_ = new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 40, 50, 100 };

		List<BootstrapEstimator.Estimate> list = new ArrayList<BootstrapEstimator.Estimate>();
		BootstrapEstimator estimator = new DefaultEstimator();
		for (int i = 0; i < n_.length; i++) {
			BootstrapEstimator.Estimate estimate = estimator.estimateProportions(model, N1, N2, n_[i], threshold);
			list.add(estimate);
		}
		return list;
	}
}
