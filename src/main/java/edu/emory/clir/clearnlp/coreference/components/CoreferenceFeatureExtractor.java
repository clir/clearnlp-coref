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
import edu.emory.clir.clearnlp.coreference.dictionary.PathDictionary;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.type.AttributeType;
import edu.emory.clir.clearnlp.coreference.type.FeatureType;
import edu.emory.clir.clearnlp.coreference.type.PronounType;
import edu.emory.clir.clearnlp.coreference.utils.retriever.wordnet.WordNetNounEntryRetriever;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
import edu.emory.clir.clearnlp.coreference.utils.util.CoreferenceDSUtils;
import edu.emory.clir.clearnlp.coreference.utils.util.CoreferenceStringUtils;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.constant.CharConst;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 9, 2015
 */
public class CoreferenceFeatureExtractor implements FeatureType {	
	private StringBuilder s_builder = new StringBuilder();
	private WordNetNounEntryRetriever wordnetDb = new WordNetNounEntryRetriever(PathDictionary.ENG_WORDNETNOUNS);

	public StringFeatureVector getFeatures(List<DEPTree> trees, CoreferantSet bootstrapLinks, AbstractMention mention1, DEPTree tree1, AbstractMention mention2, DEPTree tree2){
		StringFeatureVector vector = new StringFeatureVector();
		
		DEPNode node1 = mention1.getNode(), node2 = mention2.getNode();
		int multiMentionIndex = getMultiMentionIndex(mention1, mention2);
		String wordForm1 = mention1.getWordFrom(), wordForm2 = mention2.getWordFrom();
		PronounType pronoun1 = mention1.getPronounType(), pronoun2 = mention2.getPronounType();
		
		/* Global features */
		/* Match Gender */
		vector.addFeature(Attributes1, mention1.getGenderType().toString());
		vector.addFeature(Attributes2, mention2.getGenderType().toString());
		
		/* Match Number */
		vector.addFeature(Attributes1, mention1.getNumberType().toString());
		vector.addFeature(Attributes2, mention2.getNumberType().toString());
		
		/* Match Entity */
		vector.addFeature(Attributes1, mention1.getEntityType().toString());
		vector.addFeature(Attributes2, mention2.getEntityType().toString());
		
		/* Match Named Entity */
		if(node1 != null)	vector.addFeature(Attributes1, node1.getNamedEntityTag());
		if(node2 != null)	vector.addFeature(Attributes2, node2.getNamedEntityTag());
		
		/* Match Pronoun */
		if(pronoun1 != null)	vector.addFeature(Attributes1, mention1.getPronounType().toString());
		if(pronoun2 != null)	vector.addFeature(Attributes2, mention2.getPronounType().toString());
		
		/* Speaker status */
		if(mention1.hasAttribute(AttributeType.QUOTE))	vector.addFeature(Attributes1, HasQuote1);
		if(mention2.hasAttribute(AttributeType.QUOTE))	vector.addFeature(Attributes2, HasQuote2);
		
		/* Particular mention type features */
		switch(multiMentionIndex){
			case -1:	// Both are single metnions
				/* Exact String Match */
				if(mention1.getSubTreeWordSequence(true).equals(mention2.getSubTreeWordSequence(true)))
					vector.addFeature(Boolean, Integer.toString(ExactStringMatch));
				
				/* POS tags */
				vector.addFeature(CurrentPOSTag, getStringLabel(node1.getPOSTag(), node2.getPOSTag()));
				
				/* Dependency labels */
				vector.addFeature(CurrentDEPLabel, getStringLabel(node1.getLabel(), node2.getLabel()));
				
				/* SubTree tokens, POS tags, and dependency labels */
				for(DEPNode node : mention1.getSubTreeNodes()){
					vector.addFeature(SubTreeTokens1, node.getWordForm().toLowerCase());
					vector.addFeature(SubTreePOSTag1, node.getPOSTag());
					vector.addFeature(SubTreeDEPlabel1, node.getLabel());
				}
				for(DEPNode node : mention2.getSubTreeNodes()){
					vector.addFeature(SubTreeTokens2, node.getWordForm().toLowerCase());
					vector.addFeature(SubTreePOSTag2, node.getPOSTag());
					vector.addFeature(SubTreeDEPlabel2, node.getLabel());
				}
				
				/* SubTree token overlap count*/
				vector.addFeature(SubTreeTokenOverlap, Integer.toString(CoreferenceDSUtils.getOverlapCount(mention1.getSubTreeWordList(true), mention2.getSubTreeWordList(true), false)));
				
				/* Acronym */
				if(CoreferenceStringUtils.getAllUpperCaseLetters(mention1.getSubTreeWordSequence()).equals(CoreferenceStringUtils.getAllUpperCaseLetters(mention1.getSubTreeWordSequence())))
					vector.addFeature(Boolean, Integer.toString(AcronymMatch));
				
				/* Synonym */
				if(wordnetDb.hasSameSynonym(wordForm1, wordForm2))
					vector.addFeature(Boolean, Integer.toString(SynonymMatch));
				
				/* Antonym */
				if(wordnetDb.isAntonyms(wordForm1, wordForm2))
					vector.addFeature(Boolean, Integer.toString(AntonymMatch));
				
				/* Hypernym */
				if(wordnetDb.hasSameHypernym(wordForm1, wordForm2))
					vector.addFeature(Boolean, Integer.toString(HypernymMatch));
				
				/* Modifier sets */
				for(String modifier : mention1.getModifiersList())	vector.addFeature(ModifierSet1, modifier);
				for(String modifier : mention2.getModifiersList())	vector.addFeature(ModifierSet2, modifier);
				
				/* X-Pronoun pairs */
				if(pronoun1 != null){
					vector.addFeature(XPronounPair, getStringLabel(mention1.getWordFrom(), node2.getPOSTag()));
					vector.addFeature(XPronounPair, getStringLabel(mention1.getWordFrom(), mention2.getEntityType().toString()));
					vector.addFeature(XPronounPair, getStringLabel(mention1.getWordFrom(), mention2.getGenderType().toString()));
				}
				if(pronoun2 != null){
					vector.addFeature(XPronounPair, getStringLabel(node1.getPOSTag(), mention2.getWordFrom()));
					vector.addFeature(XPronounPair, getStringLabel(mention1.getEntityType().toString(), mention2.getWordFrom()));
					vector.addFeature(XPronounPair, getStringLabel(mention1.getGenderType().toString(), mention2.getWordFrom()));
				}
				
				break;
			
			case 0:		// Only mention1 is multiple mention
				AbstractMention tempMention = mention1;
				mention1 = mention2; mention2 = tempMention;
				tree1 = mention1.getTree(); tree2 = mention2.getTree();
				wordForm1 = mention1.getWordFrom(); wordForm2 = mention2.getWordFrom();
				pronoun1 = mention1.getPronounType(); pronoun2 = mention2.getPronounType();
				/* ... extraction continues in case 1 ... */
			
			case 1:		// Only mention2 is multiple mention
				node1 = mention1.getNode(); node2 = mention2.getNode();
				
				/* X-Pronoun pairs */
				if(pronoun2 != null){
					vector.addFeature(XPronounPair, getStringLabel(node1.getPOSTag(), mention2.getWordFrom()));
					vector.addFeature(XPronounPair, getStringLabel(mention1.getEntityType().toString(), mention2.getWordFrom()));
					vector.addFeature(XPronounPair, getStringLabel(mention1.getGenderType().toString(), mention2.getWordFrom()));
				}
				
				break;
			
			case 2:		// Both are multiple mentions
				/* Word from match */
				if(wordForm1.equals(wordForm2))	vector.addFeature(Boolean, Integer.toString(MultiMentionWordForm));
				
				break;
		}
		
		return vector;
	}
	
	private String getStringLabel(int... strings){
		s_builder.setLength(0);
		for(int string : strings){
			s_builder.append(string);
			s_builder.append(CharConst.HYPHEN);
		}
		return s_builder.substring(0, s_builder.length()-1);
	}
	
	private String getStringLabel(String... strings){
		s_builder.setLength(0);
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
