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
package edu.emory.clir.clearnlp.coreference.mention.pronoun;

import java.io.Serializable;

import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.coreference.type.NumberType;
import edu.emory.clir.clearnlp.coreference.type.PronounType;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 11, 2015
 */
public class Pronoun implements Serializable{
	private static final long serialVersionUID = 530671380338699884L;
	
	public String wordFrom;
	public EntityType e_type;
	public NumberType  n_type;
	public PronounType p_type;
	
	public Pronoun(String s){
		wordFrom = s;
		e_type = EntityType.PRONOUN_UNKNOWN;
		n_type = NumberType.UNKNOWN;
		p_type = PronounType.UNKNOWN;
	}
	
	public Pronoun(String s, EntityType e, NumberType n){
		wordFrom = s;
		e_type = e;
		n_type = n;
		p_type = PronounType.REGULAR;
	}
	
	public Pronoun(String s, String e, String n){
		wordFrom = s;
		e_type = EntityType.valueOf(e);
		n_type = NumberType.valueOf(n);
		p_type = PronounType.REGULAR;
	}
	
	public Pronoun(String s, EntityType e, NumberType n, PronounType p){
		wordFrom = s;
		e_type = e;
		n_type = n;
		p_type = p;
	}
	
	public Pronoun(String s, String e, String n, String p){
		wordFrom = s;
		e_type = EntityType.valueOf(e);
		n_type = NumberType.valueOf(n);
		p_type = PronounType.valueOf(p);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder(wordFrom);
		sb.append('\t');	sb.append(e_type);
		sb.append('\t');	sb.append(n_type);
		sb.append('\t');	sb.append(p_type);
		return sb.toString();
	}
}
