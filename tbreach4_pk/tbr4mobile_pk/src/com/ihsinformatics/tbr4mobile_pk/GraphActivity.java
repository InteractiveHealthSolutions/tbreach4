/**
 * This activity is used to display line/bar graph
 */

package com.ihsinformatics.tbr4mobile_pk;

import com.ihsinformatics.tbr4mobile_pk.R;

import com.ihsinformatics.tbr4mobile_pk.util.GraphUtil;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

public class GraphActivity extends Activity
{
	public static final String	TITLE	= "TITLE";
	public static final String	X_DATA	= "X_DATA";
	public static final String	Y_DATA	= "Y_DATA";
	LinearLayout				graphLayout;

	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);
		Bundle extras = getIntent ().getExtras ();
		String title = extras.getString (TITLE);
		double[] xData = extras.getDoubleArray (X_DATA);
		double[] yData = extras.getDoubleArray (Y_DATA);
		graphLayout = (LinearLayout) findViewById (R.graph_id.graphLinearLayout);
		showGraph (title, xData, yData);
	}

	public void showGraph (String graphName, double[] xValues, double[] yValues)
	{
		GraphView graphView = GraphUtil.getLineGraph (GraphActivity.this, graphName);
		GraphViewData[] graphData = new GraphViewData[xValues.length];
		for (int i = 0; i < xValues.length; i++)
		{
			graphData[i] = new GraphViewData (xValues[i], yValues[i]);
		}
		GraphViewSeries graphSeries = new GraphViewSeries (graphName, new GraphViewSeriesStyle (Color.BLUE, 1), graphData);
		graphView.addSeries (graphSeries);
		graphLayout.addView (graphView);
	}
}
