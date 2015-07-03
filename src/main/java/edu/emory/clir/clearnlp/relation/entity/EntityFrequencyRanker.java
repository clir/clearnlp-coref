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
package edu.emory.clir.clearnlp.relation.entity;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.dependency.DEPLibEn;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.ner.BILOU;
import edu.emory.clir.clearnlp.ner.NERLib;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.relation.structure.EntityCountEntry;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Joiner;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 2, 2015
 */
public class EntityFrequencyRanker {
	
	public static final String DIR_IN = "/Users/HenryChen/Desktop/NYTimes_Parsed/world";
	public static final String DIR_OUT = "/Users/HenryChen/Desktop/NYTimes_Parsed/world/EntityCount/";
	public static final String EXT = ".entityCount";
	public static final Set<String> extactingNETags = DSUtils.toHashSet("ORG", "PERSON");
	
	public static void main(String[] args){
		DEPTree tree; List<DEPTree> trees;
		List<String> l_filePaths = FileUtils.getFileList(DIR_IN, ".cnlp", true);
		
		PrintWriter writer;
		EntityFrequencyRanker ranker = new EntityFrequencyRanker();
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		
		
		
		for(String filePath : l_filePaths){
			try {
				trees = new ArrayList<>();
				reader.open(IOUtils.createFileInputStream(filePath));
				while( (tree = reader.next()) != null) trees.add(tree);
				reader.close();
				
				for(DEPTree t : trees)
					for(DEPNode node : t)
						if(ranker.isNamedEntity(node, extactingNETags))
							ranker.addEntity(node);
				
				writer = new PrintWriter(IOUtils.createBufferedPrintStream(DIR_OUT + FileUtils.getBaseName(filePath) + EXT));
				for(EntityCountEntry entry : ranker.getEntityRanks(5))
					writer.println(entry);
				writer.close();
				
				ranker.reset();
				
			} catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	public static final String DEPModSuffix = "mod";
	public static final String ignoredBILOUTag = BILOU.O.toString();
	public static final Set<String> ignoredDEPLabels = DSUtils.toHashSet(DEPLibEn.DEP_APPOS, DEPLibEn.DEP_PUNCT, DEPLibEn.DEP_AUX, 
																		 DEPLibEn.DEP_RELCL, DEPLibEn.DEP_PREP, DEPLibEn.DEP_POSS,
																		 DEPLibEn.DEP_COMPOUND, DEPLibEn.DEP_DET, DEPLibEn.DEP_CASE);
	private List<EntityCountEntry> l_entities;
	
	public EntityFrequencyRanker(){
		l_entities = new ArrayList<>();
	}
	
	public boolean isNamedEntity(DEPNode node){
		String NERTag = node.getNamedEntityTag(), DEPLabel = node.getLabel();
		return !NERTag.equals(ignoredBILOUTag) && !DEPLabel.endsWith(DEPModSuffix) && !ignoredDEPLabels.contains(DEPLabel); 
	}
	
	public boolean isNamedEntity(DEPNode node, Set<String> NETags){
		String NERTag = node.getNamedEntityTag(), DEPLabel = node.getLabel();
		return !NERTag.equals(ignoredBILOUTag) && !DEPLabel.endsWith(DEPModSuffix) && !ignoredDEPLabels.contains(DEPLabel) && NETags.contains(NERLib.toNamedEntity(NERTag)); 
	}
	
	public List<EntityCountEntry> getEntityRanks(){
		Collections.sort(l_entities, Collections.reverseOrder());
		return l_entities;
	}
	
	public List<EntityCountEntry> getEntityRanks(int k){
		return (k > l_entities.size())? getEntityRanks() : getEntityRanks().subList(0, k);
	}
	
	public EntityCountEntry getMostFrequentEntity(){
		return Collections.max(l_entities);
	}
	
	public void addEntity(DEPNode node){
		for(EntityCountEntry entity : l_entities){
			if(entity.hasAlias(node)) {
				entity.addAlias(node); return;
			}
		}
		l_entities.add(new EntityCountEntry(node));
	}
	
	public void reset(){
		l_entities = new ArrayList<>();
	}
	
	@Override
	public String toString(){
		return Joiner.join(l_entities.stream().map(e -> e.toString()).collect(Collectors.toList()), StringConst.NEW_LINE);
	}
}
