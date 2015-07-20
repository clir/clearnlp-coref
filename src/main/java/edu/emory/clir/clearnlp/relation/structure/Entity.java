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
import edu.emory.clir.clearnlp.relation.utils.RelationExtractionTreeUtil;
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
	
	private String s_Tag;
	private double d_confidence;
	private List<EntityAlias> l_alias;
	
	public Entity(int sentenceId,  DEPNode head, List<DEPNode> nodes){
		l_alias = new ArrayList<>();
		set(sentenceId, head, nodes, null);
	}
	
	public Entity(int sentenceId,  DEPNode head, List<DEPNode> nodes, String tag){
		l_alias = new ArrayList<>();
		set(sentenceId, head, nodes, tag);
	}
	
	public void set(int sentenceId, DEPNode head, List<DEPNode> nodes, String Tag){
		l_alias.add(0, new EntityAlias(sentenceId, head, nodes, 1d));
		s_Tag = Tag;
	}
	
	public EntityAlias getFirstAlias(){
		return l_alias.get(0);
	}
	
	public List<DEPNode> getNodes(){
		return l_alias.get(0).getNodes();
	}
	
	public String getTag(){
		return s_Tag;
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

	public boolean addAlias(int sentenceId, Chunk chunk){
		double aliasWeight;
		for(EntityAlias alias : l_alias){
			if( (aliasWeight = getAliasLiklihood(alias.getNodes(), chunk.getChunkNodes())) > 0){
				l_alias.add(new EntityAlias(sentenceId, chunk.getHeadNode(), chunk.getChunkNodes(), aliasWeight));
				return true;
			}
		}
		return false;
	}
	
	private double getAliasLiklihood(List<DEPNode> alias_nodes, List<DEPNode> curr_nodes){
		String string1, string2;
		List<DEPNode> alias = new ArrayList<>(alias_nodes), nodes = new ArrayList<>(curr_nodes);
		
		// Match exact string
		string1 = Joiner.join(alias.stream().map(n -> n.getWordForm()).collect(Collectors.toList()), StringConst.SPACE);
		string2 = Joiner.join(nodes.stream().map(n -> n.getWordForm()).collect(Collectors.toList()), StringConst.SPACE);
		if(string1.equals(string2)) return 1d;
		
		// Match match relaxed string
		RelationExtractionTreeUtil.stripSubTree(alias); 
		RelationExtractionTreeUtil.stripSubTree(nodes);
		string1 = Joiner.join(alias.stream().map(n -> n.getWordForm()).collect(Collectors.toList()), StringConst.SPACE);
		string2 = Joiner.join(nodes.stream().map(n -> n.getWordForm()).collect(Collectors.toList()), StringConst.SPACE);
		if(string1.equals(string2)) return w_alias;
		
		return 0d;	
	}
	
	@Override
	public int compareTo(Entity o) {
		return (int)Math.signum(getEntityConfidence() - o.getEntityConfidence());
	}

	@Override
	public Iterator<EntityAlias> iterator() {
		return l_alias.iterator();
	}
	
	@Override
	public String toString(){
		
		List<DEPNode> subTree = new ArrayList<>(getNodes());
		RelationExtractionTreeUtil.stripSubTree(subTree);
		
		String word = Joiner.join(subTree.stream().map(n -> n.getWordForm()).collect(Collectors.toList()), StringConst.SPACE);
		StringBuilder sb = new StringBuilder(word);
		
		sb.append(StringConst.LRB);
		sb.append(s_Tag);
		sb.append(StringConst.RRB);
		sb.append(StringConst.TAB);
		sb.append(getCount());
		sb.append(StringConst.TAB);
		sb.append(getEntityConfidence());
		
		return sb.toString();
	}
}
