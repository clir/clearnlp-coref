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
package edu.emory.clir.clearnlp.relation.parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.map.IntDoubleHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectDoublePair;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.relation.chunk.AbstractChucker;
import edu.emory.clir.clearnlp.relation.chunk.EnglishProperNounChunker;
import edu.emory.clir.clearnlp.relation.extract.MainEntityExtractor;
import edu.emory.clir.clearnlp.relation.feature.MainEntityFeatureIndex;
import edu.emory.clir.clearnlp.relation.structure.Corpus;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.relation.utils.RelationExtractionTestUtil;
import edu.emory.clir.clearnlp.relation.utils.evaluation.MainEntityEvaluator;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.Joiner;
import edu.emory.clir.clearnlp.util.MathUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 10, 2015
 */
public class MainEntityExtractorParamenterSearch {
	private final Set<String> extactingNETags = DSUtils.toHashSet("ORG", "PERSON");
	private static final String DIR_IN = "/Users/HenryChen/Desktop/NYTimes_Parsed";
	
	private static double CUTOFF = 0.485d, GAP = 0.05d;
	private static double FREQ_COUNT = 0.70d, ENTITY_CONFID = 0.05d, FIRST_APPEAR = 0.25d;
	
	private static final double CUTOFF_INCREMENT = 0.10d, GAP_INCREMENT = 0.05d, PARA_INCREMENT = 0.10d; 
	private static final double CUTOFF_CAP = 0.80d, GAP_CAP = 0.20d, PARA_CAP = 1d;
	
	private static List<ObjectDoublePair<Double[]>> precisionRecords = new ArrayList<>(), F1Records = new ArrayList<>();
	
	@Test
	public void search(){
		int intanceCount = 1;
		MainEntityExtractor extractor = null;
		MainEntityEvaluator evaluator = null;
		AbstractChucker chunker = new EnglishProperNounChunker(extactingNETags);
		
		Corpus corpus = initCorpus(false, chunker);
		
//		for(CUTOFF = CUTOFF_INCREMENT; CUTOFF <= CUTOFF_CAP; CUTOFF+=CUTOFF_INCREMENT)
//			for(GAP = GAP_INCREMENT; GAP <= GAP_CAP; GAP+=GAP_INCREMENT)
				for(FREQ_COUNT = PARA_INCREMENT; FREQ_COUNT <= PARA_CAP; FREQ_COUNT+=PARA_INCREMENT)
					for(ENTITY_CONFID = PARA_INCREMENT; ENTITY_CONFID <= PARA_CAP; ENTITY_CONFID+=PARA_INCREMENT)
						for(FIRST_APPEAR = PARA_INCREMENT; FIRST_APPEAR <= PARA_CAP; FIRST_APPEAR+=PARA_INCREMENT){
							System.out.print("Instance #" + intanceCount++ + "... ");
							System.out.println(evaluate(corpus, chunker, extractor, evaluator));
						}
		
		Collections.sort(precisionRecords, Collections.reverseOrder());
		Collections.sort(F1Records, Collections.reverseOrder());
		
		if(intanceCount > 100){
			precisionRecords = precisionRecords.subList(0, 100);
			F1Records = F1Records.subList(0, 100);
		}
		
		System.out.println("\n Precision records");
		for(ObjectDoublePair<Double[]> instance : precisionRecords)
			System.out.println(instance.d + StringConst.TAB + Joiner.join(instance.o, StringConst.SPACE));
		
		System.out.println("\n F1 records");
		for(ObjectDoublePair<Double[]> instance : F1Records)
			System.out.println(instance.d + StringConst.TAB + Joiner.join(instance.o, StringConst.SPACE));
	}
	
	private static double evaluate(Corpus corpus, AbstractChucker chunker, MainEntityExtractor extractor, MainEntityEvaluator evaluator){
		evaluator = new MainEntityEvaluator();
		extractor = new MainEntityExtractor(chunker, CUTOFF, GAP, getWeights(FREQ_COUNT, ENTITY_CONFID, FIRST_APPEAR));
		
		int doc_count = 0;
		for(Document document : corpus){
			document.setMainEnities(null);	
			document.setMainEnities(extractor.getMainEntities(document));
			
			if(!document.getMainEntities().isEmpty()){
				doc_count++;
				evaluator.evaluatePrecisionOnDocumentTitle(document.getTitle(), document.getMainEntities());
			}
		}
		
		double precision = evaluator.getAveragePrecision(),
				DocF1Score = MathUtils.getF1(precision, (double)doc_count/corpus.getDocumentCount()); 
		logInstance(precision, DocF1Score);
		return precision;
	}
	
	private static Corpus initCorpus(boolean withTitleTree, AbstractChucker chunker){
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		List<String> l_filePaths = FileUtils.getFileList(DIR_IN, ".cnlp", true);
		
		Corpus corpus = RelationExtractionTestUtil.loadCorpus(reader, l_filePaths, "TEST", withTitleTree); 
		for(Document document : corpus) document.getEntities(chunker);
		return corpus;
	}
	
	private static void logInstance(double precision, double DocF1Score){
		Double[] parameters = new Double[5];
		parameters[0] = CUTOFF; parameters[1] = GAP;
		parameters[2] = FREQ_COUNT; parameters[3] = ENTITY_CONFID; parameters[4] = FIRST_APPEAR;
		
		precisionRecords.add(new ObjectDoublePair<Double[]>(parameters, precision));
		F1Records.add(new ObjectDoublePair<Double[]>(parameters, DocF1Score));
	}
	
	private static IntDoubleHashMap getWeights(double freqencyCount, double entityConfidence, double firstAppearance){
		IntDoubleHashMap weights = new IntDoubleHashMap();
		weights.put(MainEntityFeatureIndex.FREQUENCY_COUNT, freqencyCount);
		weights.put(MainEntityFeatureIndex.ENTITY_CONFIDENCE, entityConfidence);
		weights.put(MainEntityFeatureIndex.FIRST_APPEARENCE_SENTENCE_ID, firstAppearance);
		return weights;
	}
}
