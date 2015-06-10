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
package edu.emory.clir.clearnlp.coreference.mention;

import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import edu.emory.clir.clearnlp.coreference.config.MentionConfiguration;
import edu.emory.clir.clearnlp.coreference.mention.detector.AbstractMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.detector.EnglishMentionDetector;
import edu.emory.clir.clearnlp.coreference.path.PathData;
import edu.emory.clir.clearnlp.coreference.type.AttributeType;
import edu.emory.clir.clearnlp.coreference.utils.CoreferenceTestUtil;
import edu.emory.clir.clearnlp.coreference.visualization.BratCorefVisualizer;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 15, 2015
 */
public class MentionFeatureTest {
	
	@Test
	@Ignore
	public void testQuote() {
		List<DEPTree> trees = CoreferenceTestUtil.getTestDocuments(PathData.ENG_MENTION_QUOTE);
		
		AbstractMentionDetector detector = new EnglishMentionDetector(new MentionConfiguration(true, true, true));
		List<AbstractMention> mentions = detector.getMentionList(trees);
		
		for(AbstractMention mention : mentions)
			System.out.println(mention.getNode().getWordForm() + " -> " + mention.hasFeature(AttributeType.QUOTE));
	}
	
	@Test
	public void testConjunctions() throws IOException{
		List<DEPTree> trees = CoreferenceTestUtil.getTestDocuments(PathData.ENG_MENTION_CONJUNCTION);
		
		AbstractMentionDetector detector = new EnglishMentionDetector(new MentionConfiguration(true, true, true));
		List<AbstractMention> mentions = detector.getMentionList(trees);
		
		for(AbstractMention mention : mentions)
			System.out.println(mention);
		
//		BratCorefVisualizer.export("/Users/HenryChen/Desktop/", "conjunctionTest", trees, mentions);
	}
}
