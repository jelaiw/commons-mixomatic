package edu.uab.ssg.mixomatic.power;

/* package private */ final class DefaultConfiguration implements BootstrapEstimator.Configuration {
	private int numberOfIterations;
	private BootstrapEstimator.PValueAdjuster adjuster;
	private BootstrapEstimator.RandomNumberGenerator rng;

	/* package private */ DefaultConfiguration(int numberOfIterations, BootstrapEstimator.PValueAdjuster adjuster, BootstrapEstimator.RandomNumberGenerator rng) {
		if (numberOfIterations < 1)
			throw new IllegalArgumentException(String.valueOf(numberOfIterations));
		if (adjuster == null)
			throw new NullPointerException("adjuster");
		if (rng == null)
			throw new NullPointerException("rng");
		this.numberOfIterations = numberOfIterations;
		this.adjuster = adjuster;
		this.rng = rng;
	}

	public int getNumberOfIterations() { return numberOfIterations; }
	public BootstrapEstimator.PValueAdjuster getPValueAdjuster() { return adjuster; }
	public BootstrapEstimator.RandomNumberGenerator getRandomNumberGenerator() { return rng; }

	public String toString() {
		String EOL = System.getProperty("line.separator");
		StringBuffer buffer = new StringBuffer();
		buffer.append("M = ").append(getNumberOfIterations()).append(EOL);
		buffer.append("p-value adjuster = ").append(getPValueAdjuster().getClass().getName()).append(EOL);
		buffer.append("random number generator = ").append(getRandomNumberGenerator().getClass().getName()).append(EOL);
		return buffer.toString().trim();
	}
}
