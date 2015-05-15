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
package edu.emory.clir.clearnlp.coreference.mention.pronoun.detector;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import edu.emory.clir.clearnlp.constituent.CTLibEn;
import edu.emory.clir.clearnlp.coreference.dictionary.PathMention;
import edu.emory.clir.clearnlp.coreference.mention.pronoun.Pronoun;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.Splitter;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 11, 2015
 */
public class EnglishPronounDetector extends AbstractPronounDetector {
	private static final long serialVersionUID = -8181557080414575892L;
		
	public EnglishPronounDetector() { 
		super(TLanguage.ENGLISH); 
	}
	
	@Override
	protected Map<String, Pronoun> initDictionary() {
		Map<String, Pronoun> map = new HashMap<>();
		
		initCommonPronounDictionary(map);
		initWildcardPronounDictionary(map);
		
		return map;
	}

	@Override
	public boolean isPronoun(DEPTree tree, DEPNode node) {
		// Common pronoun
		return !node.isLabel(DEPTagEn.DEP_COMPOUND) && (m_pronouns.containsKey(node.getLemma()) || m_pronouns.containsKey(node.getWordForm()) || (node.isPOSTag(CTLibEn.POS_PRP) || node.isPOSTag(CTLibEn.POS_PRPS)));
	}

	@Override
	public Pronoun getPronoun(DEPTree tree, DEPNode node) {
		// Common pronoun
		Pronoun pronoun;
		
		if( (pronoun = getPronoun(node.getLemma())) != null)	return pronoun;
		if( (pronoun = getPronoun(node.getWordForm())) != null)	return pronoun;
		
		return new Pronoun(node.getWordForm());
	}
	
	private void initCommonPronounDictionary(Map<String, Pronoun> map){
		try {
			InputStream dict = new FileInputStream(PathMention.ENG_PRONOUN);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(dict));
			String line; String[] attr;
			while((line = reader.readLine()) != null){
				attr = Splitter.splitTabs(line);
				
				// Might change depends on the dictionary fields
				map.put(attr[0], new Pronoun(attr[0], attr[1], attr[2]));
			}
			
			reader.close();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	private void initWildcardPronounDictionary(Map<String, Pronoun> map){
		try {
			InputStream dict = new FileInputStream(PathMention.ENG_WILDCARD_PRONOUN);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(dict));
			String line; String[] attr;
			while((line = reader.readLine()) != null){
				attr = Splitter.splitTabs(line);
				
				// Might change depends on the dictionary fields
				map.put(attr[0], new Pronoun(attr[0], "PRONOUN_WILDCARD", attr[1], attr[2]));
			}
			
			reader.close();
		} catch (IOException e) { e.printStackTrace(); }
	}
}
