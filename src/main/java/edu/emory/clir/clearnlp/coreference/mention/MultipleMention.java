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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.collection.map.ObjectIntHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.coreference.type.GenderType;
import edu.emory.clir.clearnlp.coreference.type.NumberType;
import edu.emory.clir.clearnlp.util.Joiner;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 19, 2015
 */
public class MultipleMention extends AbstractMention<List<SingleMention>>{
	private static final long serialVersionUID = -3206843928289079965L;
	
	public MultipleMention(SingleMention... mentions) {
		super(null, Arrays.asList(mentions));
		initCollectiveAttributes();
	}
	
	private void initCollectiveAttributes(){
		ObjectIntHashMap<EntityType> map = new ObjectIntHashMap<>();
		Set<GenderType> set = new HashSet<>();
		
		for(SingleMention mention : getNode()){
			map.add(mention.getEntityType());
			set.add(mention.getGenderType());
		}
		
		// Entity Type
		List<ObjectIntPair<EntityType>> list = map.toList();
		Collections.sort(list, Collections.reverseOrder());
		
		if(list.size() == 1)					setEntityType(list.get(0).o);
		else if(list.get(0).i == list.get(1).i)	setEntityType(EntityType.UNKNOWN);
		else									setEntityType(list.get(0).o);
		
		// Gender Type	
		if(set.contains(GenderType.MALE) && set.contains(GenderType.FEMALE))	setGenderType(GenderType.NEUTRAL);
		else if(set.contains(GenderType.MALE))									setGenderType(GenderType.MALE);
		else if(set.contains(GenderType.FEMALE))								setGenderType(GenderType.FEMALE);
		else 																	setGenderType(GenderType.NEUTRAL);
		
		// Number Type
		setNumberType(NumberType.PLURAL);
		
		// Pronoun Type
		setPronounType(null);
	}

	@Override
	public String getWordFrom() {
		return Joiner.join(getNode().stream().map(mention -> mention.getWordFrom()).collect(Collectors.toCollection(ArrayList::new)), " ");
	}

	@Override
	public String getSubTreeWordSequence() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHeadWord() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAncestorWords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAcronym() {
		// TODO Auto-generated method stub
		return null;
	}
}
