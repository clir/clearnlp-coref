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

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 23, 2015
 */
public class WordNetDatabase implements WordNetPointerSymbols{
	private Map<Integer, WordNetEntry> m_index2Entry;
	private Map<String, WordNetEntry> m_string2Entry;
	
	public WordNetDatabase(InputStream in){
		m_index2Entry = new HashMap<>();
		m_string2Entry = new HashMap<>();
		initDB(in);
	}
	
	// Under construction
	private void initDB(InputStream in){
		try {
			String line;
			BufferedReader reader = IOUtils.createBufferedReader(in);
			
			while( (line = reader.readLine()) != null){
				
			}
			
			reader.close();
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	private void addEntry(WordNetEntry entry){
		m_index2Entry.put(entry.getId(), entry);
		m_string2Entry.put(entry.getWord(), entry);
	}
	
	public WordNetEntry getEntry(int id){
		return m_index2Entry.get(id);
	}
	
	public WordNetEntry getEntry(String word){
		return m_string2Entry.get(word);
	}
	
	public boolean hasRelations(String word1, String word2, String relation){
		WordNetEntry entry1 = getEntry(word1), entry2 = getEntry(word2);
		return (entry1 != null && entry2 != null)? entry1.hasRelations(relation, entry2.getId()) : false;
	}
}
