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
package edu.emory.clir.clearnlp.coreference.coref.sieve;

import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.collection.triple.Triple;
import edu.emory.clir.clearnlp.coreference.AbstractCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.SieveSystemCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.config.MentionConfiguration;
import edu.emory.clir.clearnlp.coreference.config.SieveSystemCongiuration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.path.PathData;
import edu.emory.clir.clearnlp.coreference.sieve.ExactStringMatch;
import edu.emory.clir.clearnlp.coreference.sieve.RelaxedStringMatch;
import edu.emory.clir.clearnlp.coreference.sieve.SimplePronounMatch;
import edu.emory.clir.clearnlp.coreference.sieve.SpeakerIdentification;
import edu.emory.clir.clearnlp.coreference.utils.CoreferenceTestUtil;
import edu.emory.clir.clearnlp.coreference.utils.evaluator.CoreferenceBCubedEvaluator;
import edu.emory.clir.clearnlp.coreference.utils.reader.CoreferenceTSVReader;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Apr 13, 2015
 */
public class SieveSystemTest {
	
	public static int EVAL_DOCUMENT = 0;
	public static double PRECISION = 0d, RECALL = 0d, F1 = 0d;
	
	@Test
	@Ignore
	public void corefTest() throws IOException{
		/* Configuration */
		SieveSystemCongiuration config = new SieveSystemCongiuration(TLanguage.ENGLISH);
		config.loadDefaultMentionDetectors();
		config.loadDefaultSieves(true);
		/* ************* */
		
		AbstractCoreferenceResolution coref = new SieveSystemCoreferenceResolution(config);
		List<DEPTree> trees = CoreferenceTestUtil.getTestDocuments(PathData.ENG_MENTION, 0, 4);
		
		Pair<List<AbstractMention>, CoreferantSet> resolution = coref.getEntities(trees);
		CoreferenceTestUtil.printSentences(trees);
		CoreferenceTestUtil.printResolutionResult(resolution);
		CoreferenceTestUtil.printCorefCluster(resolution);
	}
	
	@Test
	public void BCubedEval(){
		CoreferenceBCubedEvaluator evaluator = new CoreferenceBCubedEvaluator();
		MentionConfiguration m_config = new MentionConfiguration(false, true, true);
		CoreferenceTSVReader reader = new CoreferenceTSVReader(m_config, true, false, 0, 1, 2, 3, 9, 4, 5, 6, -1, -1, 10);
		List<String> test_filePaths = FileUtils.getFileList("/Users/HenryChen/Desktop/conll-13/test", ".cnlp", true);
		
		/* Configuration */
		SieveSystemCongiuration config = new SieveSystemCongiuration(TLanguage.ENGLISH);
		config.loadMentionDetectors(false, true, true);
//		config.loadDefaultSieves(true);
		config.mountSieves(new SpeakerIdentification(), new ExactStringMatch(true), new RelaxedStringMatch(true), new SimplePronounMatch());
		AbstractCoreferenceResolution coref = new SieveSystemCoreferenceResolution(config);
		/* ************* */
		
		CoreferantSet prediction;
		Triple<Double, Double, Double> evaluation;
		Triple<List<DEPTree>, List<AbstractMention>, CoreferantSet> document;
		
		for(String filePath : test_filePaths){
			reader.open(IOUtils.createFileInputStream(filePath));
			document = reader.getGoldCoNLLDocument();
			reader.close();	EVAL_DOCUMENT++;
			
			System.out.print("Decoding " + FileUtils.getBaseName(filePath) + "... ");
			prediction = coref.getEntities(document.o1).o2;			
			
			System.out.print("Evaluating... ");
			evaluation = evaluator.getEvaluationTriple(document.o3, prediction);
			PRECISION += evaluation.o1;
			RECALL += evaluation.o2;
			F1 += evaluation.o3;
			System.out.println("DONE " + evaluation);
		}
		
		System.out.println("\nPerformance Summary:");
		System.out.println(evaluator.getEvaluationSummary());
	}
}
