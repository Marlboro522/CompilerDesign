package ADT;
import java.io.*;
public class ReserveTable {
	 //class memebers or something here.. need to check this once
	          private String[] names;
	          private int[] codes;
	          private int size;
	          //constuctor begin
	          public ReserveTable(int needed_size) {
	        	  names=new String[needed_size];
	        	  codes=new int[needed_size];
	        	  size=0;
	          } 
	          //methods of this class
	          ///you can compute the current input by the size variable. It always changes by 1 when there is new
	          //element
	          public String pad(String input, int len, boolean left) {
	        	    StringBuilder builder = new StringBuilder(input);

	        	    while (builder.length() < len) {
	        	        if (left) {
	        	            builder.insert(0, " ");
	        	        } else {
	        	            builder.append(" ");
	        	        }
	        	    }

	        	    return builder.toString();
	        	}
	          public int Add(String name, int code) {
	        	  names[size]=name;
	        	  codes[size]=code;
	        	  size++;
	        	  return size-1; //index would be always 1 more than the size
	          } public int LookupName(String name) {
	        	  for(int i =0;i<size;i++) {
	        		  if(names[i].compareToIgnoreCase(name)==0) {
	        			  return codes[i];
	        		  }
	        	  }return -1;
	          } public String LookupCode(int code) {
	        	  for(int i=0;i<size;i++) {
	        		  if(codes[i]==code) {
	        			  return names[i];
	        		  }
	        	  }return ""; //em,pty string  ...Need to check again if that is an empty strign or not;
	          } public void PrintReserveTable(String fileName) {
	        		  try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
	        	            writer.write("Index\tName\tCode");
	        	            writer.newLine();

	        	            for (int i = 0; i < size; i++) {
	        	            	String code=pad(pad(Integer.toString(codes[i]),3,true),8,false);
	        	            	String name=pad(pad(names[i],3,true),8,false);
	        	                writer.write(String.format("%d\t%s\t%s%n", i + 1, name, code));
	        	            }
	        	        } catch (IOException e) {
	        	            e.printStackTrace();
	        	        }
	        	    
//	        		 
	          } public void PrintReserveTable (File file) {
	        	  PrintReserveTable(file.getAbsolutePath());
	          }
}
