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

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.coreference.AbstractCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.SieveSystemCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.config.CorefConfiguration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.path.PathData;
import edu.emory.clir.clearnlp.coreference.utils.CoreferenceTestUtil;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSet;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Apr 13, 2015
 */
public class SieveSystemTest {
	
	@Test
	public void corefTest() throws IOException{
		/* Configuration */
		CorefConfiguration config = new CorefConfiguration();
		config.loadDefaultMentionDectors();
		config.loadDefaultSieves(false, false, false, false, false, false, false);
		/* ************* */
		
		AbstractCoreferenceResolution coref = new SieveSystemCoreferenceResolution(config);
		List<DEPTree> trees = CoreferenceTestUtil.getTestDocuments(PathData.ENG_MENTION, 0, 4);
		
		Pair<List<AbstractMention>, DisjointSet> resolution = coref.getEntities(trees);
		CoreferenceTestUtil.printSentences(trees);
		CoreferenceTestUtil.printResolutionResult(resolution);
		CoreferenceTestUtil.printCorefCluster(resolution);
	}
}
