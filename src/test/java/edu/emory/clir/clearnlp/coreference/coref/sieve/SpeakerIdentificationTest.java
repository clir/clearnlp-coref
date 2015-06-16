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

import edu.emory.clir.clearnlp.NLPDecoder;
import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.coreference.AbstractCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.SieveSystemCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.config.SieveSystemCongiuration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.path.PathData;
import edu.emory.clir.clearnlp.coreference.sieve.SpeakerIdentification;
import edu.emory.clir.clearnlp.coreference.utils.CoreferenceTestUtil;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 29, 2015
 */
public class SpeakerIdentificationTest {
	@Test
	public void testSpeakerIdentification() throws IOException{
		SieveSystemCongiuration config = new SieveSystemCongiuration(TLanguage.ENGLISH);
		config.loadDefaultMentionDectors();
		config.mountSieves(new SpeakerIdentification());
		
		AbstractCoreferenceResolution coref = new SieveSystemCoreferenceResolution(config); 
		List<DEPTree> trees = CoreferenceTestUtil.getTestDocuments(PathData.ENG_MENTION_QUOTE);
//		List<DEPTree> trees = new NLPDecoder(TLanguage.ENGLISH).toDEPTrees("\"I love this so much,\" said Tom, \"but I hate it with noodles.\"");
		
		Pair<List<AbstractMention>, CoreferantSet> resolution = coref.getEntities(trees);
		CoreferenceTestUtil.printSentences(trees);
		CoreferenceTestUtil.printResolutionResult(resolution);
		CoreferenceTestUtil.printCorefCluster(resolution);
	}
}
