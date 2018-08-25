package NADS_Project;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
//import java.awt.*;

import org.jfree.ui.RefineryUtilities;

public class ProbabilisticCounting 
{	

	private static HashMap<Integer,Integer> resultGraph = new HashMap<Integer,Integer>();    
	
	static LinkedHashMap<String,BitSet> hashtable = new LinkedHashMap<String,BitSet>();
	static LinkedHashMap<String,HashSet<String>> Hash_List = new LinkedHashMap<String,HashSet<String>>();
	static int m = 1000;
 
	public static void setHashList(String source, String destination)
	{
		 if(!hashtable.containsKey(source))
         {
         	HashSet<String> List_of_dip = new HashSet<String>();
         	List_of_dip.add(destination);
         	Hash_List.put(source,List_of_dip);
         }else{
        	 HashSet<String> hk = Hash_List.get(source);
	         hk.add(destination);
	         Hash_List.put(source, hk);
         }
	}
	public static void createHashTable() throws Exception
	{
		int index;
        BufferedReader br=new BufferedReader(new FileReader("C:\\NADS\\traffic.txt"));  
        String line="";
        while((line= br.readLine())!= null)
        {
            String[] a = line.split("\\s+");
            setHashList(a[0],a[1]);
            if(!hashtable.containsKey(a[0]))
            {
            	BitSet bitmap = new BitSet(m);
            	index = (Math.abs(a[1].hashCode())%m);
            	bitmap.set(index);
	           	hashtable.put(a[0],bitmap);
            }
            else
            {
            	BitSet temp_bitmap = hashtable.get(a[0]);
            	index = (Math.abs(a[1].hashCode())%m);
            	temp_bitmap.set(index);
	           	hashtable.put(a[0],temp_bitmap);
            }
        }
        br.close();    
	}
	
	public static void main(String args[]) throws Exception
	{
		 createHashTable();
         //Hash_List created
		 
	     BufferedWriter pc_out = new BufferedWriter(new FileWriter("C:\\NADS\\output_pc.txt"));
	     
	     //traverse the hastable
         Set<Entry<String,BitSet>> entires = hashtable.entrySet();
         for(Entry<String,BitSet> ent: entires)
         {
             String source_ip = ent.getKey();
             BitSet bitmap = ent.getValue();
             int actual_size = Hash_List.get(source_ip).size();
             int numberOf1s = bitmap.cardinality();
            
             float Vm = (float)(m-numberOf1s)/m;
             
             float estimated_size = -1 * (m * (float)Math.log(Vm));

             pc_out.write(source_ip+" "+actual_size+" "+estimated_size+"\n");
 			 resultGraph.put((int)actual_size, (int)estimated_size);			
             
         }
         pc_out.close();
         
     	ScatterPlot chart = new ScatterPlot("NADS Project 2", "Probabilistic Counting", resultGraph);
		chart.pack( );          
		RefineryUtilities.centerFrameOnScreen( chart );          
		chart.setVisible( true ); 
	}
}
