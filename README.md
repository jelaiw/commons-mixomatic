# commons-mixomatic
A Java API implementing:

1. An estimator for the mixture model described in the paper 
at http://dx.doi.org/10.1016/S0167-9473(01)00046-9.
  * See `edu.uab.ssg.mixomatic` for data model.
  * See `edu.uab.ssg.mixomatic` for mix-o-matic histogram plot.
  * See `edu.uab.ssg.mixomatic.helper` for demo code.
2. A parametric bootstrap procedure for estimating the expected discovery rate in a high dimensional biology experiment. 
See paper at http://dx.doi.org/10.1191/0962280204sm369ra.
  * See `edu.uab.ssg.mixomatic.power` for data model
  * See `edu.uab.ssg.mixomatic.power.plot` for EDR, TP, and TN plots.

Please see [javadocs](https://jelaiw.github.io/commons-mixomatic/javadoc/) for further detail.

### Example mix-o-matic histogram
This is the plot most people want to see after fitting the mixture model.

![mix-o-matic histogram example](https://jelaiw.github.io/commons-mixomatic/javadoc/edu/uab/ssg/mixomatic/plot/doc-files/Histogram-1.png)

### Example "combined" power plot
This is a useful summary plot, combining EDR, TP, and TN at a fixed threshold for significance.

![combined power plot example](https://jelaiw.github.io/commons-mixomatic/javadoc/edu/uab/ssg/mixomatic/power/plot/doc-files/CombinedPlot-1.png)

