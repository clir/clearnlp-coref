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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.constituent.CTLibEn;
import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.coreference.type.GenderType;
import edu.emory.clir.clearnlp.coreference.type.NumberType;
import edu.emory.clir.clearnlp.coreference.type.PronounType;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.Joiner;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 19, 2015
 */
public class SingleMention extends AbstractMention<DEPNode>{
	private static final long serialVersionUID = -3899758740140875733L;
	
	public SingleMention(DEPTree tree, DEPNode node){
		super(tree, node);
	}
	
	public SingleMention(DEPTree tree, DEPNode node, EntityType entityType){
		super(tree, node, entityType);
	}
	
	public SingleMention(DEPTree tree, DEPNode node, NumberType numberType){
		super(tree, node, numberType);
	}
	
	public SingleMention(DEPTree tree, DEPNode node, EntityType entityType, GenderType genderType){
		super(tree, node, entityType, genderType);
	}
	
	public SingleMention(DEPTree tree, DEPNode node, EntityType entityType, NumberType numberType){
		super(tree, node, entityType, numberType);
	}
	
	public SingleMention(DEPTree tree, DEPNode node, EntityType entityType, GenderType genderType, NumberType numberType, PronounType pronounType){
		super(tree, node, entityType, genderType, numberType, pronounType);
	}

	@Override
	public String getWordFrom() {
		return d_node.getWordForm();
	}

	@Override
	public String getSubTreeWordSequence() {
		return Joiner.join(getNode().getSubNodeList().stream().map(node -> node.getWordForm()).collect(Collectors.toCollection(ArrayList::new)), " ");
	}
	
	@Override
	public String getHeadWord(){
		return getNode().getHead().getWordForm();
	}

	@Override
	public Set<String> getAncestorWords() {
		return getNode().getAncestorSet().stream().map(node -> node.getWordForm()).collect(Collectors.toCollection(HashSet::new));
	}

	@Override
	public String getAcronym() {
		if(isNameEntity()){
			String phrase = Joiner.join(getNode().getSubNodeList().stream()
					.filter(node -> node.isPOSTag(CTLibEn.POS_NNP) || node.isPOSTag(CTLibEn.POS_NNPS))
					.map(node -> node.getWordForm())
					.collect(Collectors.toCollection(ArrayList::new)), " ");
		
			if(!phrase.isEmpty()){
				StringBuilder sb = new StringBuilder();
				for(char c : phrase.toCharArray())
					if(c >= 'A' && c <= 'Z')	sb.append(c);
				return sb.toString();
			}
		}
	
		return null;
	}
}
