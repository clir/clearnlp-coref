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
import edu.emory.clir.clearnlp.coreference.sieve.SimplePronounMatch;
import edu.emory.clir.clearnlp.coreference.sieve.SpeakerIdentification;
import edu.emory.clir.clearnlp.coreference.type.FeatureType;
import edu.emory.clir.clearnlp.coreference.utils.util.CoreferenceDSUtils;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 9, 2015
 */
public class CoreferenceFeatureExtractor implements FeatureType{
	
	private SpeakerIdentification speakidentify = new SpeakerIdentification();
	private ExactStringMatch exactString = new ExactStringMatch(true);
	private RelaxedStringMatch relaxedString = new RelaxedStringMatch(true);
	private PreciseConstructMatch preiseConstruct = new PreciseConstructMatch();
	private SimplePronounMatch pronounMatch	= new SimplePronounMatch();

	public StringFeatureVector getFeatures(List<DEPTree> trees, AbstractMention mention1, DEPTree tree1, AbstractMention mention2, DEPTree tree2){
		StringFeatureVector vector = new StringFeatureVector();
		
		/* Global features */
		/* Match Gender */
		vector.addFeature(GenderMatch, getBinaryLabel(mention1.matchGenderType(mention2)));
			
		/* Match Number */
		vector.addFeature(NumberMatch, getBinaryLabel(mention1.matchNumberType(mention2)));
		
		/* Match Entity */
		vector.addFeature(EntityMatch, getBinaryLabel(mention1.matchEntityType(mention2)));
		
		/* Match Pronoun */
		vector.addFeature(PronounMatch, getBinaryLabel(pronounMatch.match(mention1, mention2)));
		
		/* Particular mention type features */
		switch(getMultiMentionIndex(mention1, mention2)){
			case -1:	// Both are single metnions
				DEPNode node1 = mention1.getNode(), node2 = mention2.getNode();
				int i, tree1Id = mention1.getTreeId(), tree2Id = mention2.getTreeId();
				
				/* --- Sieve rules features --- */
				/* Speaker status */
				vector.addFeature(SpeakerStatus, getBinaryLabel(speakidentify.match(mention1, mention2)));
				
				/* Exact String Match */
				vector.addFeature(ExactString, getBinaryLabel(exactString.match(mention1, mention2)));
				
				/* Relaxed String Match */
				vector.addFeature(RelaxedString, getBinaryLabel(relaxedString.match(mention1, mention2)));
				
				/* Match precise construct */
				vector.addFeature(PreciseConstuctMatch, getBinaryLabel(preiseConstruct.match(mention1, mention2)));
				
				/* --- Tree information features --- */
				/* POS tags */
				vector.addFeature(CurrentPOSTag, node1.getPOSTag()+node2.getPOSTag());
				
				/* Head node POS tags */
				vector.addFeature(HeadNodePOSTag, node1.getHead().getPOSTag()+node2.getHead().getPOSTag());
				
				/* Dependency labels */
				vector.addFeature(CurrentDEPLabel, node1.getLabel()+node2.getLabel());
				
				/* --- Positional features --- */
				/* Token position in sentence */
				vector.addFeature(TokenSentencePos, node1.getID() + StringConst.HYPHEN + node2.getID());
				
				/* Sentence offset */
				vector.addFeature(SentenceOffset, Integer.toString(tree1Id - tree2Id));
				
				/* Token offset */
				int tokenOffset = node2.getID();
				if(tree1Id == tree2Id) tokenOffset -= node1.getID();
				else{
					for(i = tree2Id-1; i > tree1Id; i--) tokenOffset += trees.get(i).size();	
					tokenOffset += trees.get(tree1Id).size() - node1.getID();
				}
				vector.addFeature(TokenOffset, Integer.toString(tokenOffset));

				break;
			case 0:		// Only mention1 is multiple mention
				CoreferenceDSUtils.swapAddresses(mention1, mention2);
				CoreferenceDSUtils.swapAddresses(tree1, tree2);
				/* ... extraction continues in case 1 ... */
			case 1:		// Only mention2 is multiple mention
				break;
			case 2:		// Both are multiple mentions
				/* Match sizes of the multiple mentions */
				vector.addFeature(MultiMentionSizes, mention1.getSubMentions().size() + StringConst.HYPHEN + mention2.getSubMentions().size());
				
				break;
		}
				
		return vector;
	}
	
	private String getBinaryLabel(boolean bool){
		return (bool)? TRUE : FALSE;
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
