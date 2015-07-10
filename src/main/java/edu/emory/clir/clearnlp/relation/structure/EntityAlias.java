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
import java.util.List;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.relation.utils.RelationExtractionTreeUtil;
import edu.emory.clir.clearnlp.util.Joiner;
import edu.emory.clir.clearnlp.util.StringUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 5, 2015
 */
public class EntityAlias implements Serializable{
	private static final long serialVersionUID = -7908795263358761403L;
	
	private int i_sentenceId;
	private DEPNode n_head;
	private List<DEPNode> n_nodes;
	private double d_weight;
	
	public EntityAlias(int sentenceId, DEPNode head, List<DEPNode> nodes){
		i_sentenceId = sentenceId;
		n_head = head;
		n_nodes = nodes;
	}
	
	public EntityAlias(int sentenceId, DEPNode head, List<DEPNode> nodes, double weight){
		i_sentenceId = sentenceId;
		n_head = head;
		n_nodes = nodes;
		setWeight(weight);
	}
	
	public int getSentenceId(){
		return i_sentenceId;
	}
	
	public DEPNode getHeadNode(){
		return n_head;
	}
	
	public List<DEPNode> getNodes(){
		return n_nodes;
	}
	
	public String getWordForm(boolean decapitalize){
		if(decapitalize)
			return Joiner.join(n_nodes.stream().map(DEPNode::getLowerSimplifiedWordForm).collect(Collectors.toList()), StringConst.SPACE).trim();
		return Joiner.join(n_nodes.stream().map(DEPNode::getWordForm).collect(Collectors.toList()), StringConst.SPACE).trim();
	}
	
	public String getStippedWordForm(boolean decapitalize){
		String wordForm;
		List<DEPNode> nodes = RelationExtractionTreeUtil.stripSubTree(new ArrayList<>(n_nodes));

		if(decapitalize) wordForm = Joiner.join( nodes.stream().map(DEPNode::getLowerSimplifiedWordForm).collect(Collectors.toList()), StringConst.SPACE).trim();
		else			 wordForm = Joiner.join( nodes.stream().map(DEPNode::getWordForm).collect(Collectors.toList()), StringConst.SPACE).trim();
		return StringUtils.collapsePunctuation(wordForm);
	}
	
	public double getWeight(){
		return d_weight;
	}
	
	public void setWeight(double weight){
		d_weight= weight;
	}
	
	@Override
	public String toString(){
		return i_sentenceId + StringConst.TAB + getWordForm(false) + StringConst.TAB + getWeight(); 
	}
}
