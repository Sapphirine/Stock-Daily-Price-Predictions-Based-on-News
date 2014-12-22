package com.prediction.LinearRegression;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class LinearRegression {


  public static class LinearRegressionMapper extends MapReduceBase implements Mapper<LongWritable, Text, LongWritable, FloatWritable>
	{

		private Path[] localFiles;
		private String[] theta;

		private static String runNum = "";
		// get the Number of running time and the theta
		public void configure(JobConf job) {
			runNum = job.get("runNum");
			theta = job.getStrings("theta");
		}
		
		public void map(LongWritable key, Text value, OutputCollector<LongWritable, FloatWritable> output,
				Reporter reporter) throws IOException {
			
			String line = value.toString();
			String[] features = line.split(",");
			
			List<Float> values = new ArrayList<Float>();
			
			/**
			 * read the values and convert them to floats
			 */
			for(int i = 0; i<features.length; i++)
			{
				values.add(new Float(features[i]));
			}
			
			/**
			 * calculate the costs
			 * 
			 */
			
			float[] result = costs(values);
			for (int i = 0; i < result.length; i++) {
				output.collect(new LongWritable(i + 1), new FloatWritable(result[i]));

			}

		}
		

		private final float[] costs(List<Float> values)
		{			
			
			/**
			* Load the cache files
			*/
			
			float[] costs = new float[values.size() + 1];



			/** first value is the y value **/
			float y = values.get(0);
		
			/**
			 * Calculate the costs for each record in values
			 */
			for(int j = 0; j < values.size(); j++)
			{

					//bias calculation
					if(j == 0)
						costs[0] += (new Float(theta[j]))*1;
					else
						costs[0] += (new Float(theta[j]))*values.get(j);
				
			}

			
			
			/** get the costs. 
			 *  we need to get the product of the error and each feature of the record for updating theta
			 *  And we store the squared error between real y and predicted y
			 */
			float error = y - costs[0];
			costs[0] = error;
			for (int i = 1; i < theta.length; i++) {
				costs[i] = values.get(i) * error;
			}
			
			costs[values.size()] = error * error;
		
			return costs;
			
		}
		
	}
	
	public static void fileWriter(String fileName, String content) {
		try {
			FileWriter writer = new FileWriter(fileName, false);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static class LinearRegressionReducer extends MapReduceBase implements Reducer<LongWritable, FloatWritable, LongWritable, FloatWritable>
	{
		private static String runNum = "";
		public void configure(JobConf job) {
			runNum = job.get("runNum");
		}
		
		public void reduce(LongWritable key, Iterator<FloatWritable> value,
				OutputCollector<LongWritable, FloatWritable> output, Reporter reporter)
				throws IOException {

			/**
			 * The reducer just has to sum all the values for a given key
			 * 
			 */
			
			float sum = 0;

			while(value.hasNext())
			{
				sum += value.next().get();
			}
			
			// write the all the information we need to update theta into a local file
			fileWriter("/home/bigdata/BigData/test/cost/" + key + ".csv", sum + "");

			output.collect(key, new FloatWritable(sum));

		}

	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//the class is LinearRegression
		JobConf conf = new JobConf(LinearRegression.class);
		
		//the jobname is linearregression (this can be anything)
		conf.setJobName("linearregression");

		*/
		//set the output key class 
		conf.setOutputKeyClass(LongWritable.class);
		//set the output value class
		conf.setOutputValueClass(FloatWritable.class);
		
		//set the mapper
		conf.setMapperClass(LinearRegressionMapper.class);
		//set the combiner
		conf.setCombinerClass(LinearRegressionReducer.class);
		//set the reducer
		conf.setReducerClass(LinearRegressionReducer.class);
		
		//set the input format
		conf.setInputFormat(TextInputFormat.class);
		//set the output format
		conf.setOutputFormat(TextOutputFormat.class);
		
		
		for (int i = 0; i < 2; i++) {
			//the number of running time
			String num = i + ""; 
			//set the input path (from args)
			FileInputFormat.setInputPaths(conf, new Path(args[0]));
			//set the output path (from args)
			FileOutputFormat.setOutputPath(conf, new Path(args[1] + num));
			
			try {
			
				// To update the thetas, we need first to read the thetas for last run
				String path = "/home/bigdata/BigData/test/theta/" + num + ".csv";
				File file = new File(path);
				
				
				/**Creates a FileInputStream by opening a connection to an actual file, the file named by the File object file in the file system
				 **/
				FileInputStream fis = new FileInputStream(file);
					
				/**A BufferedInputStream adds functionality to another input stream-namely, the ability to buffer the input and to support the mark and reset methods. 
				 **/
				BufferedInputStream bis = new BufferedInputStream(fis);
					                       
				/** Reads text from a character-input stream, buffering characters so as to provide for the efficient reading of characters, arrays, and lines.
				 **/
				BufferedReader d = new BufferedReader(new InputStreamReader(bis));

				String line = d.readLine();

				/** all right we have all the theta values, lets convert them to floats **/
				String[] theta = line.split(",");
				
				conf.set("runNum", num);
				conf.setStrings("theta", theta);
				
				d.close();

				try {
					JobClient.runJob(conf);
				} catch (IOException e) {
					e.printStackTrace();
				}
			
				// update the thetas for next run			
				StringBuffer sb = new StringBuffer();
				float newTheta;
				// read the costs from local file
				for (int j = 0; j < theta.length; j++) {
					String costPath = "/home/bigdata/BigData/test/cost/" + (j + 1) + ".csv";
					File costFile = new File(costPath);
					try {
						/**Creates a FileInputStream by opening a connection to an actual file, the file named by the File object file in the file system
						 **/
						FileInputStream costFis = new FileInputStream(costFile);
						
						/**A BufferedInputStream adds functionality to another input stream-namely, the ability to buffer the input and to support the mark and reset methods. 
						 **/
						BufferedInputStream costBis = new BufferedInputStream(costFis);
						                       
						/** Reads text from a character-input stream, buffering characters so as to provide for the efficient reading of characters, arrays, and lines.
						 **/
						BufferedReader costD = new BufferedReader(new InputStreamReader(costBis));

						String cost = costD.readLine();
						
						// update the theta
						newTheta = new Float(theta[j]) + 2 * new Float(cost);
						String nt = newTheta + "";
						if (j < theta.length - 1)
							sb.append(nt + ",");
						else 
							sb.append(nt);
						
						costD.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}	
					
				}
	
				// store the new thetas into local file for next run
				int newNum = Integer.parseInt(num) + 1;
				String stNum = newNum + "";
				String newPath = "/home/bigdata/BigData/test/theta/" + stNum + ".csv"; 
				fileWriter(newPath, sb.toString());

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}


		

	}

}

