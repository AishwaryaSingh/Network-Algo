package NADS_Project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jfree.ui.RefineryUtilities;

public class VirtualBitmap {
	
	//on init
	static Map<String,List<String>> ht = new HashMap<String,List<String>>();

	String line = "";

	String sourceIP = "";
	static String destIP = "";
	
	int actualCardinality = 0;
	double estimatedCardinality = 0;

	final static int m = 10000000; //size of physical bit
	final static int s = 1500;		//size of virtual bit

	static BitSet B = new BitSet(m);
	
	static double Vm = 0;				//Fraction of ZEROS in Physical Bitmap B
	static double Vs = 0;				//Fraction of ZEROS in Virtual Bitmap X(src)
	
	static int[] R = new int[s];			//random number array	
	
	static LinkedHashSet<String> sources_IP= new LinkedHashSet<String>();
	static LinkedHashSet<String> pair_IPs = new LinkedHashSet<String>();

	private static HashMap<Integer,Integer> resultGraph = new HashMap<Integer,Integer>();    

	/* Initialize Random number array R*/
	public static void initialize_R(){
		for(int i=0; i<s; i++){
			Random rand = new Random();
			R[i] = (int) rand.nextInt(10000000);
		}
	}

	public static void setBitMap() throws Exception
	{
        BufferedReader br=new BufferedReader(new FileReader("C:\\NADS\\traffic.txt"));  
        String line="";
        br.readLine();  //remove the first line
        while((line= br.readLine())!= null)
        {
            String[] a = line.split("\\s+");
            sources_IP.add(a[0]); //add to a pool of source IPs
            pair_IPs.add(a[0]+"d"+a[1]);
            List<String> dip = new ArrayList<String>();
            if(!ht.containsKey(a[0]))
            {
            	dip = new ArrayList<String>();
            	dip.add(a[1]);
                ht.put(a[0],dip);
            }else{
            	dip=ht.get(a[0]);
            	dip.add(a[1]);
                ht.put(a[0],dip);
            }
            String CurrentDestinationIP = a[1];
            int hashD = Math.abs(CurrentDestinationIP.hashCode());
            int indexForR = hashD%s;
            int randomNumbR = R[indexForR];
            String SourceIP = a[0];
            Long SourceLongIP = ipToLong(SourceIP);
            Long indexOut =  SourceLongIP ^ randomNumbR; //XOR src with R[i]
            int ind = Math.abs(indexOut.hashCode())% m;
            B.set(ind); //physical bit
        }
        br.close();    
	}

	public static void decodeOFF() throws IOException
	{
		BufferedWriter pc_out = new BufferedWriter(new FileWriter("C:\\NADS\\output_vb.txt"));
		for(String sourceIP:sources_IP)
		{
			int numOf0 = 0;
			int actualCardinality = ht.get(sourceIP).size();
			Long s_n = ipToLong(sourceIP);
			for(int i=0;i<s;i++)
			{
				Long indexB = Math.abs(s_n^R[i]);
				if(B.get(Math.abs(indexB.hashCode()%m))==false)
					numOf0++;
			}	
			Vm = (double)(m-B.cardinality())/m;
			Vs = (double)numOf0/s;			 
			
			double est = s*Math.log(Vm)+(-1*s*Math.log(Vs));
			//double est = s * Math.log(Vm/Vs);
			//considering negative as zero
			if(est<0)
				est=0;
			pc_out.write(sourceIP+" "+actualCardinality+" "+est+"\n");
			resultGraph.put(actualCardinality, (int)est);			
		}
		pc_out.close();		
	}
	
	//Taken off the Internet
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

	public static void main (String[] args) throws Exception{
		// Initialize the array R with random numbers
		initialize_R();
		//Initialize the bitmap with 0
		B.set(0,B.size()-1,false);
		//createHashTable and encode
		setBitMap();
		//Calculate estimated flowsize on X(src) and write to file
		decodeOFF();
		//Plot
		ScatterPlot chart = new ScatterPlot("NADS Project 3", "Virtual Bitmap", resultGraph);
		chart.pack( );          
		RefineryUtilities.centerFrameOnScreen( chart );          
		chart.setVisible( true ); 
	}
}