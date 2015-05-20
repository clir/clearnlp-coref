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

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.mention.SingleMention;
import edu.emory.clir.clearnlp.coreference.type.GenderType;
import edu.emory.clir.clearnlp.coreference.type.SyntacticRole;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSetWithConfidence;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Apr 13, 2015
 * will need to edit to account for reflexive
 */
public class PronounMatch extends AbstractSieve 
{
	List<SyntacticRole> argumentSlot = new ArrayList<>(Arrays.asList(SyntacticRole.SUBJ, SyntacticRole.AGENT, SyntacticRole.DOBJ, SyntacticRole.IOBJ, SyntacticRole.POBJ)); 
	
	@Override
	public void resolute(List<DEPTree> trees, List<SingleMention> mentions, DisjointSetWithConfidence mentionLinks) {
		SingleMention curr, prev;
		int i, j, size = mentions.size();
		
		for (i = 1; i < size; i++){
			curr = mentions.get(i);
			
			for (j = i-1; j >= 0; j--){
				prev = mentions.get(i);
				if (matchesPerson(curr, prev) || 
					matchesPronoun(curr, prev) || 
					matchesCommonNoun(curr, prev) || 
					matchesWildcardPronoun(curr, prev) ||
					matchesReflexivePronoun(prev, curr)){
					
					mentionLinks.union(i, j, 0); break;
				}
			}
		}		
	}
	
	private boolean matchesPerson(SingleMention curr, SingleMention prev){
		return (curr.isGenderType(GenderType.FEMALE) && prev.isGenderType(GenderType.FEMALE)) || (curr.isGenderType(GenderType.MALE) && prev.isGenderType(GenderType.MALE));
	}
	
	private boolean matchesPronoun(SingleMention curr, SingleMention prev){
		return (curr.isGenderType(GenderType.FEMALE) && (prev.isGenderType(GenderType.FEMALE) || prev.isGenderType(GenderType.FEMALE))) || (curr.isGenderType(GenderType.MALE) && (prev.isGenderType(GenderType.MALE) || prev.isGenderType(GenderType.MALE)));
	}
	
	private boolean matchesCommonNoun(SingleMention curr, SingleMention prev){
		// we need to deal with common nouns
		return false;
	}
	
	private boolean matchesWildcardPronoun(SingleMention curr, SingleMention prev){
		// Yet to be implemented
		return false;
	}
	
	private boolean matchesReflexivePronoun(AbstractMention prev, AbstractMention curr)
	{
		//curr matches person, number, and gender with prev
		return (argumentDomain(prev, curr) || (adjunctDomain(prev, curr)) 
	}
	
	private boolean arguementDomain(AbstractMention prev, AbstractMention curr)
	{
		return prev.getHeadWord().equals(curr.getHeadWord() && indexOf(prev) > indexOf(curr));
	}
	
	private int indexOf(AbstractMention<DEPNode> mention)
	{
		DEPNode node = mention.getNode();
		
	}
	
	private boolean adjunctDomain(AbstractMention prev, AbstractMention curr)
	{
		
	}
}
