package edu.uab.ssg.mixomatic.power;

import edu.uab.ssg.mixomatic.MixtureModel;
import junit.framework.TestCase;
import junit.framework.Assert;
import org.easymock.*;

/**
 *	@author Jelai Wang
 */

public final class TestDefaultEstimate extends TestCase {
	public void testNaive() {
		BootstrapEstimator.Configuration configuration = (BootstrapEstimator.Configuration) EasyMock.createMock(BootstrapEstimator.Configuration.class);
		MixtureModel.Estimate model = (MixtureModel.Estimate) EasyMock.createMock(MixtureModel.Estimate.class);
		double[] tmp = new double[] { 0.7, 0.8, 0.9 };
		DefaultEstimate estimate = new DefaultEstimate(configuration, model, 5., 10, 0.05, tmp, tmp, tmp);
		Assert.assertSame(configuration, estimate.getConfiguration());
		Assert.assertSame(model, estimate.getModel());
		Assert.assertTrue(Double.compare(5., estimate.getEqualGroupSampleSize()) == 0);
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
