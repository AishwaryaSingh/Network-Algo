package NADS_Project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.jfree.ui.RefineryUtilities;

public class VirtualFM {

	static LinkedHashMap<String,String> pairIPs = new LinkedHashMap<String,String>();
	private static HashMap<Integer,Integer> resultGraph = new HashMap<Integer,Integer>();    

	static int m = 1000000;
	static int s = 128;
	static int row = 100;
	static int[][] FMSketch = new int[row][m];
	
	static int[] R = new int[s];			//random number array	


	public static long ipToLong(String ipAddress) {
		String[] ipAddressInArray = ipAddress.split("\\.");
		long result = 0;
		for (int i = 0; i < ipAddressInArray.length; i++) {
			int power = 3 - i;
			int ip = Integer.parseInt(ipAddressInArray[i]);
			result += ip * Math.pow(256, power);
		}
		return result;
	  }

	public static void initialize_R(){
		for(int i=0; i<s; i++){
			Random rand = new Random();
			R[i] = (int) rand.nextInt(100000);
		}
	}

	public static int geometricHash(String IP)
	{
		int result = Math.abs(IP.hashCode());
		int n = 0;
	    int bits = (result*Integer.SIZE) * 8;
	    for (int i = 1; i < bits; i ++) {
	        if (result < 0) break;
	        n ++;
	        result <<= 1;
	    }
	    return n;
	}
	
	public static void setFMSketch() throws Exception {
		int indexFM;
		int indexRow;
        BufferedReader br=new BufferedReader(new FileReader("C:\\NADS\\traffic.txt"));  
        String line= br.readLine();
        while((line= br.readLine())!= null)
        {
        	String[] a = line.split("\\s+");
        	String sourceIP = a[0];
        	String destinationIP = a[1];
        	String pairIP=sourceIP+"d"+destinationIP;
        	pairIPs.put(pairIP, a[2]);
       		Long Long_src =  ipToLong(sourceIP);
       		int hashed_dest = Math.abs(destinationIP.hashCode())%s;
           	Long ind = Math.abs((Long_src^R[hashed_dest]));
           	indexFM = Math.abs(ind.hashCode())%m;
           	indexRow = geometricHash(destinationIP);
           	FMSketch[indexRow][indexFM]=1;
        }
        br.close();
	}
	
	//OFFLINE ESTIMATE
	public static void offLineEstimation() throws Exception{
		int i=0;
		double phi = 0.77351;
		int index;
		BufferedWriter FM_out = new BufferedWriter(new FileWriter("C:\\NADS\\output_vFM.txt"));
		Set<Entry<String,String>> entries = pairIPs.entrySet();
        for(Entry<String,String> t: entries)
        {
        	String act_flow_size=t.getValue();
        	int actual_flow=Integer.parseInt(act_flow_size);
        	
        	String pairIP = t.getKey();
        	String[] a = pairIP.split("d");
        	String src = a[0];
        	String dst = a[1];
        	
        	//for ns cap
        	int val=0;
        	int temp=0;
        	for(i=0;i<s;i++)
        	{    		
        		Long Long_src =  ipToLong(src);
        	   	Long ind = Math.abs((Long_src^R[i]));
            	index = Math.abs(ind.hashCode())%m;
            	for(int j=row-1;j>0;j--)
            	{	if(FMSketch[j][index]==1) //leading zeros
	        		{	
	        			temp+=j;	
	        		}
            		else
            			break;
            	}
            	val=val+temp+1;
        	 }        	
        	double Z = val/s;
        	double ns = (s*Math.pow(2,Z))/phi;
        	
        	//for n cap
        	int nsum=0;
        	int temp2=0;
        	for(i=0;i<m;i++)
        	{  
        		for(int j=row-1;j>0;j--)
            	{
        			if(FMSketch[j][i]==1)
	        		{	
        				temp2+=j;
        			}
        			else 
        				break;
            	}
        		nsum=nsum+temp2+1;
        	}      
        	double Zm = nsum/m;  
        	double n=(m*Math.pow(2,Zm))/phi;
        	
			int term1A = m*s;
			int term1B = m-s;
			int term1 = term1A/term1B;

			double term2A = ns/s;
			double term2B = n/m;
			double term2 = term2A-term2B;

			double est = term1*term2;
			FM_out.write(src+" "+dst+" "+actual_flow+" "+est+"\n");
			resultGraph.put(actual_flow,(int) est);
        }  
        FM_out.close();
	}
	
	public static void main(String args[]) throws Exception
	{
		initialize_R();
		setFMSketch();
		offLineEstimation();
     	ScatterPlot chart = new ScatterPlot("NADS Project 4", "Virtual FM Sketch", resultGraph);
		chart.pack( );          
		RefineryUtilities.centerFrameOnScreen( chart );          
		chart.setVisible( true ); 
	}
}
