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
package edu.emory.clir.clearnlp.relation.model;

import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.relation.component.entity.MainEntityIdentificationComponent;
import edu.emory.clir.clearnlp.relation.extract.AbstractMainEntityExtractor;
import edu.emory.clir.clearnlp.relation.extract.DeterministicMainEntityExtractor;
import edu.emory.clir.clearnlp.relation.structure.Corpus;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.relation.structure.Entity;
import edu.emory.clir.clearnlp.relation.utils.RelationExtractionTestUtil;
import edu.emory.clir.clearnlp.util.FileUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 17, 2015
 */
public class MainEntityIdentificationModelTest {
	public static final int iter = 10;
	public static final int labelCutoff = 0;
	public static final int featureCutoff = 0;
	public static final boolean average = true;
	public static final double alpha = 0.1;
	public static final double rho = 0.1;
	public static final double bias = 0;
	
	private static final String DIR_IN = "/Users/HenryChen/Desktop/NYTimes_Parsed";
	
	@Test
	public void modelExperiment(){
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		List<String> l_filePaths = FileUtils.getFileList(DIR_IN, ".cnlp", true);
		
		Corpus corpus = RelationExtractionTestUtil.loadCorpus(reader, l_filePaths, "NYTimes", false);
		AbstractMainEntityExtractor extractor = new DeterministicMainEntityExtractor();
		
		
		// Bootstrapping seed documents for training.
		List<Entity> l_mainEntities;
		Corpus trnCorpus = new Corpus("NYTimes_Train"), devCorpus = new Corpus("NYTimes_Development");
		for(Document document : corpus){
			l_mainEntities = extractor.getMainEntities(document, false);
			
			if(!l_mainEntities.isEmpty()){
				document.setMainEnities(l_mainEntities);
				document.setNonMainEnities(extractor.getNonMainEntities(document));
				trnCorpus.addDocument(document);
			}
			else
				devCorpus.addDocument(document);
		}
		
		MainEntityIdentificationComponent component = new MainEntityIdentificationComponent(labelCutoff, featureCutoff, average, alpha, rho, bias);
		// Training
		component.setFlag(CFlag.TRAIN);
		for(Document document : trnCorpus)	component.train(document);
		component.initTrainer();
		component.trainModel();
		
		// Decoding
		component.setFlag(CFlag.DECODE);
		for(Document document : devCorpus){
			l_mainEntities = component.decode(document);
			document.setMainEnities(l_mainEntities);
		}
	}
}
