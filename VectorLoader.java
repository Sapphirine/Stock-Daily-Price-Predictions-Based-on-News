package TFReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.SequentialAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.math.Vector.Element;




public class App 
{

	
	public static void main(String[] args)
	{
	 Configuration conf = new Configuration();
	 FileSystem fs;
	 SequenceFile.Reader read;
	 try {
	  fs = FileSystem.get(conf);
	  read = new SequenceFile.Reader(fs, new Path("dictionary.file-0"), conf);
	  IntWritable dicKey = new IntWritable();
	  Text text = new Text();
	  HashMap dictionaryMap = new HashMap();
	  try {
	      while (read.next(text, dicKey)) {
	         dictionaryMap.put(text.toString(),Integer.parseInt(dicKey.toString()));
	      }
	      System.out.println(dictionaryMap);
	      System.out.println("\n");
	      System.out.println(dictionaryMap.size());
	   } catch (NumberFormatException e) {
	       e.printStackTrace();
	   } catch (IOException e) {
	       e.printStackTrace();
	   }
	   read.close();
	   
	  int WORD1 = (Integer) dictionaryMap.get("stuck");
	 System.out.println(WORD1);
	  int WORD2 = (Integer) dictionaryMap.get("stress");
	 System.out.println(WORD2);
	 int WORD3 = (Integer) dictionaryMap.get("threaten");
	 System.out.println(WORD3);
	 int WORD4 = (Integer) dictionaryMap.get("vice");
	 System.out.println(WORD4);
	 int WORD5 = (Integer) dictionaryMap.get("reject");
	 System.out.println(WORD5);
	 int WORD6 = (Integer) dictionaryMap.get("severe");
	 System.out.println(WORD6);
	
	   
	   read = new SequenceFile.Reader(fs, new Path("part-r-00000"), conf);
	   Text key = new Text();
	   VectorWritable value = new VectorWritable();
	   SequentialAccessSparseVector vect;
	   ArrayList<Double> list1= new ArrayList();
	   ArrayList<Double> list2= new ArrayList();
	   ArrayList<Double> list3= new ArrayList();
	   ArrayList<Double> list4= new ArrayList();
	   ArrayList<Double> list5= new ArrayList();
	   ArrayList<Double> list6= new ArrayList();
	   while (read.next(key, value)) {
		   //System.out.println(value.get().asFormatString());
		   //System.out.println(value.get().get(BEST));
	
		   list1.add(value.get().get(WORD1));
		   list2.add(value.get().get(WORD2));
		   list3.add(value.get().get(WORD3));
		   list4.add(value.get().get(WORD4));
		   list5.add(value.get().get(WORD5));
		   list6.add(value.get().get(WORD6));
	        /*NamedVector test = (NamedVector) value.get();
	        vect= (SequentialAccessSparseVector)test.getDelegate();
	        int len = vect.size();
	        for( int i =0;i<len;i++ ){
	        	Element e=vect.getElement(i);
	           System.out.println("Token: "+dictionaryMap.get(e.index())+", TF-IDF weight: "+e.get()) ;
	          }*/		   
	         }
	         read.close();
	  	     System.out.println(list1);
		     System.out.println(list2);
		     System.out.println(list3);
		     System.out.println(list4);
		     System.out.println(list5);
		     System.out.println(list6);
		     String fileName = "list1.txt";
		     //fileWriter(fileName, "TF of Best\n");
		     for(Double e : list1){
		    	 fileWriter(fileName,e.toString());
		    	 fileWriter(fileName, "\n");
		     }
		     fileName = "list2.txt";
		     //fileWriter(fileName, "TF of Good\n");
		     for(Double e : list2){    	 
		    	 fileWriter(fileName,e.toString());
		    	 fileWriter(fileName, "\n");
		     }
		     fileName = "list3.txt";
		     //fileWriter(fileName, "TF of Good\n");
		     for(Double e : list3){    	 
		    	 fileWriter(fileName,e.toString());
		    	 fileWriter(fileName, "\n");
		     }
		     fileName = "list4.txt";
		     //fileWriter(fileName, "TF of Good\n");
		     for(Double e : list4){    	 
		    	 fileWriter(fileName,e.toString());
		    	 fileWriter(fileName, "\n");
		     }
		     fileName = "list5.txt";
		     //fileWriter(fileName, "TF of Good\n");
		     for(Double e : list5){    	 
		    	 fileWriter(fileName,e.toString());
		    	 fileWriter(fileName, "\n");
		     }
		     fileName = "list6.txt";
		     //fileWriter(fileName, "TF of Good\n");
		     for(Double e : list6){    	 
		    	 fileWriter(fileName,e.toString());
		    	 fileWriter(fileName, "\n");
		     }
		     
	  } catch (IOException e) {
	   // TODO Auto-generated catch block
	  e.printStackTrace();
	 }
	}
	public static void fileWriter(String fileName, String content) {  
        try {  
         
            FileWriter writer = new FileWriter(fileName, true);  
            writer.write(content);  
            writer.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
}
