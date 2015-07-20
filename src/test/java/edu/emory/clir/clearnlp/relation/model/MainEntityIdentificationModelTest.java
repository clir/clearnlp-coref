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

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import edu.emory.clir.clearnlp.classification.vector.AbstractWeightVector;
import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.collection.triple.Triple;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.relation.component.entity.MainEntityIdentificationComponent;
import edu.emory.clir.clearnlp.relation.extract.AbstractMainEntityExtractor;
import edu.emory.clir.clearnlp.relation.extract.DeterministicMainEntityExtractor;
import edu.emory.clir.clearnlp.relation.structure.Corpus;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.relation.structure.Entity;
import edu.emory.clir.clearnlp.relation.utils.RelationExtractionFileUtil;
import edu.emory.clir.clearnlp.relation.utils.evaluation.MainEntityEvaluator;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 17, 2015
 */
public class MainEntityIdentificationModelTest {
	/* alpha(0.01) : learning rate, rho(0.1) : regularizaiton, bias(0) */
	public static final int labelCutoff = 0;
	public static final int featureCutoff = 0;
	public static final boolean average = true;
	public static final double alpha = 0.01;
	public static final double rho = 0.01;
	public static final double bias = 0;
	
	public static final double DEV_THRESHOLD = 0.00035;
	
	private static final String DIR_IN = "/Users/HenryChen/Desktop/NYTimes_Parsed";
	private static final String DIR_SEED = "/Users/HenryChen/Desktop/NYTimes_Seed";
	
	private static final String DATA_OUT = "/Users/HenryChen/Desktop/mainEntity_prediction.out";
	
	@Test
	@Ignore
	public void trainModel_withSeed(){
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		List<String> l_filePaths = FileUtils.getFileList(DIR_SEED, ".cnlp", true);
		
		int totalMainEntity = 0, totalNonMainEntity = 0, totalEntity = 0;
		Corpus seedCorpus = RelationExtractionFileUtil.loadCorpus(reader, l_filePaths, "NYTimes_Seed", false);
		AbstractMainEntityExtractor extractor = new DeterministicMainEntityExtractor();
		for(Document document : seedCorpus){
			extractor.getMainEntities(document, true);
			extractor.getNonMainEntities(document, true);
			
			totalEntity += document.getEntities().size();
			totalMainEntity += document.getMainEntities().size();
			totalNonMainEntity += document.getNonMainEntities().size();
		}
		System.out.println("(+): " + totalMainEntity + ", (-): " + totalNonMainEntity + ", Total: " + totalEntity);
		
		List<Entity> l_mainEntities;
		MainEntityIdentificationComponent component = new MainEntityIdentificationComponent(labelCutoff, featureCutoff, average, alpha, rho, bias);

		// Training
		component.setFlag(CFlag.TRAIN);
		for(Document document : seedCorpus)	component.train(document);
		
		System.out.println(StringConst.NEW_LINE + "Initializing trainer");
		component.initTrainer();
		System.out.println(component.getTrainer().trainerInfoFull());
		
		// Developing
		int iterCount = 0;
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
			for(Document document : seedCorpus){
				l_mainEntities = component.decode(document);
				evaluator.evaluatePrecisionOnDocumentTitle(document.getTitle(), l_mainEntities);
				evaluator.evaluateRecall(document.getMainEntities(), l_mainEntities);
			}
			System.out.println(evaluator.getAverageTriple());
			
			current = evaluator.getAverageF1Score();
			if(current > best.o1.o3)	best.set(evaluator.getAverageTriple(), component.getModel().getWeightVector());		
		} while(Math.abs(current - previous) > DEV_THRESHOLD);
		
		NumberFormat formatter = new DecimalFormat("#0.00");
		System.out.println("\nHighest result:");
		System.out.println("Precision:\t" + formatter.format(best.o1.o1 * 100) + "%");
		System.out.println("Recall:\t\t" + formatter.format(best.o1.o2 * 100) + "%");
		System.out.println("F1 score:\t" + formatter.format(best.o1.o3 * 100) + "%");
	}
	
	@Test
//	@Ignore
	public void trainModel(){
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		List<String> l_filePaths = FileUtils.getFileList(DIR_IN, ".cnlp", true);
		
		Corpus corpus = RelationExtractionFileUtil.loadCorpus(reader, l_filePaths, "NYTimes", false);
		AbstractMainEntityExtractor extractor = new DeterministicMainEntityExtractor();
		
		
		// Bootstrapping seed documents for training.
		List<Entity> l_mainEntities;
		int totalMainEntity = 0, totalNonMainEntity = 0, totalEntity = 0;
		Corpus trnCorpus = new Corpus("NYTimes_Train"), devCorpus = new Corpus("NYTimes_Development");
		for(Document document : corpus){
			l_mainEntities = extractor.getMainEntities(document, false);
			
			if(!l_mainEntities.isEmpty()){
				document.setMainEnities(l_mainEntities);
				extractor.getNonMainEntities(document, true);
				trnCorpus.addDocument(document);
				
				totalEntity += document.getEntities().size();
				totalMainEntity += document.getMainEntities().size();
				totalNonMainEntity += document.getNonMainEntities().size();
			}
			else
				devCorpus.addDocument(document);
		}
		System.out.println("(+): " + totalMainEntity + ", (-): " + totalNonMainEntity + ", Total: " + totalEntity);
		
		MainEntityIdentificationComponent component = new MainEntityIdentificationComponent(labelCutoff, featureCutoff, average, alpha, rho, bias);
		// Training
		component.setFlag(CFlag.TRAIN);
		for(Document document : trnCorpus)	component.train(document);
		
		System.out.println(StringConst.NEW_LINE + "Initializing trainer");
		component.initTrainer();
		System.out.println(component.getTrainer().trainerInfoFull());
		
		// Developing
		int iterCount = 0;
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
			for(Document document : trnCorpus){
				l_mainEntities = component.decode(document);
				evaluator.evaluatePrecisionOnDocumentTitle(document.getTitle(), l_mainEntities);
				evaluator.evaluateRecall(document.getMainEntities(), l_mainEntities);
			}
			System.out.println(evaluator.getAverageTriple());
					
			current = evaluator.getAverageF1Score();
			if(current > best.o1.o3)	best.set(evaluator.getAverageTriple(), component.getModel().getWeightVector());		
		} while(Math.abs(current - previous) > DEV_THRESHOLD);
		
		NumberFormat formatter = new DecimalFormat("#0.00");
		System.out.println("\nHighest result:");
		System.out.println("Precision:\t" + formatter.format(best.o1.o1 * 100) + "%");
		System.out.println("Recall:\t\t" + formatter.format(best.o1.o2 * 100) + "%");
		System.out.println("F1 score:\t" + formatter.format(best.o1.o3 * 100) + "%");
		
		// Decoding
		System.out.println("\nDecoding");
		evaluator = new MainEntityEvaluator();
		component.getModel().setWeightVector(best.o2);
		PrintWriter writer = new PrintWriter(IOUtils.createBufferedPrintStream(DATA_OUT));
		
		for(Document document : devCorpus){
			l_mainEntities = component.decode(document);
			document.setMainEnities(l_mainEntities);
			evaluator.evaluatePrecisionOnDocumentTitle(document.getTitle(), l_mainEntities);
			
			writer.println(document.getTitle());
			writer.println(document.getMainEntities());
			writer.println();
		}
		writer.close();
		
		System.out.println("Precision: " + formatter.format(evaluator.getAveragePrecision() * 100) + "%");
	}
}
