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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.classification.vector.AbstractWeightVector;
import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.collection.triple.Triple;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.AbstractReader;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.relation.chunk.EnglishProperNounChunker;
import edu.emory.clir.clearnlp.relation.component.entity.MainEntityIdentificationComponent;
import edu.emory.clir.clearnlp.relation.extract.AbstractMainEntityExtractor;
import edu.emory.clir.clearnlp.relation.extract.DeterministicMainEntityExtractor;
import edu.emory.clir.clearnlp.relation.structure.Corpus;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.relation.structure.Entity;
import edu.emory.clir.clearnlp.relation.utils.RelationExtractionFileUtil;
import edu.emory.clir.clearnlp.relation.utils.evaluation.MainEntityEvaluator;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 22, 2015
 */
public class MainEntityIdentificationBootstrapping {
	/* alpha(0.01) : learning rate, rho(0.1) : regularizaiton, bias(0) */
	public static final int labelCutoff = 0;
	public static final int featureCutoff = 0;
	public static final boolean average = true;
	public static final double alpha = 0.01;
	public static final double rho = 0.01;
	public static final double bias = 0;
	
	public static final double DEV_THRESHOLD = 0.00025;
	
	private static final NumberFormat decimalFormatter = new DecimalFormat("#0.00");
	private static final String DIR_IN = "/Users/HenryChen/Desktop/NYTimes_Parsed";
	private static final String DIR_SEED = "/Users/HenryChen/Desktop/NYTimes_Seed";
	private static final String LOG_OUT = "/Users/HenryChen/Desktop/mainEntity_prediction.out";
	
	private static final int INSTANCE_CAP = 10;
	
	@Test
	public void bootstrapModel(){
		EnglishProperNounChunker chunker = new EnglishProperNounChunker();
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		Corpus seedCorpus = generateSeedCorpus(reader, chunker, DIR_SEED), 
				generalCorpus = generateCorpus(reader, chunker, DIR_IN, "NYTimes");
		printCourpusSummary(seedCorpus);
		
		MainEntityIdentificationComponent component;

		int instanceCount = 0;
		
		while(instanceCount <= INSTANCE_CAP){
			System.out.println("\nInstance #" + instanceCount++);
			component = new MainEntityIdentificationComponent(labelCutoff, featureCutoff, average, alpha, rho, bias);
			
			train(component, seedCorpus);
			decode(component, generalCorpus);
			evaluate(generalCorpus);
			
			seedCorpus = augmentSeedCorpus(seedCorpus, generalCorpus);
			printCourpusSummary(seedCorpus);
			printCourpusSummary(generalCorpus);
			
			resetCorpus(generalCorpus);
		}
	}
	
	private void train(MainEntityIdentificationComponent component, Corpus corpus){
		component.setFlag(CFlag.TRAIN);
		for(Document document : corpus)	component.train(document);
		System.out.println(StringConst.NEW_LINE + "Initializing trainer");
		component.initTrainer();
		System.out.println(component.getTrainer().trainerInfoFull());
		develop(component, corpus);
	}
	
	private void develop(MainEntityIdentificationComponent component, Corpus corpus){
		int iterCount = 0;
		List<Entity> l_mainEntities;
		double current = 0d, previous = 0d;
		MainEntityEvaluator evaluator = null;
		Pair<Triple<Double, Double, Double>, AbstractWeightVector> best = new Pair<>(new Triple<>(0d, 0d, 0d), null);
		
		component.setFlag(CFlag.DECODE);
		System.out.println(StringConst.NEW_LINE + "Developing");
		do{
			previous = current;
			System.out.print("Iteration " + iterCount++ + " ...");
			
			component.trainModel();
					
			evaluator = new MainEntityEvaluator();
			for(Document document : corpus){
				l_mainEntities = component.decode(document).o1;
				evaluator.evaluatePrecisionOnDocumentTitle(document.getTitle(), l_mainEntities);
				evaluator.evaluateRecall(document.getMainEntities(), l_mainEntities);
			}
			System.out.println(evaluator.getAverageTriple());
			
			current = evaluator.getAverageF1Score();
			if(current > best.o1.o3)	best.set(evaluator.getAverageTriple(), component.getModel().getWeightVector());		
		} while(Math.abs(current - previous) > DEV_THRESHOLD);
		
		System.out.println("\nHighest result:");
		System.out.println("Precision:\t" + decimalFormatter.format(best.o1.o1 * 100) + "%");
		System.out.println("Recall:\t\t" + decimalFormatter.format(best.o1.o2 * 100) + "%");
		System.out.println("F1 score:\t" + decimalFormatter.format(best.o1.o3 * 100) + "%");
		
		component.getModel().setWeightVector(best.o2);
	}
	
