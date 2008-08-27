package edu.uab.ssg.mixomatic.power;

import junit.framework.TestCase;
import junit.framework.Assert;

/**
 *	@author Jelai Wang
 */

public final class TestDefaultEstimate extends TestCase {
	public void testNaive() {
		double[] tmp = new double[] { 0.7, 0.8, 0.9 };
		DefaultEstimate estimate = new DefaultEstimate(10, 0.05, tmp, tmp, tmp);
		Assert.assertEquals(10, estimate.getSampleSize());
		Assert.assertTrue(Double.compare(0.05, estimate.getSignificanceLevel()) == 0);
		Assert.assertEquals(0.8, estimate.getTP(), 0.8 * 0.01);
		Assert.assertEquals(0.8, estimate.getTN(), 0.8 * 0.01);
		Assert.assertEquals(0.8, estimate.getEDR(), 0.8 * 0.01);
		Assert.assertEquals(0.1, estimate.getStandardErrorForTP(), 0.1 * 0.01);
		Assert.assertEquals(0.1, estimate.getStandardErrorForTN(), 0.1 * 0.01);
		Assert.assertEquals(0.1, estimate.getStandardErrorForEDR(), 0.1 * 0.01);
	}
}	
