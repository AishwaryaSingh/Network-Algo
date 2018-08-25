package NADS_Project;

import java.io.BufferedReader;
import java.io.BufferedWriter;	
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BloomFilter {
	static HashSet<String> elements = new HashSet<String>();
	static HashSet<String> members = new HashSet<String>(); //number of members
	static BitSet bloomFilter = new BitSet();
	static double m = 12448040;
	static double n = 100000;
	static double k = 4;
	static double p = 0.000001;
	static double f = 0.0; //This will be determined by us

	static LinkedHashMap<String,HashSet<String>> Hash_List = new LinkedHashMap<String,HashSet<String>>();
	
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
	
	public static void hash_members() throws Exception
	{
		BufferedReader br=new BufferedReader(new FileReader("C:\\NADS\\traffic.txt"));  
        String line=br.readLine();
        int l=0;
        while((line= br.readLine())!= null)
        {
            String[] a = line.split("\\s+");
            String element =a[0]+"."+a[1];
            if(l<n)
                members.add(element); //members
            else
            	elements.add(element); //just my elements
            l++;
        }  
        br.close();
	}
	
	public static void setBloom() throws Exception
	{
		Iterator<String> itr = members.iterator();
		while(itr.hasNext())
		{
			String pair = itr.next();
			int index = 0;
			for(int i=0;i<k;i++)
			{
				int temp = Math.abs(MurmurHash.hash32(pair,i));
				index = temp % (int)m;
				bloomFilter.set(index);
			}
		}
	}
	
	public static double testMemberShip() throws Exception
	{ 
		Iterator<String> itr = elements.iterator();
		while(itr.hasNext())
		{
			int index = 0;
			int cnt = 0;
			String pair=itr.next();
			for(int i=0;i<k;i++)
			{
				int temp = Math.abs(MurmurHash.hash32(pair,i));
				index = temp % (int)m;
				if(bloomFilter.get(index))
				{
					cnt++;
				}
			}
			if(cnt==k) //hits all ones
			{
				if(!members.contains(pair)) //check if its member.
				{
					f++;
				}
			}
			cnt=0;
		}
		return f;
	}
	
	public static void main(String args[]) throws Exception
	{
		double false_negative = 0; //always 0
		hash_members();
		setBloom();
		double false_positive_count=testMemberShip();
		double false_positive_ratio = false_positive_count/n;
		System.out.println("Calculated Results:\n\nFalse Positive Ratio:\t"+false_positive_ratio+"\n"
				+"False Positive Count:\t"+f+"\n"
				+"False Negative:\t\t"+false_negative+"\n\nWhere I made the following assumptions:\n\n"
				+"p:\t"+p+"\n"
				+"k:\t"+k+"\n"
				+"n:\t"+n+ "\n"
				+"m:\t"+m);
	}
}




