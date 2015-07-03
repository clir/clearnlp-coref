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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.ner.NERLib;
import edu.emory.clir.clearnlp.pos.POSLibEn;
import edu.emory.clir.clearnlp.relation.entity.EntityFrequencyRanker;
import edu.emory.clir.clearnlp.util.Joiner;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 2, 2015
 */
public class EntityCountEntry extends ObjectIntPair<DEPNode>{
	private static final long serialVersionUID = 6011313908928592982L;
	
	private List<DEPNode> l_alias;
	private String s_NERTag;
	private List<DEPNode> l_entityNode;
	
	public EntityCountEntry(DEPNode node){
		l_alias = new ArrayList<>();
		l_entityNode = new ArrayList<>();
		set(node, 1);
		s_NERTag = NERLib.toNamedEntity(node.getNamedEntityTag());
	}
	
	public DEPNode getNode(){
		return this.o;
	}
	
	public String getNERTag(){
		return s_NERTag;
	}
	
	public List<DEPNode> getEntityNodes(){
		return l_entityNode;
	}
	
	public int getCount(){
		return this.i;
	}
	
	public List<DEPNode> getAliasList(){
		return l_alias;
	}
	
	public void incrementCount(){
		this.i++;
	}
	
	public void incrementCount(int count){
		this.i += count;
	}
	
	public void addAlias(DEPNode node){
		l_alias.add(node);
		incrementCount();
	}
	
	public boolean hasAlias(DEPNode node){
		if(!matchAliasRules(this.o, node)){
			for(DEPNode n : l_alias)
				if(matchAliasRules(n ,node)) return true;
			return false;
		}
		return true;
	}
	
	private boolean matchAliasRules(DEPNode node1, DEPNode node2){
		String string1, string2;
		List<DEPNode> l_nodes1 = node1.getSubNodeList(), l_nodes2 = node2.getSubNodeList();
		
		// Match exact string
		string1 = Joiner.join(l_nodes1.stream().map(node -> node.getWordForm()).collect(Collectors.toList()), StringConst.SPACE);
		string2 = Joiner.join(l_nodes2.stream().map(node -> node.getWordForm()).collect(Collectors.toList()), StringConst.SPACE);
		if(string1.equals(string2)) return true;
		
		// Match match relaxed string
		stripSubTree(l_nodes1, node1); stripSubTree(l_nodes2, node2);
		string1 = Joiner.join(l_nodes1.stream().map(node -> node.getWordForm()).collect(Collectors.toList()), StringConst.SPACE);
		string2 = Joiner.join(l_nodes2.stream().map(node -> node.getWordForm()).collect(Collectors.toList()), StringConst.SPACE);
		if(string1.equals(string2)) return true;
		
		// Match only nouns (NN, NNS, NNP, NNPS)
//		l_nodes1 = l_nodes1.stream().filter(node -> POSLibEn.isCommonOrProperNoun(node.getPOSTag())).collect(Collectors.toList());
//		l_nodes2 = l_nodes2.stream().filter(node -> POSLibEn.isCommonOrProperNoun(node.getPOSTag())).collect(Collectors.toList());
		
		return false;	
	}
	
	private void stripSubTree(List<DEPNode> l_subNodes, DEPNode node){
		removeSelectedDEPLabels(l_subNodes, node);
		removeModifiers(l_subNodes, node);
	}
	
	private void removeSelectedDEPLabels(List<DEPNode> l_subNodes, DEPNode node){
		List<DEPNode> nodes = node.getDependentListByLabel(EntityFrequencyRanker.ignoredDEPLabels);
		if(nodes != null) for(DEPNode n : nodes) l_subNodes.removeAll(n.getSubNodeSet());
	}
	
	private void removeModifiers(List<DEPNode> l_subNodes, DEPNode node){
		List<DEPNode> modifiers = l_subNodes.stream().filter(n -> n.getLabel().endsWith(EntityFrequencyRanker.DEPModSuffix) || POSLibEn.isAdjective(n.getPOSTag())).collect(Collectors.toList());
		if(modifiers != null) for(DEPNode modifier : modifiers) l_subNodes.removeAll(modifier.getSubNodeSet());
	}
	
	@Override
	public String toString(){
		String word = Joiner.join(this.o.getSubNodeList().stream().map(node -> node.getWordForm()).collect(Collectors.toList()), StringConst.SPACE);
		return word + StringConst.LRB + s_NERTag + StringConst.RRB + StringConst.TAB + this.i;
	}
}
