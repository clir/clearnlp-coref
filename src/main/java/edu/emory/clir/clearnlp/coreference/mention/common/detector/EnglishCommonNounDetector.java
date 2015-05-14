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
package edu.emory.clir.clearnlp.coreference.mention.common.detector;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import edu.emory.clir.clearnlp.constituent.CTLibEn;
import edu.emory.clir.clearnlp.coreference.dictionary.PathMention;
import edu.emory.clir.clearnlp.coreference.mention.common.CommonNoun;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.Splitter;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 12, 2015
 */
public class EnglishCommonNounDetector extends AbstractCommonNounDetector{
	private static final long serialVersionUID = 3474508950522490238L;
	
	public EnglishCommonNounDetector() { super(TLanguage.ENGLISH); }

	@Override
	protected Map<String, CommonNoun> initDictionary() {
		Map<String, CommonNoun> map = new HashMap<>();
		
		try {
			InputStream dict = new FileInputStream(PathMention.ENG_COMMON_NOUN);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(dict));
			String line; String[] attr;
			while((line = reader.readLine()) != null){
				attr = Splitter.splitTabs(line);
				
				// Might change depends on the dictionary fields
				map.put(attr[0], new CommonNoun(attr[0], attr[1], attr[2]));
			}
			
			reader.close();
		} catch (IOException e) { e.printStackTrace(); }
		
		return map;
	}
	
	@Override
	public boolean isCommonNoun(DEPTree tree, DEPNode node) {
		return !node.isLabel(DEPTagEn.DEP_COMPOUND) && (node.isPOSTag(CTLibEn.POS_NN) || node.isPOSTag(CTLibEn.POS_NNS)) ; 
	}

	@Override
	public CommonNoun getCommonNoun(DEPTree tree, DEPNode node) {
		CommonNoun commonNoun;
		String wordForm = node.getLemma();
		
		
		
		if(m_common_nouns.containsKey(wordForm))
			commonNoun = m_common_nouns.get(wordForm);
		else
			commonNoun = new CommonNoun(node.getLemma());
		
		// Setting common noun attributes 
		
		return commonNoun;
	}


}
