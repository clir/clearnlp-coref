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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.relation.extract.AbstractMainEntityExtractor;
import edu.emory.clir.clearnlp.relation.extract.DeterministicMainEntityExtractor;
import edu.emory.clir.clearnlp.relation.structure.Corpus;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.relation.structure.Entity;
import edu.emory.clir.clearnlp.relation.utils.RelationExtractionFileUtil;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Joiner;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 16, 2015
 */
public class EntityConfidenceStatistics {
	private static final String DIR_IN = "/Users/HenryChen/Desktop/NYTimes_Parsed";
	private static final String OUT = "/Users/HenryChen/Desktop/data.out";
	
	@Test
	public void getStatistics(){
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		List<String> l_filePaths = FileUtils.getFileList(DIR_IN, ".cnlp", true);
		
		Corpus corpus = RelationExtractionFileUtil.loadCorpus(reader, l_filePaths, "NYTimes", false);
		AbstractMainEntityExtractor extractor = new DeterministicMainEntityExtractor();

		List<Entity> l_entities;
		PrintWriter writer = new PrintWriter(IOUtils.createBufferedPrintStream(OUT));
		for(Document document : corpus){
			extractor.getMainEntities(document, true);
			l_entities = document.getEntities();
			
			Collections.sort(l_entities, Collections.reverseOrder());
			if(!document.getMainEntities().isEmpty()){
				writer.println(
						Joiner.join(l_entities.stream().map(Entity::getEntityConfidence).collect(Collectors.toList()), StringConst.TAB)
				);
			}
		}	
		writer.close();
	}
}
