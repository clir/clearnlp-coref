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

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 18, 2015
 */
public class WordGraph {
	private Map<String, WordNode> m_nodes;
	
	public WordGraph(){
		m_nodes = new HashMap<>();
	}
	
	public int getSize(){
		return m_nodes.size();
	}
	
	public Map<String, WordNode> getWordMap(){
		return m_nodes;
	}
	
	public WordNode getNode(String word){
		return m_nodes.get(word);
	}
	
	public WordNode addNode(String word){
		return m_nodes.computeIfAbsent(word, value -> new WordNode(word));
	}
	
	public void connenctNodes(WordNode n1, WordNode n2){
		connenctNodes(n1, n2, 1);
	}
	
	public void connenctNodes(WordNode n1, WordNode n2, double weight){
		n1.addEdge(n2, weight);
		n2.addEdge(n1, weight);
	}
	
	public boolean hasWord(String word){
		return m_nodes.containsKey(word);
	}
	
	public boolean hasConnection(WordNode n1, WordNode n2){
		return n1.hasConnection(n2);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		for(WordNode node : m_nodes.values())	sb.append(node.getWord()+"\n");
		
		return sb.toString();
	}
}
