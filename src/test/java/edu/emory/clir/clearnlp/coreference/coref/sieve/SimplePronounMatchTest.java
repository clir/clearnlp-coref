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

import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.coreference.AbstractCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.SieveSystemCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.config.SieveSystemCongiuration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.path.PathData;
import edu.emory.clir.clearnlp.coreference.sieve.SimplePronounMatch;
import edu.emory.clir.clearnlp.coreference.utils.CoreferenceTestUtil;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSet;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 8, 2015
 */
public class SimplePronounMatchTest {
	@Test
	public void test(){
		/* Configuration */
		SieveSystemCongiuration config = new SieveSystemCongiuration(TLanguage.ENGLISH);
		config.loadMentionDectors(true, true, true);
		config.mountSieves(new SimplePronounMatch());
		/* ************* */
		
		AbstractCoreferenceResolution coref = new SieveSystemCoreferenceResolution(config);
		List<String> l_filePaths = FileUtils.getFileList(PathData.ENG_COREF_MICROSOFT_PARSED_DIR, ".cnlp", false);
		
		List<DEPTree> trees;
		Pair<List<AbstractMention>, DisjointSet> resolution;
		for(String filePath : l_filePaths){
			trees = CoreferenceTestUtil.getTestDocuments(filePath, 9);
			resolution = coref.getEntities(trees);
			
			CoreferenceTestUtil.printSentences(trees);
			CoreferenceTestUtil.printResolutionResult(resolution);
			CoreferenceTestUtil.printCorefCluster(resolution);
		}
	}
}
