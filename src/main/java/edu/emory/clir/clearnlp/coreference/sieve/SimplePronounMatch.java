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

import java.util.Set;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.type.AttributeType;
import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.coreference.type.PronounType;
import edu.emory.clir.clearnlp.util.DSUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 8, 2015
 */
public class SimplePronounMatch extends AbstractSieve{

	private final Set<String> anonymousPronouns = DSUtils.toHashSet("it", "its", "itself");
	
	@Override
	protected boolean match(AbstractMention prev, AbstractMention curr) {
		if(!isTargetPronoun(prev) && isTargetPronoun(curr))
			return matchPronoun(prev, curr);
		return false;
	}
	
	private boolean isTargetPronoun(AbstractMention mention){
		return 	mention.isPronounType(PronounType.SUBJECT) || 
				mention.isPronounType(PronounType.OBJECT) ||
				mention.isPronounType(PronounType.POSSESSIVE) ||
				mention.isPronounType(PronounType.REFLEXIVE);
	}
	
	private boolean matchPronoun(AbstractMention entity, AbstractMention pronoun){
		if(!entity.hasFeature(AttributeType.QUOTE) && !pronoun.hasFeature(AttributeType.QUOTE)){
			if(entity.isNameEntity()){
				if(!anonymousPronouns.contains(pronoun.getLemma()))
					return pronoun.matchGenderType(entity) && pronoun.matchNumberType(entity);
			}
			else if(entity.isEntityType(EntityType.COMMON))
				return pronoun.matchGenderType(entity) && pronoun.matchNumberType(entity);
		}

		return false;
	}
}
