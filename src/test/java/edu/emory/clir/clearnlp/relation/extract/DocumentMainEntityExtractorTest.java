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
package edu.emory.clir.clearnlp.relation.extract;

import java.io.PrintWriter;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import edu.emory.clir.clearnlp.collection.map.IntDoubleHashMap;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.relation.feature.MainEntityFeatureIndex;
import edu.emory.clir.clearnlp.relation.structure.Corpus;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.relation.structure.Entity;
import edu.emory.clir.clearnlp.relation.utils.RelationExtractionTestUtil;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 6, 2015
 */
public class DocumentMainEntityExtractorTest {
	private static final String DIR_IN = "/Users/HenryChen/Desktop/NYTimes_Parsed";
	private static final String DIR_OUT = "/Users/HenryChen/Desktop/NYTimes_Filtered/";
	
	private static double CUTOFF = 0.40d, GAP = 0.10d; 
	private static IntDoubleHashMap getWeights(){
		IntDoubleHashMap weights = new IntDoubleHashMap();
		weights.put(MainEntityFeatureIndex.FREQUENCY_COUNT, 0.70d);
		weights.put(MainEntityFeatureIndex.EntityConfidence, 0.05d);
		weights.put(MainEntityFeatureIndex.FirstApearSentenceId, 0.25d);
		return weights;
	}
	
	@Test
	public void testExtractor(){
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		List<String> l_filePaths = FileUtils.getFileList(DIR_IN, ".cnlp", true);
		Corpus corpus = RelationExtractionTestUtil.loadCorpus(reader, l_filePaths, "NYTimes");
		DocumentMainEntityExtractor extractor = new DocumentMainEntityExtractor(CUTOFF, GAP, getWeights());
		
		String entityWordform;
		int document_count = 0;
		PrintWriter writer;
		
		for(Document document : corpus){
			document.setMainEnities(extractor.getMainEntities(document));
			
			if(!document.getMainEntiies().isEmpty()){
//				entityWordform = document.getMainEntiies().get(0).getFirstAlias().getNode().getWordForm();
//				if(document.getTitle().contains(entityWordform)){
					document_count++;
					
//					writer = new PrintWriter(IOUtils.createBufferedPrintStream(DIR_OUT + document.getTitle()));
//					for(DEPTree tree : document.getTrees()){
//						for(DEPNode node : tree) writer.println(node.toStringNER());
//						writer.println();
//					}
//					writer.close();
//					
//					writer = new PrintWriter(IOUtils.createBufferedPrintStream(DIR_OUT + document.getTitle() + ".entity"));
//					writer.println(document.getMainEntiies());
//					writer.close();
//				}
			}
		}
		
		System.out.println(document_count + "/" + corpus.getDocumentCount());
	}
	
	@Test
	@Ignore
	public void testExtractorWithSingleDocument(){
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		List<String> l_filePaths = DSUtils.toArrayList("/Users/HenryChen/Desktop/test.cnlp");		
		Corpus corpus = RelationExtractionTestUtil.loadCorpus(reader, l_filePaths, "NYTimes");
		DocumentMainEntityExtractor extractor = new DocumentMainEntityExtractor(CUTOFF, GAP, getWeights());
		
		List<Entity> l_mainEntities;
		for(Document document : corpus){
			l_mainEntities = extractor.getMainEntities(document);
			System.out.println(document.getTitle());
			System.out.println(l_mainEntities);
		}
	}
}
