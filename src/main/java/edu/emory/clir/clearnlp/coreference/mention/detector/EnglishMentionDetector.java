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
package edu.emory.clir.clearnlp.coreference.mention.detector;

import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.coreference.mention.MultipleMention;
import edu.emory.clir.clearnlp.coreference.mention.SingleMention;
import edu.emory.clir.clearnlp.coreference.mention.common.CommonNoun;
import edu.emory.clir.clearnlp.coreference.mention.common.detector.EnglishCommonNounDetector;
import edu.emory.clir.clearnlp.coreference.mention.pronoun.Pronoun;
import edu.emory.clir.clearnlp.coreference.mention.pronoun.detector.EnglishPronounDetector;
import edu.emory.clir.clearnlp.coreference.mention.proper.ProperNoun;
import edu.emory.clir.clearnlp.coreference.mention.proper.detector.EnglishProperNounDetector;
import edu.emory.clir.clearnlp.coreference.type.MentionAttributeType;
import edu.emory.clir.clearnlp.coreference.utils.util.CoreferenceDSUtils;
import edu.emory.clir.clearnlp.coreference.utils.util.MentionUtil;
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
	public SingleMention getMention(DEPTree tree, DEPNode node){
		SingleMention mention;
		
		if ((mention = getPronounMention(tree, node)) != null )	return mention;
		if ((mention = getCommonMention(tree, node)) != null)	return mention;
		if ((mention = getPersonMention (tree, node)) != null)	return mention;
		
		return null;
	}
	
	protected SingleMention getPronounMention(DEPTree tree, DEPNode node){
		
		if (pronounDetector.isPronoun(tree, node)){
			Pronoun pronoun = pronounDetector.getPronoun(tree, node);
			if(pronoun != null) return pronoun.toMention(tree, node);
		}
		
		return null;
	}
	
	protected SingleMention getCommonMention(DEPTree tree, DEPNode node){
		
		if (commonNounDetector.isCommonNoun(tree, node)){
			CommonNoun commonNoun = commonNounDetector.getCommonNoun(tree, node);
			if(commonNoun != null) return commonNoun.toMention(tree, node);
		}
		
		return null;
	}

	protected SingleMention getPersonMention(DEPTree tree, DEPNode node){
		
		if (properNounDetector.isProperNoun(tree, node)){
			ProperNoun properNoun = properNounDetector.getProperNoun(tree, node);
			if(properNoun != null) return properNoun.toMention(tree, node);
		}
		
		return null;
	}
	
//	====================================== MENTION ATTR ======================================
	@Override
	protected void processMentions(DEPTree tree, List<SingleMention> mentions){
		
		List<SingleMention>[] groupedMentions;
		List<int[]> boundaries = CoreferenceDSUtils.getQuotaionIndices(tree);
		
		/** Quotation detection **/
		int pos;
		for(SingleMention mention : mentions){
			pos = mention.getNode().getID();
			
			/** Inside quotation detection **/
			for(int[] boundary : boundaries){
				if(boundary[0] > pos)	break;
				if(CoreferenceDSUtils.isSequence(boundary[0], pos, boundary[1])){
					mention.addAttribute(MentionAttributeType.QUOTE);
					break;
				}
			}
		}
		/** ************************* **/
		/** Multiple Mention detection **/
		groupedMentions = MentionUtil.groupMentions(mentions);
		if(groupedMentions != null && groupedMentions.length < mentions.size()){
			List<MultipleMention> multipleMentions = new ArrayList<>();
			for(List<SingleMention> group : groupedMentions)
				if(group.size() > 1 && MentionUtil.hasConjunctionRelations(group))
					multipleMentions.add(MentionUtil.mergeSingleMentions(group));
			
			if(!multipleMentions.isEmpty())	System.out.println(multipleMentions);
		}
		/** ************************* **/
	}
}
