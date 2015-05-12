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

import java.util.HashSet;
import java.util.Set;

import edu.emory.clir.clearnlp.coreference.mention.common.CommonNoun;
import edu.emory.clir.clearnlp.coreference.mention.common.detector.EnglishCommonNounDetector;
import edu.emory.clir.clearnlp.coreference.mention.pronoun.Pronoun;
import edu.emory.clir.clearnlp.coreference.mention.pronoun.detector.EnglishPronounDetector;
import edu.emory.clir.clearnlp.dependency.DEPLibEn;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishMentionDetector extends AbstractMentionDetector{

	EnglishPronounDetector pronounDetector;
	EnglishCommonNounDetector commonNounDetector;
	Set<String> s_mentionLabels;
	
	public EnglishMentionDetector(){
		pronounDetector = new EnglishPronounDetector();
		commonNounDetector = new EnglishCommonNounDetector();
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
		
		if ((mention = getPronounMention(tree, node)) != null);
		else if ((mention = getCommonMention(tree, node)) != null);
		else if ((mention = getPersonMention (tree, node)) != null);
		
		return (mention == null)? null : processMention(mention);
	}
	
	protected Mention getPronounMention(DEPTree tree, DEPNode node){
		
		if (pronounDetector.isPronoun(tree, node)){
			Pronoun pronoun = pronounDetector.getPronoun(tree, node);
			if(pronoun != null) return pronoun.toMention(tree, node);
		}
		
		return null;
	}
	
	protected Mention getCommonMention(DEPTree tree, DEPNode node){
		
		if (commonNounDetector.isCommonNoun(tree, node)){
			CommonNoun commonNoun = commonNounDetector.getCommonNoun(tree, node);
			if(commonNoun != null) return commonNoun.toMention(tree, node);
		}
		
		return null;
	}

	protected Mention getPersonMention(DEPTree tree, DEPNode node){

		return null;
	}
	
//	====================================== MENTION ATTR ======================================
	protected Mention processMention(Mention mention){
		return mention;
	}
}
