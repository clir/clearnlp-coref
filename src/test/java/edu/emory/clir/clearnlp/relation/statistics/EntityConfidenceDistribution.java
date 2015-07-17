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
package edu.emory.clir.clearnlp.relation.statistics;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.relation.extract.AbstractMainEntityExtractor;
import edu.emory.clir.clearnlp.relation.extract.DeterministicMainEntityExtractor;
import edu.emory.clir.clearnlp.relation.structure.Corpus;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.relation.structure.Entity;
import edu.emory.clir.clearnlp.relation.utils.RelationExtractionTestUtil;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 15, 2015
 */
public class EntityConfidenceDistribution {
	private static final String DIR_IN = "/Users/HenryChen/Desktop/NYTimes_Parsed";
	private static final String OUT = "/Users/HenryChen/Desktop/data.out";
	
	@Test
	public void getDistribution(){
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		List<String> l_filePaths = FileUtils.getFileList(DIR_IN, ".cnlp", true);
		
		Corpus corpus = RelationExtractionTestUtil.loadCorpus(reader, l_filePaths, "NYTimes", false);
		AbstractMainEntityExtractor extractor = new DeterministicMainEntityExtractor();

		List<Entity> l_entities;
		List<List<Entity>> l_entityRanks_overCorpus = new ArrayList<>();
		
		int i; Entity entity;
		for(Document document : corpus){
			extractor.getMainEntities(document, true);
			l_entities = document.getEntities();
			
			if(!document.getMainEntities().isEmpty()){
				for(i = 0; i < l_entities.size(); i++){
					entity = l_entities.get(i);
					if(l_entityRanks_overCorpus.size() <= i)
						l_entityRanks_overCorpus.add(new ArrayList<>());
					l_entityRanks_overCorpus.get(i).add(entity);
				}
			}
		}	
		
		List<Double> l_avgRankConfidence = new ArrayList<>(l_entityRanks_overCorpus.size());
		for(List<Entity> entities : l_entityRanks_overCorpus)
			l_avgRankConfidence.add( entities.stream().mapToDouble(Entity::getEntityConfidence).average().getAsDouble() );
		for(double avgConfidence : l_avgRankConfidence) System.out.println(avgConfidence);
		System.out.println();
		
		List<Double> l_entityConfidences = new ArrayList<>();
		for(List<Entity> entities : l_entityRanks_overCorpus)
			for(Entity e : entities) l_entityConfidences.add(e.getEntityConfidence());
		
		PrintWriter writer = new PrintWriter(IOUtils.createBufferedPrintStream(OUT));
		for(double confidence : l_entityConfidences) writer.println(confidence);
		writer.close();
	}
}
