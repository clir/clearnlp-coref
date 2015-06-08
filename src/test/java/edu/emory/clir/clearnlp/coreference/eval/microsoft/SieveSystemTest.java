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
package edu.emory.clir.clearnlp.coreference.eval.microsoft;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.coreference.AbstractCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.SieveSystemCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.config.AbstractCorefConfiguration;
import edu.emory.clir.clearnlp.coreference.config.SieveSystemCongiuration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.path.PathData;
import edu.emory.clir.clearnlp.coreference.path.PathVisualization;
import edu.emory.clir.clearnlp.coreference.sieve.ExactStringMatch;
import edu.emory.clir.clearnlp.coreference.sieve.RelaxedStringMatch;
import edu.emory.clir.clearnlp.coreference.sieve.SimplePronounMatch;
import edu.emory.clir.clearnlp.coreference.sieve.SpeakerIdentification;
import edu.emory.clir.clearnlp.coreference.utils.CoreferenceTestUtil;
import edu.emory.clir.clearnlp.coreference.utils.CorpusReconstructor;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSet;
import edu.emory.clir.clearnlp.coreference.visualization.BratCorefVisualizer;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 27, 2015
 */
public class SieveSystemTest {

	@Test
	@Ignore
	public void testAllSieves(){
		/* Configuration */
		SieveSystemCongiuration config = new SieveSystemCongiuration(TLanguage.ENGLISH);
		config.loadMentionDectors(true, true, true);
		config.loadDefaultSieves(true, true, true, true, true, true, true, true);
		/* ************* */
		
		testCorefSieveSystem(config);
	}
	
	@Test
	public void testSelectedSieves(){
		/* Configuration */
		SieveSystemCongiuration config = new SieveSystemCongiuration(TLanguage.ENGLISH);
		config.loadMentionDectors(true, true, true);
		config.mountSieves(new SpeakerIdentification(), new ExactStringMatch(), new RelaxedStringMatch(), new SimplePronounMatch());
		/* ************* */
		
		testCorefSieveSystem(config);
	}
	
	public void testCorefSieveSystem(AbstractCorefConfiguration config){
		AbstractCoreferenceResolution coref = new SieveSystemCoreferenceResolution((SieveSystemCongiuration)config);
		BratCorefVisualizer annotator = new BratCorefVisualizer(PathVisualization.MS_DATA);
		List<String> l_filePaths = FileUtils.getFileList(PathData.ENG_COREF_MICROSOFT_PARSED_DIR, ".cnlp", false);
		
		List<DEPTree> trees;
		Pair<List<AbstractMention>, DisjointSet> resolution;
		for(String filePath : l_filePaths){
			trees = CoreferenceTestUtil.getTestDocuments(filePath, 9);
			resolution = coref.getEntities(trees);
			
			CoreferenceTestUtil.printSentences(trees);
			CoreferenceTestUtil.printResolutionResult(resolution);
			CoreferenceTestUtil.printCorefCluster(resolution);
			
			CorpusReconstructor.reconstruct(trees, resolution.o1, resolution.o2, "/Users/HenryChen/Desktop/MS_Output/"+FileUtils.getBaseName(filePath));
//			annotator.export(FileUtils.getBaseName(filePath), trees, resolution.o1, resolution.o2);
		}
	}
}
