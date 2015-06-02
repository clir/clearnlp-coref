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

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.coreference.AbstractCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.SieveSystemCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.config.CorefCongiuration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.path.PathData;
import edu.emory.clir.clearnlp.coreference.utils.CoreferenceTestUtil;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSet;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.FileUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 27, 2015
 */
public class SieveSystemTest {

	@Test
	public void test(){
		/* Configuration */
		CorefCongiuration config = new CorefCongiuration();
		config.loadMentionDectors(true, true, true);
		config.loadDefaultSieves(true, false, false, false, false, false, false);
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
			break;
		}
	}
}
