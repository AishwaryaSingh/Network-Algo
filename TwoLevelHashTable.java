package NADS_Project;

import java.io.*;
import java.util.*;
import java.util.Set;
import java.util.Map.Entry;

public class TwoLevelHashTable 
{	
	static LinkedHashMap<String,HashSet<String>> hashtable = new LinkedHashMap<String,HashSet<String>>();

	public static void createHashTable() throws Exception
	{
        BufferedReader br=new BufferedReader(new FileReader("C:\\NADS\\traffic.txt"));  
        String line="";
        while((line= br.readLine())!= null)
        {
            String[] a = line.split("\\s+");
            if(!hashtable.containsKey(a[0]))
            { 
            	HashSet<String> dip = new HashSet<String>();
            	dip.add(a[1]);
            	hashtable.put(a[0],dip);
            }
            else
            {
	           	HashSet<String> temp = hashtable.get(a[0]);
	           	temp.add(a[1]);
	           	hashtable.put(a[0],temp);
            }
        }
        br.close();   
	}
	
	public static void main(String args[]) throws Exception
	{
		 createHashTable();
         //hashtable created
		 
	     BufferedWriter pc_out = new BufferedWriter(new FileWriter("C:\\NADS\\output_2level.txt"));
	        
	     //traverse the hastable
         Set<Entry<String,HashSet<String>>> entires = hashtable.entrySet();
         for(Entry<String,HashSet<String>> ent: entires)
         {
             String source_ip = ent.getKey();
             HashSet<String> destination_ips = ent.getValue();
             int actual_size = destination_ips.size();
            
             pc_out.write(source_ip+" "+actual_size+"\n");
             System.out.println(source_ip+" "+actual_size);
         }
         pc_out.close();
	}
}
