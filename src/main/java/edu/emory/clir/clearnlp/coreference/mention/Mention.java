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

import edu.emory.clir.clearnlp.collection.map.ObjectDoubleHashMap;
import edu.emory.clir.clearnlp.constituent.CTLibEn;
import edu.emory.clir.clearnlp.coreference.type.MentionAttributeType;
import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.coreference.type.GenderType;
import edu.emory.clir.clearnlp.coreference.type.NumberType;
import edu.emory.clir.clearnlp.coreference.type.PronounType;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Mention implements Serializable
{
	private static final long serialVersionUID = -3899758740140875733L;
	
	private DEPTree    d_tree;
	private DEPNode    d_node;
	
	private EntityType t_entity;
	private	GenderType t_gender;
	private NumberType t_number;
	private PronounType t_pronoun;
	
	private ObjectDoubleHashMap<MentionAttributeType> m_attr;
	
	public Mention(DEPTree tree, DEPNode node)
	{
		setTree(tree);
		setNode(node);
		setEntityType(EntityType.UNKNOWN);
		setGenderType(GenderType.UNKNOWN);
		setNumberType(NumberType.UNKNOWN);
		setPronounType(null);
		m_attr = new ObjectDoubleHashMap<>();
	}
	
	public Mention(DEPTree tree, DEPNode node, EntityType entityType)
	{
		setTree(tree);
		setNode(node);
		setEntityType(entityType);
		setGenderType(GenderType.UNKNOWN);
		setNumberType(NumberType.UNKNOWN);
		if(entityType == EntityType.PRONOUN)	setPronounType(PronounType.UNKNOWN);
		else									setPronounType(null);
		m_attr = new ObjectDoubleHashMap<>();
	}
	
	public Mention(DEPTree tree, DEPNode node, NumberType numberType)
	{
		setTree(tree);
		setNode(node);
		setEntityType(EntityType.UNKNOWN);
		setGenderType(GenderType.UNKNOWN);
		setNumberType(numberType);
		setPronounType(null);
		m_attr = new ObjectDoubleHashMap<>();
	}
	
	public Mention(DEPTree tree, DEPNode node, EntityType entityType, GenderType genderType)
	{
		setTree(tree);
		setNode(node);
		setEntityType(entityType);
		setGenderType(genderType);
		setNumberType(NumberType.UNKNOWN);
		if(entityType == EntityType.PRONOUN)	setPronounType(PronounType.UNKNOWN);
		else									setPronounType(null);
		m_attr = new ObjectDoubleHashMap<>();
	}
	
	public Mention(DEPTree tree, DEPNode node, EntityType entityType, NumberType numberType)
	{
		setTree(tree);
		setNode(node);
		setEntityType(entityType);
		setGenderType(GenderType.UNKNOWN);
		setNumberType(numberType);
		if(entityType == EntityType.PRONOUN)	setPronounType(PronounType.UNKNOWN);
		else									setPronounType(null);
		m_attr = new ObjectDoubleHashMap<>();
	}
	
	public Mention(DEPTree tree, DEPNode node, EntityType entityType, GenderType genderType, NumberType numberType, PronounType pronounType)
	{
		setTree(tree);
		setNode(node);
		setEntityType(entityType);
		setGenderType(genderType);
		setNumberType(numberType);
		setPronounType(pronounType);
		m_attr = new ObjectDoubleHashMap<>();
	}

	public DEPTree getTree()
	{
		return d_tree;
	}
	
	public DEPNode getNode()
	{
		return d_node;
	}
	
	public EntityType getEntityType()
	{
		return t_entity;
	}
	
	public GenderType getGenderType()
	{
		return t_gender;
	}
	
	public NumberType getNumberType()
	{
		return t_number;
	}
	
	public PronounType getPronounType()
	{
		return t_pronoun;
	}
	
	public double getAttribute(MentionAttributeType type)
	{
		return m_attr.get(type);
	}
	
	public ObjectDoubleHashMap<MentionAttributeType> getFeatureMap()
	{
		return m_attr;
	}
	
	public void setTree(DEPTree tree)
	{
		d_tree = tree;
	}
	
	public void setNode(DEPNode node)
	{
		d_node = node;
	}
	
	public void setEntityType(EntityType type)
	{
		t_entity = type;
	}
	
	public void setGenderType(GenderType type)
	{
		t_gender = type;
	}
	
	public void setNumberType(NumberType type)
	{
		t_number = type;
	}
	
	public void setPronounType(PronounType type)
	{
		t_pronoun = type;
	}
	
	public void addAttribute(MentionAttributeType type)
	{
		m_attr.add(type, 1);
	}
	
	public void addAttribute(MentionAttributeType type, double weight)
	{
		m_attr.add(type, weight);
	}
	
	public boolean isEntityType(EntityType type)
	{
		return t_entity == type;
	}
	
	public boolean isGenderType(GenderType type)
	{
		return t_gender == type;
	}
	
	public boolean isNumberType(NumberType type)
	{
		return t_number == type;
	}
	
	public boolean isPronounType(PronounType type)
	{
		return t_pronoun == type;
	}
	
	public boolean hasFeature(MentionAttributeType type){
		return m_attr.containsKey(type);
	}
	
	@Override
	public String toString(){
		String wordfrom = (!d_node.isPOSTag(CTLibEn.POS_CD))? d_node.getLemma() : d_node.getWordForm();
		return wordfrom + "\t" + t_entity + "\t" + t_gender + '\t' + t_number + "\t" + t_pronoun;
		
	}
}
