package edu.uab.ssg.mixomatic.power.plot;

import edu.uab.ssg.mixomatic.power.BootstrapEstimator;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.*;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Font;
import java.util.*;
import java.io.*;

/**
 * A "combined" plot of estimates for EDR, TP, and TN at various sample
 * sizes and a fixed threshold for significance.
 *
 * @author Jelai Wang
 * @version 6/6/06
 */
public final class CombinedPlot {
	private static final int WIDTH = 680, HEIGHT = 510;
	private JFreeChart chart;

	/**
	 * Construct a combined plot.
	 * @param estimates The estimates for EDR, TP, and TN at various
	 * sample sizes. Must be estimated at the same threshold for
	 * significance or an exception is thrown.
	 */
	public CombinedPlot(List<BootstrapEstimator.Estimate> estimates) {
		if (estimates == null)
			throw new NullPointerException("estimates");
		if (estimates.size() < 1)
			throw new IllegalArgumentException(String.valueOf(estimates.size()));
		// Check that the user-supplied estimates were calculated 
		// at the same threshold of significance.
		double threshold = Double.NaN;
		for (Iterator<BootstrapEstimator.Estimate> it = estimates.iterator(); it.hasNext(); ) {
			BootstrapEstimator.Estimate estimate = it.next();
			if (Double.isNaN(threshold)) {
				threshold = estimate.getSignificanceLevel();
			}
			else if (Double.compare(threshold, estimate.getSignificanceLevel()) != 0) {
				throw new IllegalArgumentException(threshold + " " + estimate.getSignificanceLevel());
			}
		}
		this.chart = new JFreeChart(PlotFactory.createPlot(createDataset(estimates)));
		chart.setTitle("TP, TN, and EDR at Fixed Threshold");
		chart.getTitle().setFont(chart.getTitle().getFont().deriveFont(24f));
		addSubtitle("significance level = " + threshold);
		chart.setBackgroundPaint(Color.WHITE);
		double shapeSize = 10.;
		chart.getPlot().setDrawingSupplier(new DefaultDrawingSupplier(
			new Color[] { Color.RED, ChartColor.DARK_GREEN, Color.BLUE },
			DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
			DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
			DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
			new Shape[] { PlotFactory.getSquare(shapeSize), PlotFactory.getCircle(shapeSize), PlotFactory.getTriangle(shapeSize) }
		));
	}

	/**
	 * Add a subtitle.
	 */
	public void addSubtitle(String subtitle) {
		if (subtitle == null)
			throw new NullPointerException("subtitle");
		TextTitle textTitle = new TextTitle(subtitle);
		textTitle.setFont(new Font("SansSerif", Font.PLAIN, 22));
		chart.addSubtitle(textTitle);
	}

	/**
	 * Write the plot in PNG image format to an output stream.
	 * @param out The output stream, typically a file output stream.
	 */
	public void writePNG(OutputStream out) throws IOException {
		if (out == null)
			throw new NullPointerException("out");
		ChartUtilities.writeChartAsPNG(out, chart, WIDTH, HEIGHT);
		out.close();
	}

	private XYDataset createDataset(List<BootstrapEstimator.Estimate> estimates) {
		XYSeriesCollection collection = new XYSeriesCollection();
		XYSeries tpSeries = new XYSeries("TP");
		XYSeries tnSeries = new XYSeries("TN");
		XYSeries edrSeries = new XYSeries("EDR");
		for (Iterator<BootstrapEstimator.Estimate> it = estimates.iterator(); it.hasNext(); ) {
			BootstrapEstimator.Estimate estimate = it.next();
			int N = estimate.getSampleSize();
			tpSeries.add(N, estimate.getTP());
			tnSeries.add(N, estimate.getTN());
			edrSeries.add(N, estimate.getEDR());
		}
		collection.addSeries(tpSeries);
		collection.addSeries(tnSeries);
		collection.addSeries(edrSeries);
		return collection;
	}
}
