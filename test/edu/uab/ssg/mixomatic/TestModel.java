package edu.uab.ssg.mixomatic;

import junit.framework.TestCase;
import junit.framework.Assert;

/**
 * @author Jelai Wang
 */

public final class TestModel extends TestCase {
	public void testDefaultModel() {
		MixtureModel model = new DefaultModel(0.8, 1.5, 2.5);
		Assert.assertEquals(Double.doubleToLongBits(0.8), Double.doubleToLongBits(model.getLambda0()));
		Assert.assertEquals(Double.doubleToLongBits(1.5), Double.doubleToLongBits(model.getR()));
		Assert.assertEquals(Double.doubleToLongBits(2.5), Double.doubleToLongBits(model.getS()));
	}

	public void testDefaultModelBadArgs() {
		try {
			MixtureModel model = new DefaultModel(-0.8, 1.5, 2.5);
			Assert.fail("lambda0 out of range.");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			MixtureModel model = new DefaultModel(1.8, 1.5, 2.5);
			Assert.fail("lambda0 out of range.");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			MixtureModel model = new DefaultModel(0.8, -1.5, 2.5);
			Assert.fail("r out of range.");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		try {
			MixtureModel model = new DefaultModel(0.8, 1.5, -2.5);
			Assert.fail("s out of range.");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
	}
}
