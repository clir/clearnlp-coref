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
package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.coreference.type.GenderType;
import edu.emory.clir.clearnlp.coreference.type.PronounType;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSet;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Apr 13, 2015
 */
public class PronounMatch extends AbstractSieve {
	
	/* Argument infomation */
	final private Pattern verb = Pattern.compile("VB[D||P||Z]{0,1}");
	final private List<String> argumentSlot = new ArrayList<>(Arrays.asList(DEPTagEn.DEP_SUBJ, DEPTagEn.DEP_AGENT, DEPTagEn.DEP_DOBJ, DEPTagEn.DEP_IOBJ, DEPTagEn.DEP_POBJ));
	
	private int getArgumentHierarchy(DEPNode node){
		return argumentSlot.indexOf(node.getLabel());
	}
	
	
	@Override
	public void resolute(List<DEPTree> trees, List<AbstractMention> mentions, DisjointSet mentionLinks) {
		AbstractMention curr, prev;
		int i, j, size = mentions.size();
		
		for (i=1; i<size; i++){
			curr = mentions.get(i);
			
			for (j=i-1; j>=0; j--){
				prev = mentions.get(j);
				
				if (match(prev, curr)){
					mentionLinks.union(i, j, 0);
					break;
				}
			}
		}
	}
	
	private boolean match(AbstractMention prev, AbstractMention curr){
		return matchesGender(prev, curr) && matchesNumber(prev, curr) && matchesEntity(prev, curr); // && matchesPronoun(prev, curr);
	}
	
	private boolean matchesGender(AbstractMention prev, AbstractMention curr){
		if(curr.isGenderType(GenderType.NEUTRAL) || prev.isGenderType(GenderType.NEUTRAL))
			return !curr.isGenderType(GenderType.UNKNOWN) && !prev.isGenderType(GenderType.UNKNOWN);
		return curr.getGenderType() == prev.getGenderType();
	}
	
	private boolean matchesNumber(AbstractMention prev, AbstractMention curr){
		return curr.isNumberType(prev.getNumberType());
	}
	
	private boolean matchesEntity(AbstractMention prev, AbstractMention curr){
		if(curr.isNameEntity())
			return prev.getEntityType() == curr.getEntityType();
		return !curr.isEntityType(EntityType.UNKNOWN) || prev.getEntityType() == curr.getEntityType();
	}
	
	private boolean matchesPronoun(AbstractMention prev, AbstractMention curr){
		if(curr.getPronounType() != null)
			switch(curr.getPronounType()){
				case SUBJECT:		return prev.isPronounType(PronounType.SUBJECT);
				case OBJECT:		return prev.isPronounType(PronounType.OBJECT);
				case INDEFINITE:	return prev.isPronounType(PronounType.INDEFINITE);
				case POSSESSIVE:	return prev.isPronounType(PronounType.POSSESSIVE);
				case DEMOSTRATIVE:	return prev.isPronounType(PronounType.DEMOSTRATIVE);
				case REFLEXIVE:		return matchesReflexivePronoun(prev.getNode(), curr.getNode());
				case RELATIVE:		return prev.isPronounType(PronounType.RELATIVE);
				default:			return prev.getPronounType() == curr.getPronounType();
			}
		return false;
	}
	
	private boolean matchesReflexivePronoun(DEPNode prev, DEPNode curr){
		DEPNode head = prev.getLowestCommonAncestor(curr);
		return argumentDomain(head, prev, curr) || adjunctDomain(head, prev, curr); // || NPDomain(prev, curr) || VerbArgument(prev, curr) || extendedDomain(prev, curr); 
	}
	
	private boolean argumentDomain(DEPNode head, DEPNode prev, DEPNode curr){
		return head != null && prev.getHead() == head && curr.getHead() == head && getArgumentHierarchy(prev) > getArgumentHierarchy(curr);
	}
	
	private boolean adjunctDomain(DEPNode head, DEPNode prev, DEPNode curr){
		DEPNode adjuncHead = curr.getHead();
		return head != null && adjuncHead != null && curr.isLabel(DEPTagEn.DEP_POBJ) && adjuncHead.isLabel(DEPTagEn.DEP_PREP) && prev.getLowestCommonAncestor(adjuncHead) != null;
	}
//	
//	private boolean NPDomain(AbstractMention prev, AbstractMention curr)
//	{
//		DEPNode node = curr.getNode();
//		return node.isLabel(DEPTagEn.DEP_ATTR) && prev.getNode().isArgumentOf(node.getHead()) || adjunctDomain(prev, curr);
//	}
//	
//	private boolean VerbArgument(AbstractMention prev, AbstractMention curr)
//	{
//		DEPNode temp;
//		DEPNode prevNode = prev.getNode();
//		DEPNode currNode = curr.getNode();
//		
//		if (prevNode.isArgumentOf(verb)) 
//			if ((temp = (DEPNode) prevNode.getDependentList().stream().filter(x -> x.isArgumentOf(currNode))) != null && adjunctDomain(prev, new EnglishMention(temp))) 
//				return true;
//		return false;
//	}
//	
//	private boolean extendedDomain(AbstractMention prev, AbstractMention curr)
//	{
//		List<DEPNode> listOfNodes;
//		if ((listOfNodes = prev.getNode().getDependentListByLabel(DEPTagEn.DEP_ATTR)) != null) {
//			for (DEPNode node : listOfNodes) {
//				if (argumentDomain(new EnglishMention(node), curr) || adjunctDomain(new EnglishMention(node), curr))
//					return true;
//			}
//		}
//		return false;
//	}
}
