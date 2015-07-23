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
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.dependency.DEPLibEn;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.pos.POSLibEn;
import edu.emory.clir.clearnlp.relation.structure.Chunk;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 7, 2015
 */
public abstract class AbstractChucker {
	public static final Set<String> ignorePOSTags = DSUtils.toHashSet(POSLibEn.POS_DT, POSLibEn.POS_IN);
	
	protected TLanguage l_language;
	protected Function<DEPNode, String> f_chunkLable;
	protected Predicate<DEPNode> p_chunkNode;
	
	protected final Predicate<DEPNode> p_defualtIgnoredNodes = new Predicate<DEPNode>() {
		
		@Override
		public boolean test(DEPNode t) {
			return !ignorePOSTags.contains(t.getPOSTag()) &&
					!t.isLabel(DEPLibEn.DEP_COMPOUND);
		}
	};
	
	public AbstractChucker(TLanguage language){
		l_language = language;
		f_chunkLable = getLabelFunction();
		p_chunkNode = getChunkingNodePredicate();
	}
	
	public AbstractChucker(TLanguage language, Function<DEPNode, String> label_function, Predicate<DEPNode> chunking_function){
		l_language = language;
		f_chunkLable = label_function;
		p_chunkNode = chunking_function;
	}
	
	public List<Chunk> getChunk(DEPTree tree){
		List<DEPNode> candidates = getChunkNodeCandidates(tree);
		List<Chunk> chunks = new ArrayList<>();
		
		DEPNode candidate; List<DEPNode> subNodes;
		while(!candidates.isEmpty()){
			candidate = getSubTreeRoot(candidates.get(0));
			subNodes = getSubTreeChunk(candidate, candidate.getSubNodeList());
			
			chunks.add(new Chunk(f_chunkLable.apply(candidate), candidate, subNodes));
			candidates.removeAll(subNodes);
		}
		return chunks;
	}
	
	public List<Chunk> getChunks(List<DEPTree> trees){
		List<Chunk> list = new ArrayList<>();
		for(DEPTree tree : trees) list.addAll(getChunk(tree));
		return list;
	}
	
	abstract protected Function<DEPNode, String> getLabelFunction();
	abstract protected Predicate<DEPNode> getChunkingNodePredicate();
	
	protected DEPNode getSubTreeRoot(DEPNode node){
		if( node.getHead().isLabel(DEPLibEn.DEP_ROOT)
			|| !p_chunkNode.test(node.getHead())
			|| !node.isLabel(DEPLibEn.DEP_COMPOUND))	
			return node;
		return getSubTreeRoot(node.getHead());
	}
	
	protected List<DEPNode> getSubTreeChunk(DEPNode head, List<DEPNode> subNodes){
		int start, end, headId = head.getID();
		int headIndex = subNodes.indexOf(head), size = subNodes.size();
		
		int offset; DEPNode node;
		for(start = headIndex - 1, offset = 1; start >= 0; start--){
			node = subNodes.get(start);
			if(!node.isLabel(DEPLibEn.DEP_COMPOUND) &&
				headId - node.getID() != offset++) break;
		}
		for(end = headIndex + 1, offset = 1; end < size; end++){
			node = subNodes.get(end);
			if(!node.isLabel(DEPLibEn.DEP_COMPOUND) &&
				node.getID() - headId != offset++) break;
		}
		
		return subNodes.subList(start+1, end);
	}
	
	protected List<DEPNode> getChunkNodeCandidates(DEPTree tree){
		return DSUtils.toArrayList(tree.toNodeArray()).stream().filter(p_defualtIgnoredNodes.and(p_chunkNode)).collect(Collectors.toList());
	}
}
