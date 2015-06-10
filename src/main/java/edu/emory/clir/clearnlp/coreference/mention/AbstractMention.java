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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.collection.map.ObjectDoubleHashMap;
import edu.emory.clir.clearnlp.collection.map.ObjectIntHashMap;
import edu.emory.clir.clearnlp.coreference.type.AttributeType;
import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.coreference.type.GenderType;
import edu.emory.clir.clearnlp.coreference.type.NumberType;
import edu.emory.clir.clearnlp.coreference.type.PronounType;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.Joiner;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 19, 2015
 */
public abstract class AbstractMention implements Serializable {
	private static final long serialVersionUID = 7276153831562287337L;

	protected int treeId;
	protected DEPTree d_tree;
	protected DEPNode d_node;
	protected List<DEPNode> l_subNodes;
	protected List<AbstractMention> l_subMentions; // d_node = null; l_subNodes = null;
	
	protected EntityType t_entity;
	protected GenderType t_gender;
	protected NumberType t_number;
	protected PronounType t_pronoun;
	
	protected ObjectDoubleHashMap<AttributeType> m_attr;
	
	/* Constructors */
	public AbstractMention(){
		init(-1, null, null, null, null, null, null);
	}
	
	public AbstractMention(AbstractMention... mentions){
		init(-1, null, null, null, null, null, null);
		setSubMentions(DSUtils.toArrayList(mentions));
		initMultipleMention();
	}
	
	public AbstractMention(List<AbstractMention> mentions){
		init(-1, null, null, null, null, null, null);
		setSubMentions(mentions);
		initMultipleMention();
	}
	
	public AbstractMention(int t_id, DEPTree tree, DEPNode node){
		init(t_id, tree, node, null, null, null, null);
	}
	
	public AbstractMention(int t_id, DEPTree tree, DEPNode node, EntityType entityType){
		init(t_id, tree, node, entityType, null, null, null);
	}
	
	public AbstractMention(int t_id, DEPTree tree, DEPNode node, NumberType numberType){
		init(t_id, tree, node, null, null, numberType, null);
	}
	
	public AbstractMention(int t_id, DEPTree tree, DEPNode node, EntityType entityType, GenderType genderType){
		init(t_id, tree, node, entityType, genderType, null, null);
	}
	
	public AbstractMention(int t_id, DEPTree tree, DEPNode node, EntityType entityType, NumberType numberType){
		init(t_id, tree, node, entityType, null, numberType, null);
	}
	
	public AbstractMention(int t_id, DEPTree tree, DEPNode node, EntityType entityType, GenderType genderType, NumberType numberType, PronounType pronounType){
		init(t_id, tree, node, entityType, genderType, numberType, pronounType);
	}
	
	private void init(int t_id, DEPTree tree, DEPNode node, EntityType entityType, GenderType genderType, NumberType numberType, PronounType pronounType){
		m_attr = new ObjectDoubleHashMap<>();
		setTreeId(t_id);	setTree(tree);	setNode(node);	
		setSubTreeNodes((node == null)? null : node.getSubNodeList());
		setSubMentions(null);
		setEntityType(entityType);
		setGenderType(genderType);
		setNumberType(numberType);
		
		if(entityType == EntityType.PRONOUN && pronounType == null)	setPronounType(PronounType.UNKNOWN);
		else														setPronounType(pronounType);
	}
	
	private void initMultipleMention(){
		setGenderType(l_subMentions.get(0).getGenderType());
		setNumberType(NumberType.PLURAL);
		
		AbstractMention mention;
		int i, size = l_subMentions.size();
		ObjectIntHashMap<EntityType> entityMap = new ObjectIntHashMap<>();
		for(i = 0; i < size; i++){
			mention = l_subMentions.get(i);
			entityMap.add(mention.getEntityType());
			if(!isGenderType(GenderType.NEUTRAL) && !isGenderType(mention.getGenderType()))
				setGenderType(GenderType.NEUTRAL);
		}
		setEntityType(Collections.max(entityMap.toList()).o);
	}
	
