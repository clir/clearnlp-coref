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
import edu.emory.clir.clearnlp.coreference.mention.EnglishMention;
import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.coreference.type.GenderType;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSetWithConfidence;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Apr 13, 2015
 */
public class PronounMatch extends AbstractSieve {
	final static private List<String> argumentSlot = new ArrayList<>(Arrays.asList(DEPTagEn.DEP_SUBJ, DEPTagEn.DEP_AGENT, DEPTagEn.DEP_DOBJ, DEPTagEn.DEP_IOBJ, DEPTagEn.DEP_POBJ));
	final static private Pattern verb = Pattern.compile("VB[D||P||Z]{0,1}");
	
	@Override
	public void resolute(List<DEPTree> trees, List<AbstractMention> mentions, DisjointSetWithConfidence mentionLinks) {
		AbstractMention curr, prev;
		int i, j, size = mentions.size();
		
		for(i = size-1; i > 0; i--){
			curr = mentions.get(i);
			
			for (j = i-1; j >= 0; j--){
				prev = mentions.get(i);
				if (matchesPronoun(curr, prev)) {
					if (matchesReflexivePronoun(prev, curr)) {
						mentionLinks.union(i, j, 0); break;
					}
				}
			}
		}
	}
	
	private boolean matchesPronoun(AbstractMention curr, AbstractMention prev)
	{
		return matchesGender(curr, prev) && matchesNumber(curr, prev);
	}
	
	private boolean matchesGender(AbstractMention curr, AbstractMention prev)
	{
		return curr.isGenderType(prev.getGenderType()) && !curr.isGenderType(GenderType.UNKNOWN) || !curr.isGenderType(GenderType.NEUTRAL);
	}
	
	private boolean matchesNumber(AbstractMention curr, AbstractMention prev)
	{
		return curr.isNumberType(prev.getNumberType());
	}
	
	private boolean matchNumber(AbstractMention mention1, AbstractMention mention2){
		return mention1.getNumberType() == mention2.getNumberType();
	}
	
	private boolean matchEntity(AbstractMention mention1, AbstractMention mention2){
		if(mention2.isNameEntity())
			return mention1.getEntityType() == mention2.getEntityType();
		return !mention2.isEntityType(EntityType.UNKNOWN) || mention1.getEntityType() == mention2.getEntityType();
	}
	
//	private boolean matchPronoun(AbstractMention mention1, AbstractMention mention2){
//		if(mention2.getPronounType() != null)
//			switch(mention2.getPronounType()){
//				case SUBJECT:		return mention1.isPronounType(PronounType.SUBJECT);
//				case OBJECT:		return mention1.isPronounType(PronounType.OBJECT);
//				case INDEFINITE:	return mention1.isPronounType(PronounType.INDEFINITE);
//				case POSSESSIVE:	return mention1.isPronounType(PronounType.POSSESSIVE);
//				case DEMOSTRATIVE:	return mention1.isPronounType(PronounType.DEMOSTRATIVE);
//				case REFLEXIVE:		return mention1.isPronounType(PronounType.REFLEXIVE);
//				case RELATIVE:		return mention1.isPronounType(PronounType.RELATIVE);
//				default:			return mention1.getPronounType() == mention2.getPronounType();
//			}
//		return false;
//	}
	
	private boolean matchesReflexivePronoun(AbstractMention prev, AbstractMention curr)
	{
		return (argumentDomain(prev, curr) || adjunctDomain(prev, curr) || NPDomain(prev, curr) || VerbArgument(prev, curr) || extendedDomain(prev, curr)); 
	}
	
	private boolean argumentDomain(AbstractMention prev, AbstractMention curr)
	{
		return prev.getHeadNodeWordForm().equals(curr.getHeadNodeWordForm()) && indexOf(prev) > indexOf(curr);
	}
	
	private int indexOf(AbstractMention mention)
	{
		String label = mention.getNode().getLabel();
		return argumentSlot.indexOf(label);
	}
	
	private boolean adjunctDomain(AbstractMention prev, AbstractMention curr)	//ask Jinho again
	{
		DEPNode node = prev.getNode();
		return node.isLabel(DEPTagEn.DEP_POBJ) && node.getHead().containsDependent((DEPNode) node.getArgumentCandidateSet(1, false));
	}
	
	private boolean NPDomain(AbstractMention prev, AbstractMention curr)
	{
		DEPNode node = curr.getNode();
		return node.isLabel(DEPTagEn.DEP_ATTR) && prev.getNode().isArgumentOf(node.getHead()) || adjunctDomain(prev, curr);
	}
	
	private boolean VerbArgument(AbstractMention prev, AbstractMention curr)
	{
		DEPNode temp;
		DEPNode prevNode = prev.getNode();
		DEPNode currNode = curr.getNode();
		if (prevNode.isArgumentOf(verb)) 
			if ((temp = (DEPNode) prevNode.getDependentList().stream().filter(x -> x.isArgumentOf(currNode))) != null && adjunctDomain(prev, new EnglishMention(temp))) 
				return true;
		return false;
	}
	
	private boolean extendedDomain(AbstractMention prev, AbstractMention curr)
	{
		List<DEPNode> listOfNodes;
		if ((listOfNodes = prev.getNode().getDependentListByLabel(DEPTagEn.DEP_ATTR)) != null) {
			for (DEPNode node : listOfNodes) {
				if (argumentDomain(new EnglishMention(node), curr) || adjunctDomain(new EnglishMention(node), curr))
					return true;
			}
		}
		return false;
	}
}
