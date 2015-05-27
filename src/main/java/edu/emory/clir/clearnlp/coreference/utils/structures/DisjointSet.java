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
package edu.emory.clir.clearnlp.coreference.utils.structures;

import java.util.Arrays;
import java.util.Iterator;

import edu.emory.clir.clearnlp.collection.pair.DoubleIntPair;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 27, 2015
 */
public class DisjointSet implements Iterable<DoubleIntPair> {
	private int[] s_root;
	private double[] s_confidence;
	
	public DisjointSet(int size, boolean confidence){
		s_root = new int[size];	
		Arrays.fill(s_root, -1);
		if(confidence){
			s_confidence = new double[size];
			Arrays.fill(s_confidence, 1d);
		}
	}
	
	public int union(int id, int idx){
		return union(id, idx, 1d);
	}
	
	public int union(int id, int idx, double score){
		if(findHead(id) == idx)		s_root[id] = -1; 
		if(s_confidence != null) 	s_confidence[idx] = score;
		
		s_root[idx] = id;
		return findHead(id);
	}
	
	public int findHead(int id){
		return (s_root[id] < 0)? id : findHead(s_root[id]); 
	}
	
	public int findClosest(int id){
		return s_root[id]; 
	}
	
	public int find(int id, int rank){
		return (s_root[id] < 0 || rank == 0)? id : find(s_root[id], --rank); 
	}
	
	public boolean isSameSet(int id, int idx){
		return findHead(id) == findHead(idx);
	}
	
	public double getConfidence(int id){
		return s_confidence[id];
	}
	
	public double getHeadConfidence(int id){
		return (id < 0)? s_confidence[id] : s_confidence[id] * getHeadConfidence(s_root[id]);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("Clusters:\t"+Arrays.toString(s_root)+"\n");
		sb.append("Confidence:\t"+Arrays.toString(s_confidence)+"\n");
		
		return sb.toString();
	}

	@Override
	public Iterator<DoubleIntPair> iterator() {
		Iterator<DoubleIntPair> it = new Iterator<DoubleIntPair>(){
			private int index = 0;
			
			@Override
			public boolean hasNext() {
				return index < s_root.length;
			}

			@Override
			public DoubleIntPair next() {
				return new DoubleIntPair(s_confidence[index], s_root[index++]);
			}
			
			@Override
			public void remove() {}
		};
		return it;
	}
}
