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

import java.util.HashMap;
import java.util.Map;

import edu.emory.clir.clearnlp.collection.pair.ObjectDoublePair;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 18, 2015
 */
public class WordNode {
	private String word;
	private Map<String, ObjectDoublePair<WordNode>> m_edges;

	public WordNode(String s){
		word = s;
		m_edges = new HashMap<>();
	}
	
	public WordNode(WordNode node){
		word = node.getWord();
		m_edges = new HashMap<>(node.getEdges());
	}
	
	public String getWord(){
		return word;
	}
	
	public Map<String, ObjectDoublePair<WordNode>> getEdges(){
		return m_edges;
	}
	
	public void setWord(String s){
		word = s;
	}
	
	public void setEdgeWeight(String word, double weight){
		if(hasConnection(word))	m_edges.get(word).d = weight; 
	}
	
	public void setEdgeWeight(WordNode node, double weight){
		if(hasConnection(node))	m_edges.get(node.getWord()).d = weight; 
	}
	
	public void addEdge(WordNode node){
		m_edges.putIfAbsent(word, new ObjectDoublePair<WordNode>(node, 1d));
	}
	
	public void addEdge(WordNode node, double weight){
		m_edges.putIfAbsent(word, new ObjectDoublePair<WordNode>(node, weight));
	}
	
	public boolean hasConnection(String word){
		return m_edges.containsKey(word);
	}
	
	public boolean hasConnection(WordNode node){
		return m_edges.containsKey(node.getWord());
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		for(ObjectDoublePair<WordNode> pair : m_edges.values()){
			sb.append(word + " -> " + pair.o.getWord() + " : " + pair.d + "\n");
		}
		
		return sb.toString();
	}
}
