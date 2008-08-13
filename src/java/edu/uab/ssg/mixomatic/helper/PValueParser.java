package edu.uab.ssg.mixomatic.helper;

import java.util.*;
import java.io.*;

/**
 * @author Jelai Wang
 * @version 8/12/08
 */
public final class PValueParser {
	public interface BadFormatHandler {
		void handleBadPValue(String badPValue);
	}

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
