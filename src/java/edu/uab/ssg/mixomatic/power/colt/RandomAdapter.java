package edu.uab.ssg.mixomatic.power.colt;

import edu.uab.ssg.mixomatic.power.BootstrapEstimator;
import cern.jet.random.Binomial;
import cern.jet.random.Uniform;
import cern.jet.random.Beta;
import cern.jet.random.engine.MersenneTwister;
import java.util.Date;

/**
 * An adapter for the random number generator com.imsl.stat.Random in the JMSL.
 *
 * @author Jelai Wang
 */

public final class RandomAdapter implements BootstrapEstimator.RandomNumberGenerator {
	private Binomial binomial;
	private Uniform uniform;
	private Beta beta;

	// Note that we construct the binomial and beta instances with arbitrary
	// values that are bypassed in the calls to nextBinomial() and nextBeta().
	public RandomAdapter() {
		this.binomial = new Binomial(1, 0.5, new MersenneTwister(new Date()));
		this.uniform = new Uniform(new MersenneTwister(new Date()));
		this.beta = new Beta(0.5, 0.5, new MersenneTwister(new Date()));
	}

	public int nextBinomial(int n, double p) {
		return binomial.nextInt(n, p);
	}

	public double nextUniform() {
		return uniform.nextDouble();
	}

	public double nextBeta(double r, double s) {
		return beta.nextDouble(r, s);
	}
}
