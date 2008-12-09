package edu.uab.ssg.mixomatic.power.apache;

import junit.framework.TestCase;
import junit.framework.Assert;

/**
 *	@author Jelai Wang
 */

public final class TestTTestPValueAdjuster extends TestCase {
	public void testIncreaseInSampleSize() {
		TTestPValueAdjuster adjuster = new TTestPValueAdjuster();
		double pvalue = 0.05;
		double n = 5;
		int n_ = 10;
		Assert.assertEquals(0.00433721, adjuster.adjustPValue(pvalue, n, n_), 0.00433721 * 0.01);
	}

	public void testExtremePValue() {
		TTestPValueAdjuster adjuster = new TTestPValueAdjuster();
		double pvalue = 1E-50;
		double n = 7;
		int n_ = 3;
		Assert.assertEquals(8.02857E-18, adjuster.adjustPValue(pvalue, n, n_), 8.02857E-18  * 0.01);
	}

	public void testFractionalDegreesOfFreedom() {
		TTestPValueAdjuster adjuster = new TTestPValueAdjuster();
		double pvalue = 0.05;
		double n = 5.5;
		int n_ = 10;
		Assert.assertEquals(0.00688806, adjuster.adjustPValue(pvalue, n, n_), 0.00688806 * 0.01);
	}
}
