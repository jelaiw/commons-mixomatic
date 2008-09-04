package edu.uab.ssg.mixomatic.power.jmsl;

import edu.uab.ssg.mixomatic.power.BootstrapEstimator;
import com.imsl.stat.Random;

/**
 * An adapter for the random number generator com.imsl.stat.Random in the JMSL.
 *
 * @author Jelai Wang
 */

public final class RandomAdapter implements BootstrapEstimator.RandomNumberGenerator {
	private Random random;

	public RandomAdapter() {
		this.random = new Random();
	}

	public int nextBinomial(int n, double p) {
		return random.nextBinomial(n, p);
	}

	public double nextUniform() {
		return random.nextDouble();
	}

	public double nextBeta(double r, double s) {
		return random.nextBeta(r, s);
	}
}
