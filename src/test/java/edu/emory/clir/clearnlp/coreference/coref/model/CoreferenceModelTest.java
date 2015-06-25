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
package edu.emory.clir.clearnlp.coreference.coref.model;

import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.triple.Triple;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.coreference.components.CoreferenceComponent;
import edu.emory.clir.clearnlp.coreference.config.MentionConfiguration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.utils.CoreferenceTestUtil;
import edu.emory.clir.clearnlp.coreference.utils.evaluator.AbstractEvaluator;
import edu.emory.clir.clearnlp.coreference.utils.evaluator.BCubedEvaluator;
import edu.emory.clir.clearnlp.coreference.utils.reader.CoreferenceTSVReader;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 11, 2015
 */
public class CoreferenceModelTest {
	public static final int iter = 10;
	public static final int labelCutoff = 0;
	public static final int featureCutoff = 0;
	public static final boolean average = true;
	public static final double alpha = 0.1;
	public static final double rho = 0.1;
	public static final double bias = 0;
	
	public static double DEV_THRESHOLD = 0.0005;
	
	@Test
	public void test(){
		MentionConfiguration m_config = new MentionConfiguration(true, true, true);
		CoreferenceTSVReader reader = new CoreferenceTSVReader(m_config, true, true, 0, 1, 2, 3, 9, 4, 5, 6, -1, -1, 10);		
		
		List<String> trn_filePaths = FileUtils.getFileList("/Users/HenryChen/Desktop/conll-13/train", ".cnlp", true),
					dev_filePaths = FileUtils.getFileList("/Users/HenryChen/Desktop/conll-13/development", ".cnlp", true),
				 	test_filePaths = FileUtils.getFileList("/Users/HenryChen/Desktop/conll-13/test", ".cnlp", true);
		
//		List<String> trn_filePaths = FileUtils.getFileList("/Users/HenryChen/Desktop/conll-13-dummy/train", ".cnlp", true),
//				dev_filePaths = FileUtils.getFileList("/Users/HenryChen/Desktop/conll-13-dummy/development", ".cnlp", true),
//			 	test_filePaths = FileUtils.getFileList("/Users/HenryChen/Desktop/conll-13-dummy/test", ".cnlp", true);
		
//		List<String> trn_filePaths = FileUtils.getFileList("/Users/HenryChen/Desktop/conll-13-dummy/train-dummy", ".cnlp", true),
//				dev_filePaths = trn_filePaths, test_filePaths = trn_filePaths;
		
		CoreferenceComponent component = new CoreferenceComponent(labelCutoff, featureCutoff, average, alpha, rho, bias);
		train(component, reader, trn_filePaths);
		
		BCubedEvaluator evaluator = new BCubedEvaluator();
		develop(component, reader, dev_filePaths, DEV_THRESHOLD);
		evaluate(component, reader, evaluator, test_filePaths);
		
		System.out.println("\nPerformance Summary:");
		System.out.println("Traning document count: " + trn_filePaths.size());
		System.out.println("Developing document count: " + dev_filePaths.size());
		System.out.println(evaluator.getEvaluationSummary());
	}
	
	private void train(CoreferenceComponent component, CoreferenceTSVReader reader, List<String> trn_filePaths){
		component.setFlag(CFlag.TRAIN);
		System.out.println("Initializing...");
		
		for(String filePath : trn_filePaths){
			System.out.println("Adding document " + FileUtils.getBaseName(filePath));
			reader.open(IOUtils.createFileInputStream(filePath));			
			component.train(reader.getGoldCoNLLDocument());
			reader.close();
		}
		
		System.out.println("\nTRAINING...");
		
		component.initTrainer(iter);
		
		System.out.println("Trainer info:");
		System.out.println(component.getTrainer().trainerInfoFull());
		
		System.out.println(".........\nDONE!");
	}
	
	private void develop(CoreferenceComponent component, CoreferenceTSVReader reader, List<String> dev_filePaths, double threshold){
		component.setFlag(CFlag.DECODE);
		System.out.println("\nDEVELOPING...");
		
		int iter = 0;
		double prevScore = 0, currScore;
		CoreferantSet prediction;
		AbstractEvaluator evaluator;
		Triple<List<DEPTree>, List<AbstractMention>, CoreferantSet> document;
		
		while(true){
			System.out.print("Iteration #" + iter++);
			component.trainModel();
			
			evaluator = new BCubedEvaluator();
			System.out.print("... Evaluating...");
			for(String filePath : dev_filePaths){
				reader.open(IOUtils.createFileInputStream(filePath));			
				document = reader.getGoldCoNLLDocument();
				reader.close();
				
				prediction = component.decode(document);
				evaluator.getEvaluationTriple(document.o3, prediction);
			}
			
			currScore = evaluator.getAverageF1Score();
			System.out.println(" F1=" + currScore);
			if(Math.abs(prevScore - currScore) < threshold) break;
			prevScore = currScore;
		}
		
		System.out.println(".........\nDONE!");
	}
	
	private void evaluate(CoreferenceComponent component, CoreferenceTSVReader reader, AbstractEvaluator evaluator, List<String> eval_filePaths){
		component.setFlag(CFlag.DECODE);
		System.out.println("\nDECODING/EVALUATING...");
		
		CoreferantSet prediction;
		Triple<Double, Double, Double> evaluation;
		Triple<List<DEPTree>, List<AbstractMention>, CoreferantSet> document;
		
		
		for(String filePath : eval_filePaths){
			reader.open(IOUtils.createFileInputStream(filePath));
			document = reader.getGoldCoNLLDocument();
			reader.close();
			
			System.out.print("Decoding " + FileUtils.getBaseName(filePath) + "... ");
			prediction = component.decode(document);
			
			System.out.print("Evaluating... ");
			evaluation = evaluator.getEvaluationTriple(document.o3, prediction);
			System.out.println("DONE " + evaluation);
			
//			CoreferenceTestUtil.printCorefCluster(document.o2, prediction);
		}
	}
}
