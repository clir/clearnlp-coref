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
package edu.emory.clir.clearnlp.relation.evaluation;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.map.IntDoubleHashMap;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.relation.chunk.AbstractChucker;
import edu.emory.clir.clearnlp.relation.chunk.EnglishProperNounChunker;
import edu.emory.clir.clearnlp.relation.extract.WeightedMainEntityExtractor;
import edu.emory.clir.clearnlp.relation.feature.MainEntityFeatureIndex;
import edu.emory.clir.clearnlp.relation.structure.Corpus;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.relation.utils.RelationExtractionFileUtil;
import edu.emory.clir.clearnlp.relation.utils.evaluation.MainEntityEvaluator;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.MathUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 9, 2015
 */
public class MainEntityExtractionWithProperNounChunker {
	private final Set<String> extactingNETags = DSUtils.toHashSet("ORG", "PERSON", "LOC", "GPE");
	private static final String DIR_IN = "/Users/HenryChen/Desktop/NYTimes_Parsed";
	
	private static double CUTOFF = 0.490d, GAP = 0.05d;
	private static double FREQ_COUNT = 0.785d, ENTITY_CONFID = 0.125d, FIRST_APPEAR = 0.315d;
	
	private static IntDoubleHashMap getWeights(){
		IntDoubleHashMap weights = new IntDoubleHashMap();
		weights.put(MainEntityFeatureIndex.FREQUENCY_COUNT, FREQ_COUNT);
		weights.put(MainEntityFeatureIndex.ENTITY_CONFIDENCE, ENTITY_CONFID);
		weights.put(MainEntityFeatureIndex.FIRST_APPEARENCE_SENTENCE_ID, FIRST_APPEAR);
		return weights;
	}
	
	@Test
	public void testExtractor(){
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		List<String> l_filePaths = FileUtils.getFileList(DIR_IN, ".cnlp", true);
		
		AbstractChucker chunker = new EnglishProperNounChunker(extactingNETags);
		Corpus corpus = RelationExtractionFileUtil.loadCorpus(reader, l_filePaths, "NYTimes", true);
		WeightedMainEntityExtractor extractor = new WeightedMainEntityExtractor(chunker, CUTOFF, GAP, getWeights());
		MainEntityEvaluator evaluator = new MainEntityEvaluator(chunker);
		
		int doc_count = 0;
		for(Document document : corpus){
			extractor.getMainEntities(document, true);

			if(!document.getMainEntities().isEmpty()){
				evaluator.evaluatePrecisionOnDocumentTitle(document.getTitle(), document.getMainEntities());
				System.out.println(document.getMainEntities().size() + "/" + document.getEntities().size());
				doc_count++;
			}
		}	
		System.out.println("==========");
		System.out.println("Document count: " + doc_count + "/" + corpus.getDocumentCount());
		System.out.println("Precision: " + evaluator.getAveragePrecision());
		System.out.println("F1 Score: " + MathUtils.getF1(evaluator.getAveragePrecision(), (double)doc_count/corpus.getDocumentCount()));
	}
}
