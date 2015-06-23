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
package edu.emory.clir.clearnlp.coreference.dictionary.wordnet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 23, 2015
 */
public class WordNetEntry {
	private int i_id;
	private String s_word;
	private String s_pos;
	private Map<String, Set<Integer>> m_relations;
	
	public WordNetEntry(int id, String word){
		init(id, word, null);
	}
	
	public WordNetEntry(int id, String word, String pos){
		init(id, word, pos);
	}
	
	public WordNetEntry(String s_id, String word, String pos){
		try {
			int id = Integer.parseInt(s_id);
			init(id, word, pos);
		} catch (Exception e) {
			e.printStackTrace();
			init(-1, word, pos);
		}
	}
	
	private void init(int id, String word, String pos){
		i_id = id;
		s_word = word;
		s_pos = pos;
		m_relations = new HashMap<>();
	}
	
	public int getId(){
		return i_id;
	}
	
	public String getWord(){
		return s_word;
	}
	
	public String getPOSTag(){
		return s_pos;
	}
	
	public Set<Integer> getRelationSet(String relation){
		return m_relations.getOrDefault(relation, new HashSet<>());
	}
	
	public void addRelations(String relation, int relId){
		m_relations.computeIfAbsent(relation, value -> new HashSet<>()).add(relId);
	}
	
	public boolean hasRelations(String relation, int relId){
		return getRelationSet(relation).contains(relId);
	}
}
