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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
public class EnglishProperNounChunker extends AbstractChucker {

	private EnglishNamedEntityChunker NE_chunker;
	
	public EnglishProperNounChunker(){
		super(TLanguage.ENGLISH);
		NE_chunker = new EnglishNamedEntityChunker();
	}
	
	public EnglishProperNounChunker(Set<String> extactingNETags){
		super(TLanguage.ENGLISH);
		NE_chunker = new EnglishNamedEntityChunker(extactingNETags);
	}
	
	@Override
	public List<Chunk> getChunk(DEPTree tree){
		List<DEPNode> candidates = getChunkNodeCandidates(tree);
		
		// Remove NE chunks
		List<Chunk> chunks = NE_chunker.getChunk(tree);
		for(Chunk chunk : chunks)	candidates.removeAll(chunk.getChunkNodes());
		
		Chunk chunk;
		DEPNode candidate; List<DEPNode> subNodes;
		
		while(!candidates.isEmpty()){
			candidate = getSubTreeRoot(candidates.get(0));
			subNodes = getSubTreeChunk(candidate, candidate.getSubNodeList());
			
			chunk = new Chunk(f_chunkLable.apply(candidate), candidate, subNodes);
			if(!chunk.getStrippedChunkNodes().isEmpty())	chunks.add(chunk);
			candidates.removeAll(subNodes);
		}
		
		Collections.sort(chunks);
		return chunks;
	}
	
	@Override
	protected Function<DEPNode, String> getLabelFunction() {
		return new Function<DEPNode, String>(){
			
			@Override
			public String apply(DEPNode t) {
				return t.getPOSTag();
			}
		};
	}

	@Override
	protected Predicate<DEPNode> getChunkingNodePredicate() {
		return new Predicate<DEPNode>(){

			@Override
			public boolean test(DEPNode t) {
				return t.getPOSTag().startsWith(POSLibEn.POS_NNP);
			}
		};
	}
}
