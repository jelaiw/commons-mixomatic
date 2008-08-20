package edu.uab.ssg.mixomatic.helper;

import java.util.*;
import java.io.*;

/**
 * Parser for simple, "one p-value per line" file format.
 *
 * @author Jelai Wang
 * @version 8/12/08
 */
public final class PValueParser {
	/**
	 * This interface enables the parser to report badly formatted parts of
	 * the file, as it is being parsed, to the client programmer.
	 */
	public interface BadFormatHandler {
		/**
		 * Handle a bad p-value, including cases where the value is
		 * "out of bounds" or the value is not a number.
		 * @param badPValue The string that could not be parsed to a p-value.
		 */
		void handleBadPValue(String badPValue);
	}

	/**
	 * Parse an input stream, in a simple, "one p-value per line"
	 * format, and return the p-values as a double array.
	 *
	 * @param in The input stream, typically a FileInputStream. This stream
	 * will be closed.
	 * @param handler The user-supplied BadFormatHandler.
	 * @return The p-values parsed from the input stream.
	 */
	public double[] parse(InputStream in, BadFormatHandler handler) throws IOException {
		if (in == null)
			throw new NullPointerException("in");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		List<Double> list = new ArrayList<Double>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			Double d = null;
			// See Double.valueOf() API for an alternative to catching a
			// NumberFormatException to detect an input string problem.
			try {
				d = Double.valueOf(line);
			}
			catch (NumberFormatException e) {
				handler.handleBadPValue(line);
				continue;
			}

			if (d.doubleValue() < 0. || d.doubleValue() > 1.) // Bad p-value.
				handler.handleBadPValue(line);
			else // Good p-value.
				list.add(d);
		}
		reader.close();
		return asArray(list);
	}

	private double[] asArray(List<Double> list) {
		double[] tmp = new double[list.size()];	
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = list.get(i).doubleValue();
		}
		return tmp;
	}
}
