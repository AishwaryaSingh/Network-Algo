package NADS_Project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.jfree.ui.RefineryUtilities;

public class CountMin {
	private static HashMap<Integer,Integer> resultGraph = new HashMap<Integer,Integer>();    
	static LinkedHashMap<String,Integer> pairFlow = new LinkedHashMap<String,Integer>();

	static int d=10; //rows
	static int w=100000; //columns
	static int[][] countMinSketch = new int[d][w];

	public static void initialize_CSE() {
		int i=0,j=0;
		for(i=0;i<d;i++) {
			for(j=0;j<w;j++){
				countMinSketch[i][j] = 0;						
			}
		}
	}
	
	public static void setBitsCSE() throws Exception
	{
		int index;
        BufferedReader br=new BufferedReader(new FileReader("C:\\NADS\\traffic.txt"));  
        String line= br.readLine(); //removes first text line
        while((line= br.readLine())!= null)
        {
        	String[] a = line.split("\\s+");
        	String src = a[0];
        	String dst = a[1];
        	String pairIP = src+"d"+dst;
        	int flow_size= Integer.parseInt(a[2]);
        	pairFlow.put(pairIP, flow_size);
        	for(int i=0;i<d;i++)
        	{
        		index = Math.abs(MurmurHash.hash32(a[0]+"."+a[1],i)) % w; //use per source-dest
    			countMinSketch[i][index]+=flow_size; //because it will hashed that many times.
        	}
        }
        br.close();    
	}
	
	//ONLINE OPERATION
	public static void pointQuery() throws Exception
	{
	    BufferedWriter out = new BufferedWriter(new FileWriter("C:\\NADS\\output_cse.txt"));
        Set<Entry<String,Integer>> entries = pairFlow.entrySet();
        for(Entry<String,Integer> t: entries)
        {
        	String pairIP = t.getKey();
        	String[] a = pairIP.split("d");
        	String src = a[0];
        	String dst = a[1];
        	int actual_flow=t.getValue();
        	int min = Integer.MAX_VALUE;      //MAX Integer value
        	for(int i=0;i<d;i++)
        	{    		
        		int index = Math.abs(MurmurHash.hash32(src+"."+dst,i)) % w; //use per source-dest
        		int val = countMinSketch[i][index];        	
    			if(val<min){
    				min=val;
    			}
        	}        	
        	System.out.println(src+" "+dst+" "+actual_flow+" "+min+"\n");
        	out.write(src+" "+dst+" "+actual_flow+" "+min);
			resultGraph.put(actual_flow, min);			
		}        	
        out.close();
	}
	
	
	public static void main (String[] args) throws Exception{
		//Initialize the array R with 0s
		initialize_CSE();
		
		setBitsCSE();
		
		pointQuery();
	
		//Plot
		ScatterPlot chart = new ScatterPlot("NADS Project 5", "Count Min Sketch", resultGraph);
		chart.pack( );          
		RefineryUtilities.centerFrameOnScreen( chart );          
		chart.setVisible( true ); 
	}
	
}
