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

import java.util.List;

import edu.emory.clir.clearnlp.collection.pair.IntIntPair;
import edu.emory.clir.clearnlp.coreference.config.MentionConfiguration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.mention.EnglishMention;
import edu.emory.clir.clearnlp.coreference.mention.common.CommonNoun;
import edu.emory.clir.clearnlp.coreference.mention.common.detector.EnglishCommonNounDetector;
import edu.emory.clir.clearnlp.coreference.mention.pronoun.Pronoun;
import edu.emory.clir.clearnlp.coreference.mention.pronoun.detector.EnglishPronounDetector;
import edu.emory.clir.clearnlp.coreference.mention.proper.ProperNoun;
import edu.emory.clir.clearnlp.coreference.mention.proper.detector.EnglishProperNounDetector;
import edu.emory.clir.clearnlp.coreference.type.AttributeType;
import edu.emory.clir.clearnlp.coreference.utils.util.CoreferenceDSUtils;
import edu.emory.clir.clearnlp.dependency.DEPLibEn;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishMentionDetector extends AbstractMentionDetector{

	private EnglishPronounDetector pronounDetector;
	private EnglishCommonNounDetector commonNounDetector;
	private EnglishProperNounDetector properNounDetector;
	
	public EnglishMentionDetector(MentionConfiguration config){
		super(config);
		if(m_config.b_pronoun)	pronounDetector = new EnglishPronounDetector();
		if(m_config.b_common)	commonNounDetector = new EnglishCommonNounDetector();
		if(m_config.b_proper)	properNounDetector = new EnglishProperNounDetector(); 
	}

//	====================================== MENTION TYPE ======================================
	
	@Override
	public AbstractMention getMention(int treeId, DEPTree tree, DEPNode node){
		EnglishMention mention;
		
		if (m_config.b_pronoun && (mention = getPronounMention(treeId, tree, node)) != null )	return mention;
		if (m_config.b_common && (mention = getCommonMention(treeId, tree, node)) != null)	return mention;
		if (m_config.b_proper && (mention = getPersonMention (treeId, tree, node)) != null)	return mention;
		
		return null;
	}
	
	protected EnglishMention getPronounMention(int treeId, DEPTree tree, DEPNode node){
		
		if (pronounDetector.isPronoun(tree, node)){
			Pronoun pronoun = pronounDetector.getPronoun(tree, node);
			if(pronoun != null) return pronoun.toMention(treeId, tree, node);
		}
		
		return null;
	}
	
	protected EnglishMention getCommonMention(int treeId, DEPTree tree, DEPNode node){
		
		if (commonNounDetector.isCommonNoun(tree, node)){
			CommonNoun commonNoun = commonNounDetector.getCommonNoun(tree, node);
			if(commonNoun != null) return commonNoun.toMention(treeId, tree, node);
		}
		
		return null;
	}

	protected EnglishMention getPersonMention(int treeId, DEPTree tree, DEPNode node){
		
		if (properNounDetector.isProperNoun(tree, node)){
			ProperNoun properNoun = properNounDetector.getProperNoun(tree, node);
			if(properNoun != null) return properNoun.toMention(treeId, tree, node);
		}
		
		return null;
	}
	
//	====================================== MENTION ATTR ======================================
	@Override
	protected void processMentions(List<DEPTree> trees, List<AbstractMention> mentions){
		
		List<IntIntPair[]> boundaries = CoreferenceDSUtils.getQuotaionIndices(trees);
		
		DEPNode curr;
		IntIntPair[] boundary;
		AbstractMention mention_prev, mention_curr;
		int i, j, n_pos, t_pos, m_size = mentions.size(), b_size = boundaries.size();
		
		for(i = 0; i < m_size; i++){
			mention_curr = mentions.get(i);
			mention_prev = (i > 0)? mentions.get(i-1) : null;
			
			/** Inside quotation detection **/
			t_pos = mention_curr.getTreeId();
			n_pos = mention_curr.getNode().getID();
			for(j = 0; j < b_size; j++){
				boundary = boundaries.get(j);
				if(CoreferenceDSUtils.isSequence(boundary[0].i1, t_pos, boundary[1].i1)){			// LQ_TreeID <= t_pos <= RQ_TreeID
					if(boundary[0].i1 == boundary[1].i1){											// LQ_TreeID == t_pos == RQ_TreeID
						if(CoreferenceDSUtils.isSequence(boundary[0].i2, n_pos, boundary[1].i2)){	// LQ_NodeId <= n_pos <= RQ_NodeId
							mention_curr.addAttribute(AttributeType.QUOTE, j);	break;
						}
					}
					else if((boundary[0].i1 < t_pos && t_pos < boundary[1].i1) ||		// LQ_TreeId < t_pos < RQ_TreeId
							(boundary[0].i1 == t_pos && boundary[0].i2 < n_pos) ||		// LQ_TreeID == t_pos && LQ_NodeId < n_pos
							(boundary[1].i1 == t_pos && boundary[1].i2 > n_pos) ){		// RQ_TreeID == t_pos && RQ_NodeId > n_pos
						mention_curr.addAttribute(AttributeType.QUOTE, j);	break;
					}
				}
			}
			
			/** Multiple Mention detection **/
			if(mention_prev != null){
				curr = mention_curr.getNode();
				if(curr.isLabel(DEPLibEn.DEP_CONJ) && curr.isDependentOf(mention_prev.getNode()))
					mention_curr.setConjunctionMention(mention_prev);
			}
		}
	}
}
