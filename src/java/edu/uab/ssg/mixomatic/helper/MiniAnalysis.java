package edu.uab.ssg.mixomatic.helper;

import edu.uab.ssg.mixomatic.*;
import edu.uab.ssg.mixomatic.flanagan.BoundedOptimizer;
import edu.uab.ssg.mixomatic.jsci.DefaultProbabilityDensityFunction;
import edu.uab.ssg.mixomatic.plot.Histogram;
import java.io.*;

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

		System.out.println("Done.");
	}
}