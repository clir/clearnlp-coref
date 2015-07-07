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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.emory.clir.clearnlp.dependency.DEPLibEn;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.ner.BILOU;
import edu.emory.clir.clearnlp.ner.NERLib;
import edu.emory.clir.clearnlp.util.DSUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 2, 2015
 */
public class Document implements Serializable, Iterable<Entity>{
	public static final String DEPModSuffix = "mod";
	public static final String ignoredBILOUTag = BILOU.O.toString();
	public static final Set<String> ignoredWordForm = DSUtils.toHashSet("—", "’s");
	public static final Set<String> ignoredDEPLabels = DSUtils.toHashSet(DEPLibEn.DEP_APPOS, DEPLibEn.DEP_PUNCT, DEPLibEn.DEP_AUX, 
																		 DEPLibEn.DEP_RELCL, DEPLibEn.DEP_PREP, DEPLibEn.DEP_POSS,
																		 DEPLibEn.DEP_COMPOUND, DEPLibEn.DEP_DET, DEPLibEn.DEP_CASE);
	public static final Set<String> extactingNETags = DSUtils.toHashSet("ORG", "PERSON");
	
	private static final long serialVersionUID = 8332364748967299712L;
	
	private String s_title;
	private double w_confidence;
	private List<DEPTree> l_trees;
	private List<Entity> l_entities;
	private List<Entity> l_mainEntities;
	
	public Document(String title){
		s_title = title;
		l_trees = new ArrayList<>();
	}
	
	public Document(String title, List<DEPTree> trees){
		s_title = title;
		l_trees = trees;
	}
	
	public String getTitle(){
		return s_title;
	}
	
	public double getConfidence(){
		return w_confidence;
	}
	
	public List<DEPTree> getTrees(){
		return l_trees;
	}
	
	public int getTreeCount(){
		return l_trees.size();
	}
	
	public List<Entity> getEntities(){
		return (l_entities == null)? extractEntities() : l_entities;
	}
	
	public Entity getMostFrequentEntity(){
		return Collections.max(l_entities);
	}
	
	public List<Entity> getEntityRanks(){
		Collections.sort(getEntities(), Collections.reverseOrder());
		return l_entities;
	}
	
	public List<Entity> getEntityRanks(int k){
		return (k > l_entities.size())? getEntityRanks() : getEntityRanks().subList(0, k);
	}
	
	public List<Entity> getMainEntiies(){
		return l_mainEntities;
	}
	
	public void addTree(DEPTree tree){
		l_trees.add(tree);
	}
	
	public void addTrees(List<DEPTree> trees){
		l_trees.addAll(trees);
	}
	
	public void setConfidence(double confidence){
		w_confidence = confidence;
	}
	
	public void setMainEnities(List<Entity> entities){
		l_mainEntities = entities;
	}
	
	@SuppressWarnings("unused")
	private boolean isNamedEntity(DEPNode node){
		String NERTag = node.getNamedEntityTag(), DEPLabel = node.getLabel(), wordForm = node.getWordForm();
		return !NERTag.equals(ignoredBILOUTag) && !DEPLabel.endsWith(DEPModSuffix) && !ignoredDEPLabels.contains(DEPLabel) && !ignoredWordForm.contains(wordForm); 
	}
	
	private boolean isNamedEntity(DEPNode node, Set<String> NETags){
		String NERTag = node.getNamedEntityTag(), DEPLabel = node.getLabel(), wordForm = node.getWordForm();
		return !NERTag.equals(ignoredBILOUTag) && !DEPLabel.endsWith(DEPModSuffix) && !ignoredDEPLabels.contains(DEPLabel) && !ignoredWordForm.contains(wordForm) && NETags.contains(NERLib.toNamedEntity(NERTag)); 
	}
	
	private List<Entity> extractEntities(){
		DEPTree tree;
		int i, size = l_trees.size();
		List<Entity> list = new ArrayList<>();
		
		boolean hasAlias;
		for(i = 0; i < size; i++){
			tree = l_trees.get(i);
			for(DEPNode node : tree){							// iterate through all the nodes
				if(isNamedEntity(node, extactingNETags)){		// check if the node has the desired NE tag
					// check the node is an alias of another entity
					hasAlias = false;
					for(Entity entity : list){					
						
						if(entity.addAlias(i, node)){
							hasAlias = true; break;
						}
					}
					if(!hasAlias)	list.add(new Entity(i, node)); 					
				}
			}
		}
		
		l_entities = list;
		return l_entities;
	}

	@Override
	public Iterator<Entity> iterator() {
		Iterator<Entity> it = new Iterator<Entity>() {
			int i = 0, size = (l_entities == null)? 0 : l_entities.size();
			@Override
			public Entity next() {
				return l_entities.get(i++);
			}
			
			@Override
			public boolean hasNext() {
				return i < size;
			}
		};
		return it;
	}
}
