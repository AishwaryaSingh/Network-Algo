package NADS_Project;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.util.ShapeUtilities;

public class ScatterPlot extends ApplicationFrame 
{
    private static final int truth_line = 0;

	public ScatterPlot( String applicationTitle, String chartTitle, HashMap result )
	{
		super(applicationTitle);
		JPanel jpanel = createDemoPanel(chartTitle,result);
		jpanel.setPreferredSize(new Dimension(1000, 1000));
		setContentPane(jpanel);
	}
	
	public JPanel createDemoPanel(String chartTitle, HashMap result ) 
	{
		JFreeChart jfreechart = ChartFactory.createScatterPlot(chartTitle , "Actual Cardinality" , "Estimated Cardinality"
    		  , createDataset(chartTitle,result), PlotOrientation.VERTICAL, true, true, false);
		
        XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
        xyPlot.setDomainCrosshairVisible(true);
        xyPlot.setRangeCrosshairVisible(true);
        
        XYItemRenderer renderer = xyPlot.getRenderer();
        renderer.setSeriesPaint(0, Color.blue);
//
//        xyPlot.setRenderer(new XYLineAndShapeRenderer());
//		
//		Shape diamond = ShapeUtilities.createDiamond(3);		    
//	    XYLineAndShapeRenderer renderer2= (XYLineAndShapeRenderer)xyPlot.getRenderer();
//	    renderer2.setSeriesShape(1,diamond);
//	    renderer2.setSeriesPaint(1,Color.red);
//	    renderer2.setSeriesShapesVisible(1, true);
	    
	    NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
        domain.setRange(0.00, 1000.00);
        domain.setTickUnit(new NumberTickUnit(5.0));
        domain.setVerticalTickLabels(true);
        NumberAxis range = (NumberAxis) xyPlot.getRangeAxis();
        range.setRange(0.0, 1000.0);
        range.setTickUnit(new NumberTickUnit(5.0));
        return new ChartPanel(jfreechart);
	    
	}

   private XYDataset createDataset(String chartTitle, HashMap result)
   {
      XYSeries series = new XYSeries(chartTitle);  
      Iterator it = result.entrySet().iterator();
      while (it.hasNext())
      {
          Map.Entry pair = (Map.Entry)it.next();
          series.add((Integer)pair.getKey(),(Integer)pair.getValue());
          it.remove(); // avoids a ConcurrentModificationException
      }
      XYSeriesCollection dataset = new XYSeriesCollection( );          
      dataset.addSeries(series);
      return dataset;
   }

   public static void main( String[ ] args ){}
}