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
package edu.emory.clir.clearnlp.relation.chunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.ner.BILOU;
import edu.emory.clir.clearnlp.ner.NERLib;
import edu.emory.clir.clearnlp.relation.structure.Chunk;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 8, 2015
 */
public class EnglishNamedEntityChunker extends AbstractChucker{	
	private Set<String> s_extactingNETags;

	public EnglishNamedEntityChunker() {
		super(TLanguage.ENGLISH);
	}
	
	public EnglishNamedEntityChunker(Set<String> extactingNETags) {
		super(TLanguage.ENGLISH);
		s_extactingNETags = extactingNETags;
	}
	
	@Override
	public List<Chunk> getChunk(DEPTree tree){
		return getNamedEntityChunks(tree);
	}
	
	protected List<Chunk> getNamedEntityChunks(DEPTree tree){
		List<Chunk> chunks = new ArrayList<>();
		
		DEPNode head; BILOU BILOUTag;
		List<DEPNode> subNodes = new ArrayList<>();
		for(DEPNode node : tree){
			if(p_chunkNode.test(node)){
				BILOUTag = NERLib.toBILOU(node.getNamedEntityTag());
				switch(BILOUTag){						
					case L:
						subNodes.add(node); head = getNEChunkHead(subNodes);
						chunks.add(new Chunk(f_chunkLable.apply(head), head, subNodes));
						subNodes = new ArrayList<>();
						break;
					case U:
						subNodes.add(node);
						chunks.add(new Chunk(f_chunkLable.apply(node), node, subNodes));
						subNodes = new ArrayList<>();
						break;
					default:
						subNodes.add(node);
				}
			}
		}
		
		return chunks;
	}
	
	protected DEPNode getNEChunkHead(List<DEPNode> subNodes){
		int index;
		for(DEPNode node : subNodes){
			index = Collections.binarySearch(subNodes, node.getHead());
			if(index < 0) return node;
		}
		return null;
	}
	
	@Override
	protected Function<DEPNode, String> getLabelFunction() {
		return new Function<DEPNode, String>() {

			@Override
			public String apply(DEPNode t) {
				return NERLib.toNamedEntity(t.getNamedEntityTag());
			}
		};
	}

	@Override
	protected Predicate<DEPNode> getChunkingNodePredicate() {
		return new Predicate<DEPNode>() {
			String ignoredBILOUTag = BILOU.O.toString();
			
			@Override
			public boolean test(DEPNode t) {
				String NERTag = t.getNamedEntityTag();
				if(s_extactingNETags == null)
					return !NERTag.equals(ignoredBILOUTag);
				return !NERTag.equals(ignoredBILOUTag) && s_extactingNETags.contains(NERLib.toNamedEntity(NERTag));
			}
		};
	}
}
