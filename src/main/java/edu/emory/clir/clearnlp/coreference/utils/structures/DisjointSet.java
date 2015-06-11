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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.collection.pair.DoubleIntPair;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 27, 2015
 */
public class DisjointSet implements Serializable, Iterable<DoubleIntPair> {
	private static final long serialVersionUID = 3976817040635652271L;
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
	
	public int union(int prev, int curr){
		return union(prev, curr, 1d);
	}
	
	public int union(int prev, int curr, double score){
		if(findHead(prev) == curr)		s_root[prev] = -1; 
		if(s_confidence != null) 	s_confidence[curr] = score;
		
		s_root[curr] = prev;
		return findHead(prev);
	}
	
	public int findHead(int id){
		int head_id;
		for(head_id = id; s_root[head_id] >= 0; head_id = s_root[head_id]);
		return head_id;
	}
	
	public int findClosest(int id){
		return (s_root[id] >= 0)? s_root[id] : id; 
	}
	
	public int find(int id, int rank){
		int idx;
		for(idx = id; s_root[idx] >= 0 && rank-- > 0; idx = s_root[idx]);
		return idx; 
	}
	
	public boolean isSingleton(int id){
		return s_root[id] < 0;
	}
	
	public boolean isSameSet(int id, int idx){
		return findHead(id) == findHead(idx);
	}
	
	public double getConfidence(int id){
		return s_confidence[id];
	}
	
	public double getHeadConfidence(int id){
		double confidence = s_confidence[id];
		for(int curr_id = s_root[id]; curr_id >= 0; confidence*=s_confidence[curr_id], curr_id = s_root[curr_id]);
		return confidence;
	}
	
	public int getDistanceFromHead(int id){
		int distance = 0;
		for(int curr_id = s_root[id]; curr_id >= 0; distance++, curr_id = s_root[curr_id]);
		return distance;
	}
	
	public List<Set<Integer>> getClusterSets(boolean includeSingleton){
		Map<Integer, Set<Integer>> clusters = new HashMap<>();
		
		int i, headId;
		for(i = s_root.length-1; i >= 0; i--){
			headId = findHead(i);
			clusters.computeIfAbsent(headId, HashSet::new).add(i);
		}
		
		if(includeSingleton)	
			return new ArrayList<>(clusters.values());
		return clusters.values().stream().filter(set -> set.size() > 1).collect(Collectors.toList());
	}
	
	public List<List<Integer>> getClusterLists(boolean includeSingleton, boolean sort){
		Map<Integer, List<Integer>> clusters = new HashMap<>();
		
		int i, headId;
		for(i = s_root.length-1; i >= 0; i--){
			headId = findHead(i);
			clusters.computeIfAbsent(headId, ArrayList::new).add(i);
		}
		
		if(sort)
			for(List<Integer> cluster : clusters.values())
				Collections.sort(cluster);
		
		if(includeSingleton)	
			return new ArrayList<>(clusters.values());
		return clusters.values().stream().filter(set -> set.size() > 1).collect(Collectors.toList());
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
