package edu.uab.ssg.mixomatic.helper;

import edu.uab.ssg.mixomatic.*;
import edu.uab.ssg.mixomatic.flanagan.BoundedOptimizer;
import edu.uab.ssg.mixomatic.jsci.DefaultProbabilityDensityFunction;
import edu.uab.ssg.mixomatic.plot.Histogram;
import edu.uab.ssg.mixomatic.power.*;
import edu.uab.ssg.mixomatic.power.plot.CombinedPlot;
import java.io.*;
import java.util.*;

/**
 * A command-line "mini"-analysis program for the mix-o-matic procedure.
 * This program takes an input file name, parses this file for p-values,
 * fits the mix-o-matic mixture model, outputs the estimates of the
 * model parameters, and plots a custom histogram. 
 *
 * @author Jelai Wang
 */

public final class MiniAnalysis {
	public static void main(String[] args) throws IOException, MixomaticException {
		String inputFileName = args[0];

		PValueParser parser = new PValueParser();
		System.out.println("Reading input file: " + inputFileName);
		double[] pvalues = parser.parse(new FileInputStream(inputFileName), new PValueParser.BadFormatHandler() {
			public void handleBadPValue(String badPValue) {
				System.err.println("Skipped bad input: " + badPValue);
			}
		});

		System.out.println("Estimating mixture model.");
		MixtureModel.Estimator estimator = new BoundedOptimizer();
		MixtureModel.Estimate estimate = estimator.estimateParameters(pvalues);
		System.out.println(estimate);

		System.out.println("Creating histogram.");
		ProbabilityDensityFunction function = new DefaultProbabilityDensityFunction();
		Histogram histogram = new Histogram(pvalues);
		histogram.addSubtitle("Input filename: " + inputFileName);
		histogram.writePNG(new FileOutputStream("histogram.png"));

		System.out.println("Estimating proportions of interest at various sample sizes.");
		int N1 = 5, N2 = 5; // Turn these into user-supplied arguments??
		List<BootstrapEstimator.Estimate> estimates = estimateProportionsOfInterestAtVariousSampleSizes(estimate, N1, N2);

		System.out.println("Creating combined plot of EDR, TP, and TN at various sample sizes and a fixed threshold.");
		CombinedPlot combined = new CombinedPlot(estimates);
		combined.writePNG(new FileOutputStream("combined.png"));

		System.out.println("Done.");
	}

	private static List<BootstrapEstimator.Estimate> estimateProportionsOfInterestAtVariousSampleSizes(MixtureModel.Estimate model, int N1, int N2) {
		int[] n_ = new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 40, 50, 100 };
		double[] thresholds = new double[] { 0.05 };

		List<BootstrapEstimator.Estimate> list = new ArrayList<BootstrapEstimator.Estimate>();
		BootstrapEstimator estimator = new DefaultEstimator();
		for (int i = 0; i < n_.length; i++) {
			for (int j = 0; j < thresholds.length; j++) {
				BootstrapEstimator.Estimate estimate = estimator.estimateProportions(model, N1, N2, n_[i], thresholds[j]);
				list.add(estimate);
			}
		}
		return list;
	}
}
