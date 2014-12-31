/**
 * This class is used to create a chart/graph
 */
package com.ihsinformatics.tbr4mobile.util;

import android.content.Context;
import android.graphics.Color;

import com.ihsinformatics.tbr4mobile.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.LineGraphView;

/**
 * @author owais.hussain@irdresearch.org
 *
 */
public class GraphUtil 
{
	/**
	 * @param context 
	 * @param title 
	 */
	public static GraphView getLineGraph (Context context, String title)
	{
		GraphView graphView = new LineGraphView (context, title);
		graphView.getGraphViewStyle ().setGridColor (Color.GRAY);
		graphView.getGraphViewStyle ().setVerticalLabelsColor (Color.RED);
		graphView.getGraphViewStyle ().setTextSize (context.getResources ().getDimension (R.dimen.medium));
		graphView.setViewPort (2, 10);
		graphView.setScrollable (true);
		graphView.setScalable (true);
		graphView.setShowLegend (true);
		graphView.setLegendAlign(LegendAlign.BOTTOM);
		graphView.setLegendWidth(220);	
		return graphView;
	}
}
