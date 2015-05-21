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
import edu.emory.clir.clearnlp.coreference.type.GenderType;
import edu.emory.clir.clearnlp.coreference.type.PronounType;
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
		
		for(i = size-1; i > 0; i--){
			curr = mentions.get(i);
			for(j = i-1; j >= 0; j--){
				prev = mentions.get(j);
				if(match(prev, curr))	mentionLinks.union(i, j, 0);
			}
		}
	}
	
	private boolean match(AbstractMention mention1, AbstractMention mention2){
		return matchGender(mention1, mention2) 
			&& matchNumber(mention1, mention2)
			&& matchEntity(mention1, mention2)
			&& matchPronoun(mention1, mention2);
	}
	
	private boolean matchGender(AbstractMention mention1, AbstractMention mention2){
		if(mention1.isGenderType(GenderType.NEUTRAL) || mention2.isGenderType(GenderType.NEUTRAL))
			return !mention1.isGenderType(GenderType.UNKNOWN) && !mention2.isGenderType(GenderType.UNKNOWN);
		return mention1.getGenderType() == mention2.getGenderType();
	}
	
	private boolean matchNumber(AbstractMention mention1, AbstractMention mention2){
		return mention1.getNumberType() == mention2.getNumberType();
	}
	
	private boolean matchEntity(AbstractMention mention1, AbstractMention mention2){
		if(mention2.isNameEntity())
			return mention1.getEntityType() == mention2.getEntityType();
		return !mention2.isEntityType(EntityType.UNKNOWN) || mention1.getEntityType() == mention2.getEntityType();
	}
	
	private boolean matchPronoun(AbstractMention mention1, AbstractMention mention2){
		switch(mention2.getPronounType()){
			case SUBJECT:		return mention1.isPronounType(PronounType.SUBJECT);
			case OBJECT:		return mention1.isPronounType(PronounType.OBJECT);
			case INDEFINITE:	return mention1.isPronounType(PronounType.INDEFINITE);
			case POSSESSIVE:	return mention1.isPronounType(PronounType.POSSESSIVE);
			case DEMOSTRATIVE:	return mention1.isPronounType(PronounType.DEMOSTRATIVE);
			case REFLEXIVE:		return mention1.isPronounType(PronounType.REFLEXIVE);
			case RELATIVE:		return mention1.isPronounType(PronounType.RELATIVE);
			default:			return mention1.getPronounType() == mention2.getPronounType();
		}
	}
}
