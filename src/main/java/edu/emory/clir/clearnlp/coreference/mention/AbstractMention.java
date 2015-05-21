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
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.collection.map.ObjectDoubleHashMap;
import edu.emory.clir.clearnlp.coreference.type.AttributeType;
import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.coreference.type.GenderType;
import edu.emory.clir.clearnlp.coreference.type.NumberType;
import edu.emory.clir.clearnlp.coreference.type.PronounType;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.Joiner;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 19, 2015
 */
public abstract class AbstractMention implements Serializable {
	private static final long serialVersionUID = 7276153831562287337L;

	protected DEPTree d_tree;
	protected DEPNode d_node;
	protected List<DEPNode> l_subNodes;
	protected AbstractMention m_conj;
	
	protected EntityType t_entity;
	protected GenderType t_gender;
	protected NumberType t_number;
	protected PronounType t_pronoun;
	
	protected ObjectDoubleHashMap<AttributeType> m_attr;
	
	/* Constructors */
	public AbstractMention(){
		init(null, null, null, null, null, null);
	}
	
	public AbstractMention(DEPTree tree, DEPNode node){
		init(tree, node, null, null, null, null);
	}
	
	public AbstractMention(DEPTree tree, DEPNode node, EntityType entityType){
		init(tree, node, entityType, null, null, null);
	}
	
	public AbstractMention(DEPTree tree, DEPNode node, NumberType numberType){
		init(tree, node, null, null, numberType, null);
	}
	
	public AbstractMention(DEPTree tree, DEPNode node, EntityType entityType, GenderType genderType){
		init(tree, node, entityType, genderType, null, null);
	}
	
	public AbstractMention(DEPTree tree, DEPNode node, EntityType entityType, NumberType numberType){
		init(tree, node, entityType, null, numberType, null);
	}
	
	public AbstractMention(DEPTree tree, DEPNode node, EntityType entityType, GenderType genderType, NumberType numberType, PronounType pronounType){
		init(tree, node, entityType, genderType, numberType, pronounType);
	}
	
	private void init(DEPTree tree, DEPNode node, EntityType entityType, GenderType genderType, NumberType numberType, PronounType pronounType){
		m_attr = new ObjectDoubleHashMap<>();
		setTree(tree);	setNode(node);	
		setSubTreeNodes(node.getSubNodeList());
		setConjunctionMention(null);
		setEntityType(entityType);
		setGenderType(genderType);
		setNumberType(numberType);
		
		if(entityType == EntityType.PRONOUN && pronounType == null)	setPronounType(PronounType.UNKNOWN);
		else														setPronounType(pronounType);
	}
	
	/* Getters */
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
	
	public List<DEPNode> getSubTreeNodes(){
		return l_subNodes;
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
	
	/* Setters */
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
	
	public void setSubTreeNodes(List<DEPNode> nodes){
		l_subNodes = nodes;
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
	
	/* boolean methods */
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
	
	/* String handling methods */
	public String getWordFrom(){
		return d_node.getWordForm();
	}

	public String getSubTreeWordSequence(){
		return Joiner.join(getSubTreeWordList(), " ");
	}
	
	public List<String> getSubTreeWordList(){
		return getSubTreeNodes().stream().map(node -> node.getWordForm()).collect(Collectors.toList());
	}
	
	public String getHeadNodeWordForm(){
		return getNode().getHead().getWordForm();
	}

	public Set<String> getAncestorWords(){
		return getNode().getAncestorSet().stream().map(node -> node.getWordForm()).collect(Collectors.toSet());
	}
	
	/* Abstract methods */
	abstract public String getAcronym();
	
	@Override
	public String toString(){
		return getWordFrom() + "\t" + t_entity + "\t" + t_gender + '\t' + t_number + "\t" + t_pronoun;
	}
}
