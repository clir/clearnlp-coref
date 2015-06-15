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
import edu.emory.clir.clearnlp.coreference.type.AttributeType;
import edu.emory.clir.clearnlp.coreference.type.FeatureType;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 9, 2015
 */
public class CoreferenceFeatureExtractor implements FeatureType{

	public StringFeatureVector getFeatures(List<DEPTree> trees, AbstractMention mention1, DEPTree tree1, AbstractMention mention2, DEPTree tree2){
		StringFeatureVector vector = new StringFeatureVector();
		
		if(!mention1.isMultipleMention() && !mention2.isMultipleMention()){
			DEPNode node1 = mention1.getNode(), node2 = mention2.getNode();
			int i, tree1Id = mention1.getTreeId(), tree2Id = mention2.getTreeId();
			
			/* Exact String Match */
			if(mention1.getSubTreeWordSequence().equals(mention2.getSubTreeWordSequence()))	vector.addFeature(ExactString, TRUE);
			else																			vector.addFeature(ExactString, FALSE);
			
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
			
			/* POS Tags */
			vector.addFeature(CurrentPOSTag, node1.getPOSTag()+node2.getPOSTag());
			
			/* Dependency Labels */
			vector.addFeature(CurrentDEPLabel, node1.getLabel()+node2.getLabel());
			
			/* Match Gender*/
			if(mention1.matchGenderType(mention2))	vector.addFeature(GenderMatch, TRUE);
			else									vector.addFeature(GenderMatch, FALSE);
				
			/* Match Number*/
			if(mention1.matchNumberType(mention2))	vector.addFeature(NumberMatch, TRUE);
			else									vector.addFeature(NumberMatch, FALSE);
			
			/* Match Entity*/
			if(mention1.matchEntityType(mention2)) 	vector.addFeature(EntityMatch, TRUE);
			else									vector.addFeature(EntityMatch, FALSE);
			
			/* Match Pronoun */
			String pronoun1 = (mention1.getPronounType() == null)? "null" : mention1.getPronounType().toString(),
				   pronoun2 = (mention2.getPronounType() == null)? "null" : mention2.getPronounType().toString();
			vector.addFeature(PronounMatch, pronoun1+pronoun2);
			
			/* Speaker Status */
			String speaker1 = mention1.hasAttribute(AttributeType.QUOTE)? TRUE : FALSE,
				   speaker2 = mention2.hasAttribute(AttributeType.QUOTE)? TRUE : FALSE;
			vector.addFeature(SpeakerStatus, speaker1+speaker2);
			
			/* Parent-child Mention Relationship */
//			if(mention1.isParentMention(mention2))	vector.addFeature(IsParentRel, TRUE);
//			else									vector.addFeature(IsParentRel, FALSE);
		}
		
		return vector;
	}
}
