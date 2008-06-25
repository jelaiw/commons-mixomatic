package edu.uab.ssg.mixomatic;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;

/**
 * @author Jelai Wang
 * @version $Rev$ $LastChangedDate$ $LastChangedBy$ 4/4/06
 */

public final class TestLogLikelihoodFunction extends TestCase {
	private double[] pValues;

	public void setUp() throws IOException {
		this.pValues = new double[12488];
		BufferedReader in = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("edu/uab/ssg/mixomatic/pvalues.txt")));
		for (int i = 0; i < pValues.length; i++) {
			String text = in.readLine();
			pValues[i] = Double.parseDouble(text);
		}
		in.close();
	}

	public void tearDown() {
		pValues = null;
	}

	public void testJmslPdf() throws IOException {
		MixtureModel model = new DefaultModel(0.8, 1.5, 2.5);
		ProbabilityDensityFunction function = new edu.uab.ssg.mixomatic.jmsl.DefaultPDF();
		double L = LogLikelihoodFunction.evaluate(model, function, pValues);
		Assert.assertEquals(39.976442, L, 39.976442 * 0.01);
	}

	public void testJsciPdf() throws IOException {
		MixtureModel model = new DefaultModel(0.8, 1.5, 2.5);
		ProbabilityDensityFunction function = new edu.uab.ssg.mixomatic.jsci.DefaultProbabilityDensityFunction();
		double L = LogLikelihoodFunction.evaluate(model, function, pValues);
		Assert.assertEquals(39.976442, L, 39.976442 * 0.01);
	}
}