	private void decode(MainEntityIdentificationComponent component, Corpus corpus){
		component.setFlag(CFlag.DECODE);
		System.out.println(StringConst.NEW_LINE + "Decoding");
		
		Pair<List<Entity>, List<Entity>> decodePair;
		for(Document document : corpus){
			decodePair = component.decode(document);
			document.setMainEnities(decodePair.o1);
			document.setNonMainEnities(decodePair.o2);
		}
	}
	
	private void evaluate(Corpus corpus){
		MainEntityEvaluator evaluator = new MainEntityEvaluator();
		for(Document document : corpus)
			evaluator.evaluatePrecisionOnDocumentTitle(document.getTitle(), document.getMainEntities());
		System.out.println("\nEvaluation result:");
		System.out.println("Precision: " + decimalFormatter.format(evaluator.getAveragePrecision() * 100) + "%");
	}
	
	private Corpus generateCorpus(AbstractReader<DEPTree> reader, EnglishProperNounChunker chunker, String fileDirectory, String corpusName){
		List<String> l_filePaths = FileUtils.getFileList(fileDirectory, ".cnlp", true);
		Corpus corpus = RelationExtractionFileUtil.loadCorpus(reader, l_filePaths, corpusName, false);
		for(Document document : corpus)
			if(document.getEntities() == null) document.getEntities(chunker);
		return corpus;
	}
	
	private Corpus generateSeedCorpus(AbstractReader<DEPTree> reader, EnglishProperNounChunker chunker, String fileDirectory){
		Corpus seedCorpus = generateCorpus(reader, chunker, fileDirectory, "NYTimes_Seed");
		AbstractMainEntityExtractor extractor = new DeterministicMainEntityExtractor();
		for(Document document : seedCorpus){
			extractor.getMainEntities(document, true);
			extractor.getNonMainEntities(document, true);
		}
		return seedCorpus;
	}
	
	private Corpus augmentSeedCorpus(Corpus seedCorpus, Corpus bootstrapCorpus){
		// Replace seed corpus with bootstrap corpus
		Corpus corpus = new Corpus("NYTimes_Seed");
		for(Document document : bootstrapCorpus){
			if(!document.getMainEntities().isEmpty())
				corpus.addDocument(new Document(document));
		}
		return corpus;
		
		// Augment seed corpus with bootstrap corpus		
//		for(Document document : bootstrapCorpus){
//			if(!document.getMainEntities().isEmpty())
//				seedCorpus.addDocument(new Document(document));
//		}
	}
	
	private void resetCorpus(Corpus corpus){
		for(Document document : corpus){
			document.setMainEnities(null);
			document.setNonMainEnities(null);
		}
	}
	
	private void printCourpusSummary(Corpus corpus){
		int totalMainEntity = 0, totalNonMainEntity = 0, totalEntity = 0;
		for(Document document : corpus){
			totalEntity += document.getEntities().size();
			totalMainEntity += document.getMainEntities().size();
			totalNonMainEntity += document.getNonMainEntities().size();
		}
		
		System.out.println("\nCorpus Summary:");
		System.out.println("Corpus name: " + corpus.getCorpusName() + ", Document count: " + corpus.getDocumentCount());
		System.out.println("(+): " + totalMainEntity + ", (-): " + totalNonMainEntity + ", Total: " + totalEntity);
	}
}
