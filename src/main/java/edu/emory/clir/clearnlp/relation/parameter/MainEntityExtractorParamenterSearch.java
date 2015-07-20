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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.emory.clir.clearnlp.collection.map.IntDoubleHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectDoublePair;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.relation.chunk.AbstractChucker;
import edu.emory.clir.clearnlp.relation.chunk.EnglishProperNounChunker;
import edu.emory.clir.clearnlp.relation.extract.MainEntityExtractor;
import edu.emory.clir.clearnlp.relation.feature.MainEntityFeatureIndex;
import edu.emory.clir.clearnlp.relation.structure.Corpus;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.relation.utils.RelationExtractionFileUtil;
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
public class MainEntityExtractorParamenterSearch implements Runnable{
	private static final Set<String> extactingNETags = DSUtils.toHashSet("ORG", "PERSON");
//	private static final String DIR_IN = "/Users/HenryChen/Desktop/NYTimes_Parsed";
	private static final String DIR_IN = "/home/henryyhc/corpus/NYTimes_Parsed";
	
	private double CUTOFF, GAP;
	private double FREQ_COUNT_INIT, ENTITY_CONFID_INIT, FIRST_APPEAR_INIT;
	private double FREQ_COUNT, ENTITY_CONFID, FIRST_APPEAR;
	private double FREQ_COUNT_CAP, ENTITY_CONFID_CAP, FIRST_APPEAR_CAP, PARA_INCREMENT;
	private List<ObjectDoublePair<Double[]>> precisionRecords, F1Records;
	
	private PrintStream out;
	
	public MainEntityExtractorParamenterSearch(double cutoff, double gap, PrintStream out, double frequencyInit, double frequencyCap,
																							double entityInit, double entityCap,
																							double appearInit, double appearCap,
																							double parameterIncre){
		CUTOFF = cutoff; GAP = gap;
		FREQ_COUNT_INIT = frequencyInit;
		FREQ_COUNT_CAP = frequencyCap;
		ENTITY_CONFID_INIT = entityInit;
		ENTITY_CONFID_CAP = entityCap;
		FIRST_APPEAR_INIT = appearInit;
		FIRST_APPEAR_CAP = appearCap;
		PARA_INCREMENT = parameterIncre;
		
		precisionRecords = new ArrayList<>();
		F1Records = new ArrayList<>();
		
		this.out = out;
	}
	
	@Override
	public void run() {
		try {
			search(out);
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	private void search(PrintStream out){
		int intanceCount = 1;
		MainEntityExtractor extractor = null;
		MainEntityEvaluator evaluator = null;
		AbstractChucker chunker = new EnglishProperNounChunker(extactingNETags);
		
		Corpus corpus = initCorpus(false, chunker);
		
		for(FREQ_COUNT = FREQ_COUNT_INIT; FREQ_COUNT <= FREQ_COUNT_CAP; FREQ_COUNT+=PARA_INCREMENT)
			for(ENTITY_CONFID = ENTITY_CONFID_INIT; ENTITY_CONFID <= ENTITY_CONFID_CAP; ENTITY_CONFID+=PARA_INCREMENT)
				for(FIRST_APPEAR = FIRST_APPEAR_INIT; FIRST_APPEAR <= FIRST_APPEAR_CAP; FIRST_APPEAR+=PARA_INCREMENT){
					System.out.println("Instance #" + intanceCount++ + "... " + evaluate(corpus, chunker, extractor, evaluator) + 
							"\nParameters: " + CUTOFF + " " + GAP + " " + FREQ_COUNT + " " + ENTITY_CONFID + " " + FIRST_APPEAR);
				}
		
		PrintWriter writer = new PrintWriter(out);		
		writer.println("Precision records");
		for(ObjectDoublePair<Double[]> instance : precisionRecords)
			writer.println(instance.d + StringConst.TAB + Joiner.join(instance.o, StringConst.SPACE));
		
		writer.println("\nF1 records");
		for(ObjectDoublePair<Double[]> instance : F1Records)
			writer.println(instance.d + StringConst.TAB + Joiner.join(instance.o, StringConst.SPACE));		
		writer.close();
	}
	
	private double evaluate(Corpus corpus, AbstractChucker chunker, MainEntityExtractor extractor, MainEntityEvaluator evaluator){
		evaluator = new MainEntityEvaluator();
		extractor = new MainEntityExtractor(chunker, CUTOFF, GAP, getWeights(FREQ_COUNT, ENTITY_CONFID, FIRST_APPEAR));
		
		int doc_count = 0;
		for(Document document : corpus){
			document.setMainEnities(null);	
			extractor.getMainEntities(document, true);
			
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
	
	private Corpus initCorpus(boolean withTitleTree, AbstractChucker chunker){
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		List<String> l_filePaths = FileUtils.getFileList(DIR_IN, ".cnlp", true);
		
		Corpus corpus = RelationExtractionFileUtil.loadCorpus(reader, l_filePaths, "TEST", withTitleTree); 
		for(Document document : corpus) document.getEntities(chunker);
		return corpus;
	}
	
	private void logInstance(double precision, double DocF1Score){
		Double[] parameters = new Double[5];
		parameters[0] = CUTOFF; parameters[1] = GAP;
		parameters[2] = FREQ_COUNT; parameters[3] = ENTITY_CONFID; parameters[4] = FIRST_APPEAR;
		
		precisionRecords.add(new ObjectDoublePair<Double[]>(parameters, precision));
		F1Records.add(new ObjectDoublePair<Double[]>(parameters, DocF1Score));
	}
	
	private IntDoubleHashMap getWeights(double freqencyCount, double entityConfidence, double firstAppearance){
		IntDoubleHashMap weights = new IntDoubleHashMap();
		weights.put(MainEntityFeatureIndex.FREQUENCY_COUNT, freqencyCount);
		weights.put(MainEntityFeatureIndex.ENTITY_CONFIDENCE, entityConfidence);
		weights.put(MainEntityFeatureIndex.FIRST_APPEARENCE_SENTENCE_ID, firstAppearance);
		return weights;
	}
}
