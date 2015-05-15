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
package edu.emory.clir.clearnlp.coreference.mention.proper.detector;

import edu.emory.clir.clearnlp.collection.ngram.Unigram;
import edu.emory.clir.clearnlp.constituent.CTLibEn;
import edu.emory.clir.clearnlp.coreference.mention.proper.ProperNoun;
import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.coreference.type.NumberType;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.dictionary.PathNamedEntity;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 13, 2015
 */
public class EnglishProperNounDetector extends AbstractProperNounDetector{
	private static final long serialVersionUID = 6902314209581853770L;
	
	private Unigram<String> m_femaleNames;
	private Unigram<String> m_maleNames;
	
	public EnglishProperNounDetector() { 
		super(TLanguage.ENGLISH); 
		m_femaleNames = new Unigram<>();
		m_maleNames = new Unigram<>();
		
		addDictionary(IOUtils.getInputStreamsFromClasspath(PathNamedEntity.US_FEMALE_NAMES), m_femaleNames);
		addDictionary(IOUtils.getInputStreamsFromClasspath(PathNamedEntity.US_MALE_NAMES), m_maleNames);
	}

	@Override
	public boolean isProperNoun(DEPTree tree, DEPNode node) {
		return !node.isLabel(DEPTagEn.DEP_COMPOUND) && (node.isPOSTag(CTLibEn.POS_NNP) || node.isPOSTag(CTLibEn.POS_NNPS));
	}

	@Override
	public ProperNoun getProperNoun(DEPTree tree, DEPNode node) {
		ProperNoun properNoun = new ProperNoun(node.getLemma());
		String NERtag = node.getNamedEntityTag();
		NERtag = NERtag.substring(NERtag.indexOf('-')+1);
		
		switch(NERtag){
			case "PERSON":
				processPersonTag(properNoun);
				break;
			case "ORG":
				properNoun.e_type = EntityType.ORGANIZATION;
				properNoun.n_type = NumberType.UNKNOWN;
				break;
			case "LOC":
				properNoun.e_type = EntityType.LOCATION;
				properNoun.n_type = NumberType.UNKNOWN;
				break;
		}
		
		return properNoun;
	}
	
	private void processPersonTag(ProperNoun properNoun){
		properNoun.e_type = EntityType.PERSON_UNKNOWN;
		properNoun.n_type = NumberType.SINGULAR;
		
		if(m_maleNames.contains(properNoun.wordFrom))		properNoun.e_type = EntityType.PERSON_MALE;
		if(m_femaleNames.contains(properNoun.wordFrom))	
			if(m_maleNames.contains(properNoun.wordFrom))	properNoun.e_type = EntityType.PERSON_NEUTRAL;
			else											properNoun.e_type = EntityType.PERSON_FEMALE;
			
	}
}
