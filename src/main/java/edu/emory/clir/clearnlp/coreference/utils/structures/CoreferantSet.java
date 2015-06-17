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
import java.util.Comparator;
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
public class CoreferantSet implements Serializable, Iterable<DoubleIntPair> {
	private static final long serialVersionUID = 3976817040635652271L;
	private int[] s_root;
	private double[] s_confidence;
	private List<List<Integer>> l_clusters;
	
	private Comparator<List<Integer>> clusterComparator = new Comparator<List<Integer>>() {
		@Override
		public int compare(List<Integer> o1, List<Integer> o2) {
			return o1.get(0) - o2.get(0);
		}
	};
	
	public CoreferantSet(int size){
		init(size, false, true);
	}
	
	public CoreferantSet(int size, boolean confidence, boolean disjointClusters){
		init(size, confidence, disjointClusters);
	}
	
	private void init(int size, boolean confidence, boolean disjointClusters){
		s_root = new int[size];	
		Arrays.fill(s_root, -1);
		
		if(confidence){
			s_confidence = new double[size];
			Arrays.fill(s_confidence, 1d);
		}
		
		if(!disjointClusters)	l_clusters = new ArrayList<>();
	}
	
	public int size(){
		return s_root.length;
	}
	public void setHead(int id){
		s_root[id] = -1;
		if(s_confidence != null) s_confidence[id] = 1d;
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
	
	public void addClusters(List<List<Integer>> clusters, boolean toDisjointClusters){
		if(l_clusters != null)
			for(List<Integer> cluster : clusters)	addCluster(cluster);
		if(toDisjointClusters)	initDisjointClusters();
		else					initSingletonClusters();
	}
	
	public int addCluster(List<Integer> cluster){
		if(l_clusters != null){
			Collections.sort(cluster);
			
			for(int i = cluster.size()-1; i > 0; i--)
				union(cluster.get(i-1), cluster.get(i));
			
			l_clusters.add(cluster);
			return l_clusters.size() -1;
		}
		return -1;
	}
	
	public void initSingletonClusters(){
		List<Integer> list; 
		Set<Integer> existed = new HashSet<>();
		
		for(List<Integer> cluster : l_clusters)
			existed.addAll(cluster);
		
		for(int i = 0; i < size(); i++){
			if(!existed.contains(i)){
				list = new ArrayList<>();
				list.add(i);
				l_clusters.add(list);
			}
		}
		l_clusters.sort(clusterComparator);
	}
	
	public void initDisjointClusters(){
		l_clusters = null;
		l_clusters = getClusterLists(true);
	}
	
	public int findHead(int id){
		int head_id;
		for(head_id = id; s_root[head_id] >= 0; head_id = s_root[head_id]);
		return head_id;
	}
	
	public int findClusterHead(int id){
		if(l_clusters != null){
			int index;
			for(List<Integer> cluster : l_clusters){
				index = Collections.binarySearch(cluster, id);
				if(index > 0)	return cluster.get(0);
			}
		}
		return findHead(id);
	}
	
	public int findClosest(int id){
		return (s_root[id] >= 0)? s_root[id] : id; 
	}
	
	public int findByRank(int id, int rank){
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
	
	public List<Integer> getCluster(int id){
		if(l_clusters != null && id < l_clusters.size())
			return l_clusters.get(id);
		return null;
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
		List<Set<Integer>> list = (l_clusters == null)? new ArrayList<>() : l_clusters.stream().map(l -> new HashSet<>(l)).collect(Collectors.toList());
		
		if(list.isEmpty()){
			int i, headId;
			Map<Integer, Set<Integer>> clusters = new HashMap<>();

			for(i = s_root.length-1; i >= 0; i--){
				headId = findHead(i);
				clusters.computeIfAbsent(headId, HashSet::new).add(i);
			}
			
			list = new ArrayList<>(clusters.values());
		}
		
		if(includeSingleton)	
			return list.stream().filter(set -> !set.isEmpty()).collect(Collectors.toList());
		return list.stream().filter(set -> set.size() > 1).collect(Collectors.toList());
	}
	
	public List<List<Integer>> getClusterLists(boolean includeSingleton){
		List<List<Integer>> list = (l_clusters == null)? new ArrayList<>() : l_clusters;
		
		if(list.isEmpty()){
			int i, headId;
			Map<Integer, List<Integer>> clusters = new HashMap<>();
			
			for(i = s_root.length-1; i >= 0; i--){
				headId = findHead(i);
				clusters.computeIfAbsent(headId, ArrayList::new).add(i);
			}
			
			list = new ArrayList<>(clusters.values());
		}
				
		for(List<Integer> cluster : list)	Collections.sort(cluster);
		list.sort(clusterComparator);
		
		if(includeSingleton)	
			return list.stream().filter(set -> !set.isEmpty()).collect(Collectors.toList());
		return list.stream().filter(set -> set.size() > 1).collect(Collectors.toList());
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
