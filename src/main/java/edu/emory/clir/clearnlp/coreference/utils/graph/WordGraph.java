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
package edu.emory.clir.clearnlp.coreference.utils.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 18, 2015
 */
public class WordGraph {
	private int idCount;
	private Map<Integer, WordNode> m_ids;
	private Map<String, WordNode> m_nodes;
	private List<Set<WordEdge>> l_edges; 
	private boolean undirected;
	
	public WordGraph(boolean u){
		idCount = 0;
		m_ids = new HashMap<>();
		m_nodes = new HashMap<>();
		l_edges = new ArrayList<>();
		undirected = u;
	}
	
	/* Graph helper functions */
	
	public int getSize(){
		return m_nodes.size();
	}
	
	public Map<String, WordNode> getWordMap(){
		return m_nodes;
	}
	
	public Set<WordEdge> getEdges(String word){
		return (hasWord(word))? getEdges(getNode(word).getID()) : null;
	}
	
	public Set<WordEdge> getEdges(WordNode node){
		return (m_nodes.values().contains(node))? getEdges(node.getID()) : null;
	}
	
	public Set<WordEdge> getEdges(int id){
		return l_edges.get(id);
	}
	
	public List<Set<WordEdge>> getAllEdges(){
		return l_edges;
	}
	
	public WordNode getNode(int id){
		return m_ids.get(id);
	}
	
	public WordNode getNode(String word){
		return m_nodes.get(word);
	}
	
	public WordNode addNode(String word){
		if(!hasWord(word)){
			WordNode node = new WordNode(idCount, word);
			l_edges.add(new HashSet<WordEdge>());
			m_ids.put(idCount++, node);
			m_nodes.put(word, node);
			return node;
		}

		return getNode(word);
	}
	
	public void connenctNodes(WordNode n1, WordNode n2){
		connenctNodes(n1, n2, 1);
	}
	
	public void connenctNodes(WordNode n1, WordNode n2, double weight){
		if(!hasConnection(n1.getWord(), n2.getWord()))					l_edges.get(n1.getID()).add(new WordEdge(n1, n2, weight));
		if(undirected && !hasConnection(n2.getWord(), n1.getWord()))	l_edges.get(n2.getID()).add(new WordEdge(n2, n1, weight));
	}
	
	public boolean hasWord(String word){
		return m_nodes.containsKey(word);
	}
	
	public boolean hasConnection(String s1, String s2){
		if(hasWord(s1) && hasWord(s2)){
			for(WordEdge e : getEdges(s1)){
				if(e.getTarget().getWord().equals(s2)) return true;
			}
		}
		return false;
	}
	
	/* Graph functions */
	public WordPath getShortestPath(String s1, String s2){
		if(!hasWord(s1) || !hasWord(s2))	return null;
		
		WordNode start = getNode(s1), end = getNode(s2);
		PriorityQueue<WordEdge> queue = new PriorityQueue<>();
		int[] previous = new int[getSize()];			Arrays.fill(previous, -1);
		double[] distances = new double[getSize()];		Arrays.fill(distances, Double.MAX_VALUE);	distances[start.getID()] = 0;
		Set<Integer> visited = new HashSet<>();
		
		queue.add(new WordEdge(null, start, 0));
		
		int currentID, nextID;
		double dist;
		WordEdge edge;
		while(!queue.isEmpty()){
			edge = queue.poll();
			
			currentID = edge.getTarget().getID();
			visited.add(currentID);
			
			for(WordEdge e : getEdges(edge.getTarget())){
				nextID = e.getTarget().getID();
				
				if(!visited.contains(nextID)){
					dist = distances[currentID] + e.getWeight();
					
					if(dist < distances[nextID]){
						distances[nextID] = dist;
						previous[nextID] = currentID;
						queue.add(e);
					}
				}
			}
		}
		return (distances[end.getID()] < Double.MAX_VALUE)? getPath(previous, distances, start, end) : null;
	}
	
	public double getDistance(String s1, String s2){
		WordPath path = getShortestPath(s1, s2);
		return (path == null)? Double.MAX_VALUE : path.getWeight();
	}
	
	private WordPath getPath(int[] previous, double[] distances, WordNode start, WordNode end){
		WordPath path = new WordPath();
		
		int id = end.getID();
		path.setWeight(distances[id]);
		
		while( id >= 0 ){
			path.addNode(getNode(id), 0);
			id = previous[id];
		}
		
		Collections.reverse(path.getPath());
		return path;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		for(WordNode node : m_nodes.values()){
			sb.append("===== " + node + " =====\n");
			for(WordEdge e : getEdges(node.getID()))
				sb.append(e+"\n");
		}
		
		return sb.toString();
	}
}
