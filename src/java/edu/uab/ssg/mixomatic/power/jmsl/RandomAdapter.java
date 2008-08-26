package edu.uab.ssg.mixomatic.power.jmsl;

import edu.uab.ssg.mixomatic.power.BootstrapEstimator;
import com.imsl.stat.Random;

/**
 * @author Jelai Wang
 */

public final class RandomAdapter implements BootstrapEstimator.RandomNumberGenerator {
	private Random random;

	public RandomAdapter() {
		this.random = new Random();
	}

	public double nextBinomial(int n, double p) {
		return random.nextBinomial(n, p);
	}

	public double nextUniform() {
		return random.nextDouble();
	}

	public double nextBeta(double r, double s) {
		return random.nextBeta(r, s);
	}
}
