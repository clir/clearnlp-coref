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
package edu.emory.clir.clearnlp.relation.component.entity;

import java.util.List;
import java.util.Set;

import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.dependency.DEPLibEn;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.relation.feature.MainEntityComponentLabel;
import edu.emory.clir.clearnlp.relation.feature.MainEntityFeatureIndex;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.relation.structure.Entity;
import edu.emory.clir.clearnlp.relation.structure.EntityAlias;
import edu.emory.clir.clearnlp.tokenization.AbstractTokenizer;
import edu.emory.clir.clearnlp.tokenization.EnglishTokenizer;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 15, 2015
 */
public class MainEntityIdentificationFeatureExtractor implements MainEntityFeatureIndex, MainEntityComponentLabel{
	private final String MOD_SUFFIX = "mod";
	private final boolean DECAPITALIZE = true;
	private final Set<String> ignoredDEPLabels = DSUtils.toHashSet(DEPLibEn.DEP_APPOS, DEPLibEn.DEP_RELCL, DEPLibEn.DEP_POSS); 
	
	public StringFeatureVector getVector(Document document, Entity entity){
		String wordForm; DEPTree tree;
		List<DEPNode> subNodes, aliasNodes;
		StringFeatureVector vector = new StringFeatureVector();
		
		// **Document information
		// Document tree count
		vector.addFeature(DocumentInfo, Integer.toString(document.getTreeCount()));
		// Document entity count
		vector.addFeature(DocumentInfo, Integer.toString(document.getEntities().size()));
		
		// **Entity information
		// Entity tag given by the chunker
		vector.addFeature(EntityTag, entity.getTag());
		
		// **Entity alias information
		for(EntityAlias alias : entity){
			aliasNodes = alias.getNodes();
			tree = document.getTree(alias.getSentenceId());
			
			// *Alias information
			// Alias sentence Id
			vector.addFeature(AliasSentenceId, Integer.toString(alias.getSentenceId()));
			
			// Alias word form
			vector.addFeature(AliasWordForm, alias.getWordForm(DECAPITALIZE));
								
			// Alias stripped word form
			wordForm = alias.getStippedWordForm(DECAPITALIZE);
			vector.addFeature(AliasStrippedWordForm, wordForm);
			
			// Alias stripped word form match with title
			if(document.getTitle().contains(wordForm))
				vector.addFeature(DocumentTiltleTokenMatch, TRUE);
			
			// *Alias head information
			// Node Id of the alias head
			vector.addFeature(AliasHeadNodeId, Integer.toString(alias.getHeadNode().getID()));
			
			// Distance from predicate of the alias head
			vector.addFeature(AliasHeadNodeRank, Integer.toString(getDistanceFromPredicate(tree, alias.getHeadNode())));
			
			// *Sub nodes (excluding chunk nodes) DEP labels of head node
			subNodes = alias.getHeadNode().getSubNodeList();	subNodes.removeAll(aliasNodes);
			for(DEPNode node : subNodes){
				if(ignoredDEPLabels.contains(node.getLabel())) vector.addFeature(AliasHeadSubNodeIgnoredDEPLabels, node.getLabel());
			}
			
			// *Alias chunk (subNodes) information
			for(DEPNode node : aliasNodes){
				// POS tags of chunk nodes
				vector.addFeature(AliasChunkPOSTags, node.getPOSTag());
				
				// DEP labels of chunk nodes
				vector.addFeature(AliasChunkDEPLabels, node.getLabel());
				
				// NER tags of chunk nodes
				vector.addFeature(AliasChunkNERTags, node.getNamedEntityTag());
				
				// Modifier in the chunk
				if(node.getLabel().endsWith(MOD_SUFFIX))
					vector.addFeature(AliasChunkModifiers, node.getLabel());
			}
			
			// *Sentence information
			for(DEPNode node : tree){
				// POS tags of where each sentence a entity alias is located
				vector.addFeature(SentencePOSTags, node.getPOSTag());
				
				// DEP labels of where each sentence a entity alias is located
				vector.addFeature(SentenceDEPLables, node.getLabel());
			}
		}
				
		return vector;
	}
	
	private int getDistanceFromPredicate(DEPTree tree, DEPNode node){
		int count = 0; List<DEPNode> roots = tree.getRoots();
		for(; !roots.contains(node); count++, node = node.getHead());
		return count;
	}
}
