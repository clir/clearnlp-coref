/**
 * Copyright 2015, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.clir.clearnlp.coreference.benchmark;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import edu.emory.clir.clearnlp.util.Joiner;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 20, 2015
 */
public class LamdaBenchMark {
	final static int ITER = 1000;
	final static int SIZE = 1000000;	// 5 millions
	
	public static long startTime;
	public static List<Integer> output;
	public static List<Long> timeLog;
	public static List<Integer> warmupList, testList;
	
	public static void init(List<Integer> warmupList, List<Integer> testList){
		for(int i = 0; i < SIZE; i++){
			warmupList.add(i);
			testList.add(SIZE-i);
		}
	}
	
	@Test
	@Ignore
	public void testFilter() throws IOException{
		timeLog = new ArrayList<>();
		warmupList = new ArrayList<>();
		testList = new ArrayList<>();
		init(warmupList, testList);
		
		System.out.println("Warming up...");
		
		for(int i = 0; i < ITER; i++){
//			output = new ArrayList<>();

			// Lamda
//			Joiner.join(warmupList.stream().filter(num -> num%2 == 0).map(num -> num+1).collect(Collectors.toCollection(ArrayList::new)), " ");
			
			// Regular
//			for(Integer num : warmupList)	if(i%2 == 0)	output.add(num+1);
			StringJoiner join = new StringJoiner(" ");
			for(int j = 0; j < SIZE; j++)	if(warmupList.get(j)%2 == 0) join.add(Integer.toString(warmupList.get(j)+1)); 	//output.add(warmupList.get(j)+1);
		}
		
		System.out.println("Testing...");
		for(int i = 0; i < ITER; i++){
//			output = new ArrayList<>();
			startTime = System.currentTimeMillis();
		
			// Lamda
//			Joiner.join(testList.stream().filter(num -> num%2 == 0).map(num -> num+1).collect(Collectors.toCollection(ArrayList::new)), " ");
			
			// Regular
//			for(Integer num : testList)	if(i%2 == 0)	output.add(num+1);
			StringJoiner join = new StringJoiner(" ");
			for(int j = 0; j < SIZE; j++)	if(testList.get(j)%2 == 0) join.add(Integer.toString(testList.get(j)+1)); 	//output.add(warmupList.get(j)+1);
			
			timeLog.add(System.currentTimeMillis() - startTime);
		}
		
		long sum = 0; 	for(long l : timeLog)	sum += l;
		System.out.println("Time spent: " + sum/ITER);
	}
	
	@Test
//	@Ignore
	public void testMap(){
		timeLog = new ArrayList<>();
		warmupList = new ArrayList<>();
		testList = new ArrayList<>();
		init(warmupList, testList);
		
		System.out.println("Warming up...");
		
		for(int i = 0; i < ITER; i++){
			output = new ArrayList<>();

			// Lamda
			warmupList.stream().map(num -> num+1).collect(Collectors.toCollection(ArrayList::new));
			
			// Regular
//			for(Integer num : warmupList)	output.add(num+1);
//			for(int j = 0; j < SIZE; j++)	output.add(warmupList.get(j)+1);
		}
		
		System.out.println("Testing...");
		for(int i = 0; i < ITER; i++){
			output = new ArrayList<>();
			startTime = System.currentTimeMillis();
		
			// Lamda
			testList.stream().map(num -> num+1).collect(Collectors.toCollection(ArrayList::new));
			
			// Regular
//			for(Integer num : testList)	output.add(num+1);
//			for(int j = 0; j < SIZE; j++)	output.add(testList.get(j)+1);
			
			timeLog.add(System.currentTimeMillis() - startTime);
		}
		
		long sum = 0; 	for(long l : timeLog)	sum += l;
		System.out.println("Time spent: " + sum/ITER);
	}
	
	@Test
	@Ignore
	public void testCollectors(){
		testList = new ArrayList<>();
		System.out.println(testList.stream().map(num -> num).collect(Collectors.toMap(k -> k, v -> v)) instanceof HashMap);
		System.out.println(testList.stream().map(num -> num).collect(Collectors.toSet()) instanceof HashSet);
		System.out.println(testList.stream().map(num -> num).collect(Collectors.toList()) instanceof ArrayList);		
	}
}
