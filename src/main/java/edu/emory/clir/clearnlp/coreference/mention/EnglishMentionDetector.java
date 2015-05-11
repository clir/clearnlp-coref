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
package edu.emory.clir.clearnlp.coreference.mention;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import edu.emory.clir.clearnlp.constituent.CTLibEn;
import edu.emory.clir.clearnlp.coreference.mention.pronoun.Pronoun;
import edu.emory.clir.clearnlp.coreference.mention.pronoun.detector.EnglishPronounDetector;
import edu.emory.clir.clearnlp.dependency.DEPLibEn;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishMentionDetector extends AbstractMentionDetector{

	EnglishPronounDetector pronounDictionary;
	Set<String> s_mentionLabels;
	
	public EnglishMentionDetector() throws IOException{
		pronounDictionary = new EnglishPronounDetector();
		s_mentionLabels = initMentionLabels();
	}
	
//	====================================== LEXICA ======================================

	private Set<String> initMentionLabels(){
		Set<String> set = new HashSet<>();
		
		set.add(DEPLibEn.DEP_NSUBJ);
		set.add(DEPLibEn.DEP_NSUBJPASS);
		set.add(DEPLibEn.DEP_AGENT);
		set.add(DEPLibEn.DEP_DOBJ);
		set.add(DEPLibEn.DEP_IOBJ);
		set.add(DEPLibEn.DEP_POBJ);
		
		return set;
	}
	
//	====================================== MENTION TYPE ======================================
	
	@Override
	public Mention getMention(DEPTree tree, DEPNode node){
		Mention mention;
		
		if ((mention = getPronounMention(tree, node)) != null)	return mention;
		if ((mention = getPersonMention (tree, node)) != null)	return mention;
		
		return null;
	}
	
	public Mention getPronounMention(DEPTree tree, DEPNode node){
		
		if (node.isPOSTag(CTLibEn.POS_PRP) || node.isPOSTag(CTLibEn.POS_PRPS)){
			Pronoun pronoun;
			Mention mention = new Mention(tree, node);
			
			if( (pronoun = pronounDictionary.getPronoun(node.getLemma())) != null) {
				mention.setEntityType(pronoun.e_type);
				mention.setNumberType(pronoun.n_type);
			}

			return mention;
		}
		
		return null;
	}

	public Mention getPersonMention(DEPTree tree, DEPNode node){

		return null;
	}
}
