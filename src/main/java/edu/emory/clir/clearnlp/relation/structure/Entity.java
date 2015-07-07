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
package edu.emory.clir.clearnlp.relation.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.ner.NERLib;
import edu.emory.clir.clearnlp.pos.POSLibEn;
import edu.emory.clir.clearnlp.util.Joiner;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 2, 2015
 */
public class Entity implements Serializable, Iterable<EntityAlias>, Comparable<Entity>{
	private static final long serialVersionUID = 6011313908928592982L;
	public static final double w_alias = 0.8d;
	
	private String s_NERTag;
	private double d_confidence;
	private List<EntityAlias> l_alias;
	
	public Entity(int sentenceId, DEPNode node){
		l_alias = new ArrayList<>();
		set(sentenceId, node, NERLib.toNamedEntity(node.getNamedEntityTag()));
	}
	
	public void set(int sentenceId, DEPNode node, String NERTag){
		l_alias.add(0, new EntityAlias(sentenceId, node, 1d));
		s_NERTag = NERTag;
	}
	
	public EntityAlias getFirstAlias(){
		return l_alias.get(0);
	}
	
	public DEPNode getNode(){
		return l_alias.get(0).getNode();
	}
	
	public String getNERTag(){
		return s_NERTag;
	}
	
	public int getCount(){
		return l_alias.size();
	}
	
	public double getEntityConfidence(){
		return d_confidence;
	}
	
	public void setEntityConfidence(double confidence){
		d_confidence = confidence;
	}
	
	public double getAliasConfidence(){
		return l_alias.stream().mapToDouble(EntityAlias::getWeight).average().getAsDouble();
	}
	
	public List<EntityAlias> getAliasList(){
		return l_alias;
	}

	public boolean addAlias(int sentenceId, DEPNode node){
		double aliasWeight;
		for(EntityAlias alias : l_alias){
			if( (aliasWeight = getAliasLiklihood(alias.getNode() , node)) > 0){
				l_alias.add(new EntityAlias(sentenceId, node, aliasWeight));
				return true;
			}
		}
		return false;
	}
	
	private double getAliasLiklihood(DEPNode alias, DEPNode node){
		String string1, string2;
		List<DEPNode> l_nodes1 = alias.getSubNodeList(), l_nodes2 = node.getSubNodeList();
		
		// Match exact string
		string1 = Joiner.join(l_nodes1.stream().map(n -> n.getWordForm()).collect(Collectors.toList()), StringConst.SPACE);
		string2 = Joiner.join(l_nodes2.stream().map(n -> n.getWordForm()).collect(Collectors.toList()), StringConst.SPACE);
		if(string1.equals(string2)) return 1d;
		
		// Match match relaxed string
		stripSubTree(l_nodes1, alias); stripSubTree(l_nodes2, node);
		string1 = Joiner.join(l_nodes1.stream().map(n -> n.getWordForm()).collect(Collectors.toList()), StringConst.SPACE);
		string2 = Joiner.join(l_nodes2.stream().map(n -> n.getWordForm()).collect(Collectors.toList()), StringConst.SPACE);
		if(string1.equals(string2)) return w_alias;
		
		return 0d;	
	}
	
	private void stripSubTree(List<DEPNode> l_subNodes, DEPNode node){
		removeSelectedDEPLabels(l_subNodes, node);
		removeModifiers(l_subNodes, node);
	}
	
	private void removeSelectedDEPLabels(List<DEPNode> l_subNodes, DEPNode node){
		List<DEPNode> nodes = node.getDependentListByLabel(Document.ignoredDEPLabels);
		if(nodes != null) for(DEPNode n : nodes) l_subNodes.removeAll(n.getSubNodeSet());
	}
	
	private void removeModifiers(List<DEPNode> l_subNodes, DEPNode node){
		List<DEPNode> modifiers = l_subNodes.stream().filter(n -> n.getLabel().endsWith(Document.DEPModSuffix) || POSLibEn.isAdjective(n.getPOSTag())).collect(Collectors.toList());
		if(modifiers != null) for(DEPNode modifier : modifiers) l_subNodes.removeAll(modifier.getSubNodeSet());
	}

	@Override
	public int compareTo(Entity o) {
		return (int)Math.signum(getEntityConfidence() - o.getEntityConfidence());
	}

	@Override
	public Iterator<EntityAlias> iterator() {
		Iterator<EntityAlias> it = new Iterator<EntityAlias>() {
			int i = 0, size = l_alias.size();
			@Override
			public EntityAlias next() {
				return l_alias.get(i++);
			}
			
			@Override
			public boolean hasNext() {
				return i < size;
			}
		};
		return it;
	}
	
	@Override
	public String toString(){
		
		DEPNode node = getNode();
		List<DEPNode> subTree = node.getSubNodeList();
		stripSubTree(subTree, node);
		
		String word = Joiner.join(subTree.stream().map(n -> n.getWordForm()).collect(Collectors.toList()), StringConst.SPACE);
		StringBuilder sb = new StringBuilder(word);
		
		sb.append(StringConst.LRB);
		sb.append(s_NERTag);
		sb.append(StringConst.RRB);
		sb.append(StringConst.TAB);
		sb.append(getCount());
		sb.append(StringConst.TAB);
		sb.append(getEntityConfidence());
		
		return sb.toString();
	}
}
