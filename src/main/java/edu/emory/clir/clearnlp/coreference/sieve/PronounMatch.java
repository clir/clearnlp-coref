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

import java.util.List;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSetWithConfidence;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Apr 13, 2015
 */
public class PronounMatch extends AbstractSieve {

	@Override
	public void resolute(List<DEPTree> trees, List<AbstractMention> mentions, DisjointSetWithConfidence mentionLinks) {
		AbstractMention curr, prev;
		int i, j, size = mentions.size();
		
		for (i = 1; i < size; i++){
			curr = mentions.get(i);
			
			for (j = i-1; j >= 0; j--){
				prev = mentions.get(i);
				if (matchesPerson(curr, prev) || 
					matchesPronoun(curr, prev) || 
					matchesCommonNoun(curr, prev) || 
					matchesWildcardPronoun(curr, prev)){
					
					mentionLinks.union(i, j, 0); break;
				}
			}
		}		
	}
	
	private boolean matchesPerson(AbstractMention curr, AbstractMention prev){
		return (curr.isEntityType(EntityType.PERSON_FEMALE) && prev.isEntityType(EntityType.PERSON_FEMALE)) || (curr.isEntityType(EntityType.PERSON_MALE)   && prev.isEntityType(EntityType.PERSON_MALE));
	}
	
	private boolean matchesPronoun(AbstractMention curr, AbstractMention prev){
		return (curr.isEntityType(EntityType.PRONOUN_FEMALE) && (prev.isEntityType(EntityType.PRONOUN_FEMALE) || prev.isEntityType(EntityType.PERSON_FEMALE))) || (curr.isEntityType(EntityType.PRONOUN_MALE)   && (prev.isEntityType(EntityType.PRONOUN_MALE)   || prev.isEntityType(EntityType.PERSON_MALE)));
	}
	
	private boolean matchesCommonNoun(AbstractMention curr, AbstractMention prev){
		// we need to deal with common nouns
		return false;
	}
	
	private boolean matchesWildcardPronoun(AbstractMention curr, AbstractMention prev){
		// Yet to be implemented
		return false;
	}
}
