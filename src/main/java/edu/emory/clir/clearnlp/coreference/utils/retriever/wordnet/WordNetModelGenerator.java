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
package edu.emory.clir.clearnlp.coreference.utils.retriever.wordnet;

import java.io.BufferedReader;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Splitter;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 25, 2015
 */
public class WordNetModelGenerator {
	public static void main(String[] args){
		Set<String> dict = new HashSet<>();
		
		try {
			int skipLineCount = 29;
			String line; String[] tokens;
			BufferedReader reader = IOUtils.createBufferedReader("src/main/resources/edu/emory/clir/clearnlp/dictionary/wordnet/WordNet-3.0/dict/index.noun");
			while( (line = reader.readLine()) != null){
				if(skipLineCount-- > 0) continue;
				tokens = Splitter.splitSpace(line);
				dict.add(tokens[0]);
			}
			reader.close();
		} catch (Exception e) { e.printStackTrace(); }
		
		Map<String, Set<String>> map;
		Map<Integer, Map<String, Set<String>>> obj = new HashMap<>();
		WordNetNounEntryRetriever db = new WordNetNounEntryRetriever();
		
		// Synonyms
		map = new HashMap<>();
		for(String word : dict)
			map.put(word, db.getSynonyms(word));
		obj.put(0, map);
		
		// Antonyms
		map = new HashMap<>();
		for(String word : dict)
			map.put(word, db.getAntonyms(word));
		obj.put(1, map);
				
		// Hypernyms
		map = new HashMap<>();
		for(String word : dict)
			map.put(word, db.getHypernyms(word));
		obj.put(2, map);
		
		try {
			ObjectOutputStream out = IOUtils.createObjectXZBufferedOutputStream("/Users/HenryChen/Desktop/WordNetFeatures.xz");
			out.writeObject(obj);
			out.close();
		} catch (Exception e) { e.printStackTrace(); }
	}
}
