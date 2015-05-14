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
package edu.emory.clir.clearnlp.coreference.mention.proper;

import java.io.Serializable;

import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.coreference.type.NumberType;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 13, 2015
 */
public class ProperNoun implements Serializable{
	private static final long serialVersionUID = -1812023556802755863L;
	
	public String wordFrom;
	public EntityType e_type;
	public NumberType  n_type;
	
	public ProperNoun(String s){
		wordFrom = s;
		e_type = EntityType.PROPER_UNKOWN;
		n_type = NumberType.UNKNOWN;
	}
	
	public ProperNoun(String s, EntityType e, NumberType n){
		wordFrom = s;
		e_type = e;
		n_type = n;
	}
	
	public ProperNoun(String s, String e, String n){
		wordFrom = s;
		e_type = EntityType.valueOf(e);
		n_type = NumberType.valueOf(n);
	}
	
	public Mention toMention(DEPTree tree, DEPNode node){
		Mention mention = new Mention(tree, node);
		mention.setEntityType(e_type);
		mention.setNumberType(n_type);
		return mention;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder(wordFrom);
		sb.append('\t');	sb.append(e_type);
		sb.append('\t');	sb.append(n_type);
		return sb.toString();
	}
}
