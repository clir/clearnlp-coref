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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.emory.clir.clearnlp.collection.map.ObjectDoubleHashMap;
import edu.emory.clir.clearnlp.coreference.type.AttributeType;
import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.coreference.type.GenderType;
import edu.emory.clir.clearnlp.coreference.type.NumberType;
import edu.emory.clir.clearnlp.coreference.type.PronounType;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 19, 2015
 */
public abstract class AbstractMention implements Serializable {
	private static final long serialVersionUID = 7276153831562287337L;

	protected DEPTree d_tree;
	protected DEPNode d_node;
	protected AbstractMention m_conj;
	
	protected EntityType t_entity;
	protected GenderType t_gender;
	protected NumberType t_number;
	protected PronounType t_pronoun;
	
	protected ObjectDoubleHashMap<AttributeType> m_attr;
	
	public AbstractMention(){
		setTree(null);
		setNode(null);
		setConjunctionMention(null);
		setEntityType(EntityType.UNKNOWN);
		setGenderType(GenderType.UNKNOWN);
		setNumberType(NumberType.UNKNOWN);
		setPronounType(null);
		m_attr = new ObjectDoubleHashMap<>();
	}
	
	public AbstractMention(DEPTree tree, DEPNode node)
	{
		setTree(tree);
		setNode(node);
		setConjunctionMention(null);
		setEntityType(EntityType.UNKNOWN);
		setGenderType(GenderType.UNKNOWN);
		setNumberType(NumberType.UNKNOWN);
		setPronounType(null);
		m_attr = new ObjectDoubleHashMap<>();
	}
	
	public AbstractMention(DEPTree tree, DEPNode node, EntityType entityType){
		setTree(tree);
		setNode(node);
		setEntityType(entityType);
		setConjunctionMention(null);
		setGenderType(GenderType.UNKNOWN);
		setNumberType(NumberType.UNKNOWN);
		if(entityType == EntityType.PRONOUN)	setPronounType(PronounType.UNKNOWN);
		else									setPronounType(null);
		m_attr = new ObjectDoubleHashMap<>();
	}
	
	public AbstractMention(DEPTree tree, DEPNode node, NumberType numberType){
		setTree(tree);
		setNode(node);
		setConjunctionMention(null);
		setEntityType(EntityType.UNKNOWN);
		setGenderType(GenderType.UNKNOWN);
		setNumberType(numberType);
		setPronounType(null);
		m_attr = new ObjectDoubleHashMap<>();
	}
	
	public AbstractMention(DEPTree tree, DEPNode node, EntityType entityType, GenderType genderType){
		setTree(tree);
		setNode(node);
		setConjunctionMention(null);
		setEntityType(entityType);
		setGenderType(genderType);
		setNumberType(NumberType.UNKNOWN);
		if(entityType == EntityType.PRONOUN)	setPronounType(PronounType.UNKNOWN);
		else									setPronounType(null);
		m_attr = new ObjectDoubleHashMap<>();
	}
	
	public AbstractMention(DEPTree tree, DEPNode node, EntityType entityType, NumberType numberType){
		setTree(tree);
		setNode(node);
		setConjunctionMention(null);
		setEntityType(entityType);
		setGenderType(GenderType.UNKNOWN);
		setNumberType(numberType);
		if(entityType == EntityType.PRONOUN)	setPronounType(PronounType.UNKNOWN);
		else									setPronounType(null);
		m_attr = new ObjectDoubleHashMap<>();
	}
	
	public AbstractMention(DEPTree tree, DEPNode node, EntityType entityType, GenderType genderType, NumberType numberType, PronounType pronounType){
		setTree(tree);
		setNode(node);
		setConjunctionMention(null);
		setEntityType(entityType);
		setGenderType(genderType);
		setNumberType(numberType);
		setPronounType(pronounType);
		m_attr = new ObjectDoubleHashMap<>();
	}
	
	/* Helper methods */
	public DEPTree getTree(){
		return d_tree;
	}
	
	public DEPNode getNode(){
		return d_node;
	}
	
	public EntityType getEntityType(){
		return t_entity;
	}
	
	public GenderType getGenderType(){
		return t_gender;
	}
	
	public NumberType getNumberType(){
		return t_number;
	}
	
	public PronounType getPronounType(){
		return t_pronoun;
	}
	
	public double getAttribute(AttributeType type){
		return m_attr.get(type);
	}
	
	public ObjectDoubleHashMap<AttributeType> getFeatureMap(){
		return m_attr;
	}
	
	public AbstractMention getConjunctionMention(){
		return m_conj;
	}
	
	public List<AbstractMention> getConjunctionMentions(){
		if(hasConjunctionMention()){
			AbstractMention mention = this;
			List<AbstractMention> mentions = new ArrayList<>();
			
			do		mentions.add(mention);
			while((	mention = mention.getConjunctionMention()) != null );
			
			Collections.reverse(mentions);
			return mentions;
		}
		return null;
	}
	
	public void setTree(DEPTree tree){
		d_tree = tree;
	}
	
	public void setNode(DEPNode node){
		d_node = node;
	}
	
	public void setEntityType(EntityType type){
		t_entity = type;
	}
	
	public void setGenderType(GenderType type){
		t_gender = type;
	}
	
	public void setNumberType(NumberType type){
		t_number = type;
	}
	
	public void setPronounType(PronounType type){
		t_pronoun = type;
	}
	
	public void setConjunctionMention(AbstractMention mention){
		m_conj = mention;
		if(m_conj != null) addAttribute(AttributeType.CONJUNCTION);
	}
	
	public void addAttribute(AttributeType type){
		m_attr.add(type, 1);
	}
	
	public void addAttribute(AttributeType type, double weight){
		m_attr.add(type, weight);
	}
	
	public boolean isEntityType(EntityType type){
		return t_entity == type;
	}
	
	public boolean isNameEntity(){
		return isEntityType(EntityType.PERSON) || isEntityType(EntityType.LOCATION) || isEntityType(EntityType.ORGANIZATION);
	}
	
	public boolean isGenderType(GenderType type){
		return t_gender == type;
	}
	
	public boolean isNumberType(NumberType type){
		return t_number == type;
	}
	
	public boolean isPronounType(PronounType type){
		return t_pronoun == type;
	}
	
	public boolean isParentMention(AbstractMention mention){
		return getNode().getSubNodeSet().contains(mention.getNode());
	}
	
	public boolean isChildMention(AbstractMention mention){
		return mention.getNode().getSubNodeSet().contains(this);
	}
	
	public boolean hasFeature(AttributeType type){
		return m_attr.containsKey(type);
	}
	
	public boolean hasConjunctionMention(){
		return m_conj != null;
	}
	
	/* Abstract methods */
	abstract public String getWordFrom();
	abstract public String getSubTreeWordSequence();
	abstract public List<String> getSubTreeWordList();
	abstract public List<DEPNode> getSubTreeNodes();
	abstract public String getHeadNodeWordForm();
	abstract public Set<String> getAncestorWords();
	abstract public String getAcronym();
	
	@Override
	public String toString(){
		return getWordFrom() + "\t" + t_entity + "\t" + t_gender + '\t' + t_number + "\t" + t_pronoun;
	}
}
