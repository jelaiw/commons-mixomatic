package edu.uab.ssg.mixomatic.power.plot;

import edu.uab.ssg.mixomatic.power.BootstrapEstimator;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;
import org.jfree.util.ShapeUtilities;
import org.jfree.chart.title.TextTitle;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Font;
import java.util.*;
import java.io.*;

/**
 * A superclass for EDRPlot, TPPlot, and TNPlot containing the shared 
 * configuration and implementation for these closely related plots.
 *
 * @author Jelai Wang
 */

/* package private */ abstract class AbstractPlot {
	private static final int WIDTH = 680, HEIGHT = 510;
	private JFreeChart chart;

	protected AbstractPlot(List<BootstrapEstimator.Estimate> estimates, String title, ProportionHandler handler) {
		if (estimates == null)
			throw new NullPointerException("estimates");
		if (estimates.size() < 1)
			throw new IllegalArgumentException(String.valueOf(estimates.size()));
		if (title == null)
			throw new NullPointerException("title");
		if (handler == null)
			throw new NullPointerException("handler");

		chart = new JFreeChart(PlotFactory.createPlot(createDataset(estimates, handler)));
		chart.setTitle(title);
		chart.getTitle().setFont(chart.getTitle().getFont().deriveFont(24f));
		chart.setBackgroundPaint(java.awt.Color.WHITE);
		double shapeSize = 10.;
		// Five color, six shape rotation. 
		chart.getPlot().setDrawingSupplier(new DefaultDrawingSupplier(
			new Color[] { Color.RED, ChartColor.DARK_GREEN, Color.BLUE, Color.BLACK, ChartColor.DARK_YELLOW },
			DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
			DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
			DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
			new Shape[] { PlotFactory.getSquare(shapeSize), PlotFactory.getCircle(shapeSize), PlotFactory.getTriangle(shapeSize), PlotFactory.getDiamond(shapeSize), PlotFactory.getUpsideDownTriangle(shapeSize), ShapeUtilities.createDiagonalCross(4, 1) }
		));
	}

	/**
	 * Adds a subtitle.
	 */
	public void addSubtitle(String subtitle) {
		if (subtitle == null)
			throw new NullPointerException("subtitle");
		TextTitle textTitle = new TextTitle(subtitle);
		textTitle.setFont(new Font("SansSerif", Font.PLAIN, 22));
		chart.addSubtitle(textTitle);
	}

	/**
	 * Writes the plot in PNG image format to an output stream.
	 * @param out The output stream, typically a file output stream.
	 */
	public void writePNG(OutputStream out) throws IOException {
		if (out == null)
			throw new NullPointerException("out");
		ChartUtilities.writeChartAsPNG(out, chart, WIDTH, HEIGHT);
		out.close();
	}

	/**
	 * An interface for returning the proportion of interest from an estimate.
	 */
	/* package private */ interface ProportionHandler {
		/**
		 * Returns the proportion of interest from a given estimate.
		 */
		double getProportionOfInterest(BootstrapEstimator.Estimate estimate);
	}

	// Each "series" is the proportion of interest (EDR, TP, or TP) specified 
	// by the ProportionHandler at a particular threshold of significance.
	private XYDataset createDataset(List<BootstrapEstimator.Estimate> estimates, ProportionHandler handler) {
		Map<Double, XYSeries> map = new LinkedHashMap<Double, XYSeries>();
		for (Iterator<BootstrapEstimator.Estimate> it = estimates.iterator(); it.hasNext(); ) {
			BootstrapEstimator.Estimate estimate = it.next();
			Double threshold = new Double(estimate.getSignificanceLevel());
			XYSeries series = map.get(threshold);
			if (series == null) {
				series = new XYSeries(threshold.toString());
				map.put(threshold, series);
			}
			series.add(estimate.getSampleSize(), handler.getProportionOfInterest(estimate));
		}
		XYSeriesCollection collection = new XYSeriesCollection();
		for (Iterator<XYSeries> it = map.values().iterator(); it.hasNext(); ) {
			XYSeries series = it.next();
			collection.addSeries(series);
		}
		return collection;
	}
}
