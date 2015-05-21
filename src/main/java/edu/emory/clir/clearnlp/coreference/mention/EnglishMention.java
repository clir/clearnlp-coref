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

import java.util.ArrayList;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.constituent.CTLibEn;
import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.coreference.type.GenderType;
import edu.emory.clir.clearnlp.coreference.type.NumberType;
import edu.emory.clir.clearnlp.coreference.type.PronounType;
import edu.emory.clir.clearnlp.coreference.utils.util.CoreferenceStringUtils;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.Joiner;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 19, 2015
 */
public class EnglishMention extends AbstractMention{
	private static final long serialVersionUID = -3899758740140875733L;
	
	public EnglishMention(DEPNode node) {
		super();
		this.setNode(node);
	}
	
	public EnglishMention(DEPTree tree, DEPNode node){
		super(tree, node);
	}
	
	public EnglishMention(DEPTree tree, DEPNode node, EntityType entityType){
		super(tree, node, entityType);
	}
	
	public EnglishMention(DEPTree tree, DEPNode node, NumberType numberType){
		super(tree, node, numberType);
	}
	
	public EnglishMention(DEPTree tree, DEPNode node, EntityType entityType, GenderType genderType){
		super(tree, node, entityType, genderType);
	}
	
	public EnglishMention(DEPTree tree, DEPNode node, EntityType entityType, NumberType numberType){
		super(tree, node, entityType, numberType);
	}
	
	public EnglishMention(DEPTree tree, DEPNode node, EntityType entityType, GenderType genderType, NumberType numberType, PronounType pronounType){
		super(tree, node, entityType, genderType, numberType, pronounType);
	}

	@Override
	public String getAcronym() {
		if(isNameEntity()){
			String phrase = Joiner.join(getNode().getSubNodeList().stream()
					.filter(node -> node.isPOSTag(CTLibEn.POS_NNP) || node.isPOSTag(CTLibEn.POS_NNPS))
					.map(node -> node.getWordForm())
					.collect(Collectors.toCollection(ArrayList::new)), " ");
		
			if(!phrase.isEmpty())	return CoreferenceStringUtils.getAllUpperCaseLetters(phrase);
		}
		return null;
	}
}
