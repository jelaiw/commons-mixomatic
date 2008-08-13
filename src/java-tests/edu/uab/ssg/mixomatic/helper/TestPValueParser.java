package edu.uab.ssg.mixomatic.helper;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.IOException;

/**
 * @author Jelai Wang
 * @version 8/12/08
 */

public final class TestPValueParser extends TestCase {
	public void testRealPValues() throws IOException {
		PValueParser parser = new PValueParser();
		double[] pvalues = parser.parse(ClassLoader.getSystemResourceAsStream("edu/uab/ssg/mixomatic/pvalues.txt"), new PValueParser.BadFormatHandler() {
			public void handleBadPValue(double badPValue) {
				Assert.fail(String.valueOf(badPValue));
			}

			public void handleBadPValue(String badPValue) {
				Assert.fail(badPValue);
			}
		});
		Assert.assertEquals(12488, pvalues.length);
		Assert.assertTrue(0.000259375 == pvalues[0]);
		Assert.assertTrue(0.73531518 == pvalues[12487]);
	}

	public void testBadFormat() throws IOException {
		PValueParser parser = new PValueParser();
		double[] pvalues = parser.parse(ClassLoader.getSystemResourceAsStream("edu/uab/ssg/mixomatic/helper/bad_pvalues.txt"), new PValueParser.BadFormatHandler() {
			public void handleBadPValue(double badPValue) {
				Assert.assertTrue(1.000259375 == badPValue);
			}

			public void handleBadPValue(String badPValue) {
				Assert.assertEquals("foobar", badPValue);
			}
		});
		Assert.assertEquals(8, pvalues.length);
		Assert.assertTrue(0.000548105 == pvalues[0]);
		Assert.assertTrue(0.00044243 == pvalues[7]);
	}
}	
