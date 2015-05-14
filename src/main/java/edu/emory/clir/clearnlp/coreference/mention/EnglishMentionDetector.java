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

import edu.emory.clir.clearnlp.coreference.mention.common.CommonNoun;
import edu.emory.clir.clearnlp.coreference.mention.common.detector.EnglishCommonNounDetector;
import edu.emory.clir.clearnlp.coreference.mention.pronoun.Pronoun;
import edu.emory.clir.clearnlp.coreference.mention.pronoun.detector.EnglishPronounDetector;
import edu.emory.clir.clearnlp.coreference.mention.proper.ProperNoun;
import edu.emory.clir.clearnlp.coreference.mention.proper.detector.EnglishProperNounDetector;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishMentionDetector extends AbstractMentionDetector{

	private EnglishPronounDetector pronounDetector;
	private EnglishCommonNounDetector commonNounDetector;
	private EnglishProperNounDetector properNounDetector;
	
	public EnglishMentionDetector(){
		pronounDetector = new EnglishPronounDetector();
		commonNounDetector = new EnglishCommonNounDetector();
		properNounDetector = new EnglishProperNounDetector(); 
	}

//	====================================== MENTION TYPE ======================================
	
	@Override
	public Mention getMention(DEPTree tree, DEPNode node){
		Mention mention;
		
		if ((mention = getPronounMention(tree, node)) != null )	return processMention(mention);
		if ((mention = getCommonMention(tree, node)) != null)	return processMention(mention);
		if ((mention = getPersonMention (tree, node)) != null)	return processMention(mention);
		
		return null;
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
		
		if (properNounDetector.isProperNoun(tree, node)){
			ProperNoun properNoun = properNounDetector.getProperNoun(tree, node);
			if(properNoun != null) return properNoun.toMention(tree, node);
		}
		
		return null;
	}
	
//	====================================== MENTION ATTR ======================================
	protected Mention processMention(Mention mention){
		return mention;
	}
}