	/* Getters */
	public int getTreeId(){
		return treeId;
	}
	
	public DEPTree getTree(){
		return d_tree;
	}
	
	public DEPNode getNode(){
		return d_node;
	}
	
	public DEPNode getHeadNode(){
		return d_node.getHead();
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
	
	public List<AbstractMention> getSubMentions(){
		return l_subMentions;
	}
	
	public List<DEPNode> getSubTreeNodes(){
		return l_subNodes;
	}
	
	/* Setters */
	public void setTreeId(int t_id){
		treeId = t_id;
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
	
	public void setSubTreeNodes(List<DEPNode> nodes){
		l_subNodes = nodes;
	}
	
	public void setSubMentions(List<AbstractMention> mentions){
		l_subMentions = mentions;
		if(l_subMentions != null && !hasFeature(AttributeType.CONJUNCTION)) 
			addAttribute(AttributeType.CONJUNCTION);
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
	
	public boolean isMultipleMention(){
		return l_subMentions != null && l_subMentions.size() > 1;
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
		return (getSubTreeNodes() == null)? false : getSubTreeNodes().contains(mention.getNode());
	}
	
	public boolean isChildMention(AbstractMention mention){
		return (mention.getSubTreeNodes() == null)? false :  mention.getSubTreeNodes().contains(getNode());
	}
	
	public boolean matchEntityType(AbstractMention mention){
		if(isNameEntity() && mention.isNameEntity())	return getEntityType() == mention.getEntityType() && getSubTreeWordSequence().equals(mention.getSubTreeWordSequence());
		else if(isNameEntity())							return mention.isPronounType(PronounType.SUBJECT) || mention.isPronounType(PronounType.OBJECT) || mention.isPronounType(PronounType.POSSESSIVE);
		else if(mention.isNameEntity())					return isPronounType(PronounType.SUBJECT) || isPronounType(PronounType.OBJECT) || isPronounType(PronounType.POSSESSIVE);
		else if(isEntityType(EntityType.COMMON))		return !(getEntityType() == mention.getEntityType());
		return !isEntityType(EntityType.UNKNOWN) || getEntityType() == mention.getEntityType();
	}
	
	public boolean matchNumberType(AbstractMention mention){
		return mention.isNumberType(getNumberType());
	}
	
	public boolean matchGenderType(AbstractMention mention){
		return mention.isGenderType(getGenderType()) || isGenderType(GenderType.NEUTRAL) || mention.isGenderType(GenderType.NEUTRAL);
	}
	
	public boolean hasSameHeadNode(AbstractMention mention){
		return getNode().getHead() == mention.getNode().getHead();
	}
	
	public boolean hasFeature(AttributeType type){
		return m_attr.containsKey(type);
	}
	
	/* String handling methods */
	public String getWordFrom(){
		return d_node.getWordForm();
	}
	
	public String getLemma(){
		return d_node.getLemma();
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
	abstract public List<String> getModifiersList();
	abstract public boolean isInAdjunctDomainOf(AbstractMention mention);
	
	public Set<String> getModifiersSet(){
		return new HashSet<>(getModifiersList());
	}
	
	@Override
	public String toString(){
		StringJoiner joiner = new StringJoiner("\t");
		
		if(l_subMentions == null)
			joiner.add(getWordFrom() + StringConst.AT + getTreeId() + StringConst.PERIOD + getNode().getID());
		else
			joiner.add(Joiner.join(l_subMentions.stream().map(m -> m.getWordFrom()).collect(Collectors.toList()), StringConst.COMMA));
		
		joiner.add((t_entity == null)? StringConst.UNDERSCORE : t_entity.toString());
		joiner.add((t_gender == null)? StringConst.UNDERSCORE : t_gender.toString());
		joiner.add((t_number == null)? StringConst.UNDERSCORE : t_number.toString());
		joiner.add((t_pronoun == null)? StringConst.UNDERSCORE : t_pronoun.toString());
		joiner.add(m_attr.toString());
		return joiner.toString();
	}
}
