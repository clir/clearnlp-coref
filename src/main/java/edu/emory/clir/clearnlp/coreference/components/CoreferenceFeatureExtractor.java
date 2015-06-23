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
package edu.emory.clir.clearnlp.coreference.components;

import java.util.List;

import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.sieve.ExactStringMatch;
import edu.emory.clir.clearnlp.coreference.sieve.PreciseConstructMatch;
import edu.emory.clir.clearnlp.coreference.sieve.RelaxedStringMatch;
import edu.emory.clir.clearnlp.coreference.type.AttributeType;
import edu.emory.clir.clearnlp.coreference.type.FeatureType;
import edu.emory.clir.clearnlp.coreference.type.PronounType;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.constant.CharConst;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 9, 2015
 */
public class CoreferenceFeatureExtractor implements FeatureType{
	private ExactStringMatch exactString = new ExactStringMatch(true);
	private RelaxedStringMatch relaxedString = new RelaxedStringMatch(true);
	private PreciseConstructMatch preiseConstruct = new PreciseConstructMatch();
	
	private StringBuilder s_builder = new StringBuilder();

	public StringFeatureVector getFeatures(List<DEPTree> trees, CoreferantSet bootstrapLinks, AbstractMention mention1, DEPTree tree1, AbstractMention mention2, DEPTree tree2){
		StringFeatureVector vector = new StringFeatureVector();
		int multiMentionIndex = getMultiMentionIndex(mention1, mention2);
		
		/* Global features */
		/* Match Gender */
		vector.addFeature(GenderMatch, getStringLabel(mention1.getGenderType().toString(), mention2.getGenderType().toString()));
			
		/* Match Number */
		vector.addFeature(NumberMatch, getStringLabel(mention1.getNumberType().toString(), mention2.getNumberType().toString()));
		
		/* Match Entity */
		vector.addFeature(EntityMatch, getStringLabel(mention1.getEntityType().toString(), mention2.getEntityType().toString()));
		
		/* Match Pronoun */
		PronounType pronoun1 = mention1.getPronounType(), pronoun2 = mention2.getPronounType();
		String s_pronoun1 = (pronoun1 == null)? NULL : pronoun1.toString(), s_pronoun2 = (pronoun2 == null)? NULL : pronoun2.toString();
		vector.addFeature(PronounMatch, getStringLabel(s_pronoun1, s_pronoun2));
		
		/* Is named entity-pronoun pair */
		vector.addFeature(NEPronounPair, getBinaryLabel((mention1.isNameEntity() && pronoun2 != null) || (pronoun1 != null && mention2.isNameEntity())));
		
		/* Multiple mention index */
		vector.addFeature(MultiMentionIndex, Integer.toString(multiMentionIndex));
		
		/* Particular mention type features */
		switch(multiMentionIndex){
			case -1:	// Both are single metnions
				DEPNode node1 = mention1.getNode(), node2 = mention2.getNode();
				
				/* --- Sieve rules features --- */
				/* Speaker status */
				vector.addFeature(SpeakerStatus, getStringLabel(getBinaryLabel(mention1.hasAttribute(AttributeType.QUOTE)), getBinaryLabel(mention2.hasAttribute(AttributeType.QUOTE))));
				
				/* Exact String Match */
				vector.addFeature(ExactString, getBinaryLabel(exactString.match(mention1, mention2)));
				
				/* Relaxed String Match */
				vector.addFeature(RelaxedString, getBinaryLabel(relaxedString.match(mention1, mention2)));
				
				/* Match precise construct */
				vector.addFeature(AppositionMatch, getBinaryLabel(preiseConstruct.appositiveMatch(mention1, mention2)));
				vector.addFeature(PredicateNomMatch, getBinaryLabel(preiseConstruct.predicateNominativeMatch(mention1, mention2)));
				vector.addFeature(AcronymMatch, getBinaryLabel(preiseConstruct.acronymMatch(mention1, mention2)));
				
				/* --- Tree information features --- */
				/* POS tags */
				vector.addFeature(CurrentPOSTag, getStringLabel(node1.getPOSTag(), node2.getPOSTag()));
				
				/* Head node POS tags */
				vector.addFeature(HeadNodePOSTag, getStringLabel(node1.getHead().getPOSTag(), node2.getHead().getPOSTag()));
				
				/* Dependency labels */
				vector.addFeature(CurrentDEPLabel, getStringLabel(node1.getLabel(), node2.getLabel()));
				
				/* --- Positional features --- */
				/* Token position in sentence */
				vector.addFeature(TokenSentencePos, getStringLabel(node1.getID(), node2.getID()));
				
				break;
			
			case 0:		// Only mention1 is multiple mention
				AbstractMention tempMention = mention1;
				mention1 = mention2; mention2 = tempMention;
				DEPTree tempTree = tree1;
				tree1 = tree2; tree2 = tempTree;
				/* ... extraction continues in case 1 ... */
			
			case 1:		// Only mention2 is multiple mention
				/* Pronoun type of mention1 */
				pronoun1 = mention1.getPronounType();
				vector.addFeature(PronounType, (pronoun1 == null)? NULL : pronoun1.toString());
				
				break;
			
			case 2:		// Both are multiple mentions
				/* Match sizes of the multiple mentions */
				vector.addFeature(MultiMentionSizes, getStringLabel(mention1.getSubMentions().size(), mention2.getSubMentions().size()));
				
				break;
		}
				
		return vector;
	}
	
	private String getBinaryLabel(boolean bool){
		return (bool)? TRUE : FALSE;
	}
	
	private String getStringLabel(int... strings){
//		s_builder.setLength(0);
		s_builder = new StringBuilder();
		for(int string : strings){
			s_builder.append(string);
			s_builder.append(CharConst.HYPHEN);
		}
		return s_builder.substring(0, s_builder.length()-1);
	}
	
	private String getStringLabel(String... strings){
//		s_builder.setLength(0);
		s_builder = new StringBuilder();
		for(String string : strings){
			s_builder.append(string);
			s_builder.append(CharConst.HYPHEN);
		}
		return s_builder.substring(0, s_builder.length()-1);
	}
	
	/**
	 * @param mention1
	 * @param mention2
	 * @return -1 if both are single mention, 0 if mention1 is multimention, 1 if mention2 is multimention, 2 if both are multimention.
	 */
	private int getMultiMentionIndex(AbstractMention mention1, AbstractMention mention2){
		boolean multi1 = mention1.isMultipleMention(), multi2 = mention2.isMultipleMention();
		
		if(multi1 && multi2) return 2;
		else if(multi1) 	return 0;
		else if(multi2)		return 1;
		
		return -1;
	}
}
