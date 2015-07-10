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
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.collection.triple.Triple;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.relation.utils.RelationExtractionTreeUtil;
import edu.emory.clir.clearnlp.util.Joiner;
import edu.emory.clir.clearnlp.util.StringUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 8, 2015
 */
public class Chunk extends Triple<String, DEPNode, List<DEPNode>> implements Comparable<Chunk>, Iterable<DEPNode>{
	private static final long serialVersionUID = 1473156168279114903L;

	private String s_wordFrom;
	private String s_strippedWordFrom;
	private List<DEPNode> l_strippedChunkNodes;
	
	public Chunk(String o1, DEPNode o2, List<DEPNode> o3) {
		super(o1, o2, o3);
		
		l_strippedChunkNodes = RelationExtractionTreeUtil.stripSubTree(new ArrayList<>(o3));
		s_wordFrom = Joiner.join(o3.stream().map(DEPNode::getWordForm).collect(Collectors.toList()), StringConst.SPACE).trim();
		s_strippedWordFrom = Joiner.join(l_strippedChunkNodes.stream().map(DEPNode::getWordForm).collect(Collectors.toList()), StringConst.SPACE).trim();
		s_strippedWordFrom = StringUtils.collapsePunctuation(s_strippedWordFrom);
	}
	
	public String getTag(){
		return o1;
	}
	
	public String getWordForm(boolean decapitalize){
		if(decapitalize)	
			return StringUtils.toLowerCase(s_wordFrom);
		return s_wordFrom;
	}
	
	public String getStrippedWordForm(boolean decapitalize){
		if(decapitalize) 
			return StringUtils.toLowerCase(s_strippedWordFrom);
		return s_strippedWordFrom;
	}
	
	public DEPNode getHeadNode(){
		return o2;
	}
	
	public List<DEPNode> getChunkNodes(){
		return o3;
	}
	
	public List<DEPNode> getStrippedChunkNodes(){
		return l_strippedChunkNodes;
	}
	
	public Entity toEnity(){
		return new Entity(-1, getHeadNode(), getChunkNodes(), getTag());
	}

	@Override
	public int compareTo(Chunk o) {
		return o2.compareTo(o.o2);
	}

	@Override
	public Iterator<DEPNode> iterator() {
		return o3.iterator();
	}
	
	@Override
	public String toString(){
		String chunk = Joiner.join(getChunkNodes().stream().map(DEPNode::getWordForm).collect(Collectors.toList()), StringConst.SPACE);
		return getTag() + StringConst.TAB + chunk;
	}
}
