package edu.uab.ssg.mixomatic.power.plot;

import org.jfree.chart.axis.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.general.DatasetUtilities;
import java.awt.*;
import java.awt.geom.*;

/**
 * @author Jelai Wang
 */

/* package private */ final class PlotFactory {
	/* package private */ static XYPlot createPlot(XYDataset dataset) {
		XYItemRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setBaseStroke(new java.awt.BasicStroke(2.0f));
		XYPlot plot = new XYPlot(dataset, createHorizontalAxis(dataset), createVerticalAxis(), renderer);
		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);
		return plot;
	}

	private static NumberAxis createHorizontalAxis(XYDataset dataset) {
		// Investigate newer LogAxis??
		LogarithmicAxis axis = new LogarithmicAxis("Sample size (N per Group)");
		axis.setTickLabelFont(axis.getTickLabelFont().deriveFont(22f));
		axis.setLabelFont(axis.getLabelFont().deriveFont(Font.BOLD, 22f));
		return axis;
	}

	private static NumberAxis createVerticalAxis() {
		NumberAxis axis = new NumberAxis("Expected Proportion");
		axis.setRange(-0.05, 1.05); // This looks better.
		axis.setTickUnit(new NumberTickUnit(0.2));
		axis.setTickLabelFont(axis.getTickLabelFont().deriveFont(22f));
		axis.setLabelFont(axis.getLabelFont().deriveFont(Font.BOLD, 22f));
		return axis;
	}

	// See org.jfree.util.ShapeUtilities in the JCommon API.

	/* package private */ static Shape getSquare(double size) {
		double half = size / 2.;
		return new Rectangle2D.Double(-half, -half, size, size);
	}

	/* package private */ static Shape getCircle(double size) {
		double half = size / 2.;
		return new Ellipse2D.Double(-half, -half, size, size);
	}

	/* package private */ static Shape getTriangle(double size) {
		double half = size / 2.;
		return new Polygon(new int[] { 0, (int) half, (int) -half }, new int[] { (int) -half, (int) half, (int) half }, 3);
	}

	/* package private */ static Shape getDiamond(double size) {
		double half = size / 2.;
		return new Polygon(new int[] { 0, (int) half, 0, (int) -half }, new int[] { (int) -half, 0, (int) half, 0 }, 4);
	}

	/* package private */ static Shape getUpsideDownTriangle(double size) {
		double half = size / 2.;
		return new Polygon(new int[] { (int) -half, (int) half, 0 }, new int[] { (int) -half, (int) -half, (int) half }, 3);
	}

	/* package private */ static Shape getFourPointStar(double size) {
		double half = size / 2., quarter = size / 4.;
		GeneralPath star = new GeneralPath();
		star.moveTo(0f, (float) quarter);
		star.lineTo((float) half, (float) half);
		star.lineTo((float) quarter, 0f);
		star.lineTo((float) half, (float) -half);
		star.lineTo(0f, (float) -quarter);
		star.lineTo((float) -half, (float) -half);
		star.lineTo((float) -quarter, 0f);
		star.lineTo((float) -half, (float) half);
		star.closePath();
		return star;
	}
}
