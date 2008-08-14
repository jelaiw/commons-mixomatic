package edu.uab.ssg.mixomatic.plot;

import edu.uab.ssg.mixomatic.*;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.title.*;
import org.jfree.data.xy.*;
import org.jfree.data.function.*;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.*;
import java.awt.*;
import java.io.*;

/**
 * @author Jelai Wang
 * @version 1/25/05
 */

public final class Histogram {
	private static final int NUMBER_OF_BINS = 20;
	private MixtureModel model;
	private double[] pvalues;
	private ProbabilityDensityFunction function;
	private JFreeChart chart;

	public Histogram(MixtureModel model, double[] pvalues, ProbabilityDensityFunction function) {
		if (model == null)
			throw new NullPointerException("model");
		this.model = model;
		if (pvalues == null)
			throw new NullPointerException("pvalues");
		if (pvalues.length < 1)
			throw new IllegalArgumentException(String.valueOf(pvalues.length));
		for (int i = 0; i < pvalues.length; i++) {
			if (pvalues[i] < 0. || pvalues[i] > 1.)
				throw new IllegalArgumentException(i + " " + pvalues[i]);
		}
		this.pvalues = (double[]) pvalues.clone();
		if (function == null)
			throw new NullPointerException("function");
		this.function = function;

		this.chart = new JFreeChart(createXYPlot());
		chart.setTitle("Histogram of p-values");
		chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 26));
		chart.setBackgroundPaint(Color.WHITE);
		LegendTitle legend = chart.getLegend();
		legend.setItemFont(legend.getItemFont().deriveFont(18f));
	}

	public void addSubtitle(String subtitle) {
		if (subtitle == null)
			throw new NullPointerException("subtitle");
		TextTitle textTitle = new TextTitle(subtitle);
		textTitle.setFont(new Font("SansSerif", Font.PLAIN, 22));
		chart.addSubtitle(textTitle);
	}

	public void write(OutputStream out, int width, int height) throws IOException {
		if (out == null)
			throw new NullPointerException("out");
		if (width < 1)
			throw new IllegalArgumentException(String.valueOf(width));
		if (height < 1)
			throw new IllegalArgumentException(String.valueOf(height));
		ChartUtilities.writeChartAsPNG(out, chart, width, height);
	}

	public MixtureModel getMixtureModel() { return model; }
	public double[] getPValues() { return (double[]) pvalues.clone(); }
	public ProbabilityDensityFunction getProbabilityDensityFunction() { return function; }

	private XYPlot createXYPlot() {
		XYPlot plot = new XYPlot();
		// Set up histogram.
		HistogramDataset dataset = new HistogramDataset();
		dataset.addSeries("histogram", pvalues, NUMBER_OF_BINS);
		dataset.setType(HistogramType.SCALE_AREA_TO_1);
		plot.setDataset(0, dataset);
		plot.setRenderer(0, new XYBarRenderer(0.1));
		// Set up mix-o-matic probability density function.
		XYDataset functionData = DatasetUtilities.sampleFunction2D(new Function2D() {
			public double getValue(double x) {
				return function.evaluate(model, x);
			}
		}, 0.005, 0.995, 150, "fitted mix-o-matic density function");
		XYItemRenderer functionRenderer = new StandardXYItemRenderer();		
		functionRenderer.setBaseStroke(new BasicStroke(3.0f));	
		plot.setDataset(1, functionData);
		plot.setRenderer(1, functionRenderer);
		// Set up uniform probability density function.
		XYDataset lineData = DatasetUtilities.sampleFunction2D(new LineFunction2D(1, 0), 0.005, 0.995, 5, "uniform density function");
		XYItemRenderer lineRenderer = new StandardXYItemRenderer();
		// Read java.awt.BasicStroke API.
		// See http://www.jfree.org/forum/viewtopic.php?t=5518 for example.
        lineRenderer.setBaseStroke(new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0.0f, new float[] { 10.0f }, 0.0f));
		plot.setDataset(2, lineData);
		plot.setRenderer(2, lineRenderer);
		// Customize plot.
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		plot.setDomainAxis(createHorizontalAxis());
		plot.setRangeAxis(createVerticalAxis(DatasetUtilities.findMaximumRangeValue(dataset).doubleValue()));
		plot.setDrawingSupplier(createDrawingSupplier());
		plot.setRangeGridlinesVisible(false);
		plot.setDomainGridlinesVisible(false);
		// Set up legend items and skip histogram legend item.
		LegendItemCollection items = plot.getLegendItems();
		LegendItemCollection newItems = new LegendItemCollection();
		newItems.add(items.get(1)); // Mix-o-matic density function.
		newItems.add(items.get(2)); // Uniform density function.
		plot.setFixedLegendItems(newItems);
		return plot;
	}

	private NumberAxis createHorizontalAxis() {
		NumberAxis axis = new NumberAxis("p-value");
		axis.setRange(0., 1.); // p-values range from 0 to 1.
		axis.setTickUnit(new NumberTickUnit(0.2));
		axis.setTickLabelFont(axis.getTickLabelFont().deriveFont(Font.BOLD, 22f));
		axis.setLabelFont(axis.getLabelFont().deriveFont(Font.BOLD, 24f));
		return axis;
	}

	private NumberAxis createVerticalAxis(double maxY) {
		NumberAxis axis = new NumberAxis("Relative Frequency");	
		axis.setTickUnit(new NumberTickUnit(maxY / 4.));
		java.text.NumberFormat formatter = java.text.NumberFormat.getInstance();
		formatter.setMaximumFractionDigits(1);
		axis.setNumberFormatOverride(formatter);
		axis.setTickLabelFont(axis.getTickLabelFont().deriveFont(Font.BOLD, 22f));
		axis.setLabelFont(axis.getLabelFont().deriveFont(Font.BOLD, 24f));
		return axis;
	}

	private DrawingSupplier createDrawingSupplier() {
		Color[] colors = new Color[] { Color.RED, Color.GREEN.darker(), Color.BLUE };
		return new DefaultDrawingSupplier(colors, // Use our own colors.
			DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE, 
			DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE, 
			DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE, 
			DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
	}

	public static void main(String[] args) throws java.io.IOException, MixomaticException {
		edu.uab.ssg.mixomatic.helper.PValueParser parser = new edu.uab.ssg.mixomatic.helper.PValueParser();
		double[] pvalues = parser.parse(new java.io.FileInputStream(args[0]), new edu.uab.ssg.mixomatic.helper.PValueParser.BadFormatHandler() {
			public void handleBadPValue(String badPValue) {
				System.err.println("Skipped bad input: " + badPValue);
			}
		});
		MixtureModel.Estimator estimator = new edu.uab.ssg.mixomatic.flanagan.BoundedOptimizer();
		MixtureModel.Estimate estimate = estimator.estimateParameters(pvalues);
		ProbabilityDensityFunction function = new edu.uab.ssg.mixomatic.jsci.DefaultProbabilityDensityFunction();
		Histogram histogram = new Histogram(estimate, pvalues, function);
		histogram.addSubtitle(args[0]);
		histogram.write(new FileOutputStream("histogram.png"), 1024, 768);
	}
}
