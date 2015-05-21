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

import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.coreference.mention.detector.AbstractMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.detector.EnglishMentionDetector;
import edu.emory.clir.clearnlp.coreference.path.PathData;
import edu.emory.clir.clearnlp.coreference.utils.CoreferenceTestUtil;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 19, 2015
 */
public class AbstractMentionTest {
	
	@Test 
	public void test() {
		List<DEPTree> trees = CoreferenceTestUtil.getTestDocuments(PathData.ENG_MENTION, 0, 10);
		
		AbstractMentionDetector detector = new EnglishMentionDetector();
		List<AbstractMention> mentions = detector.getMentionList(trees);
		
		testSubTreeWordSequence(mentions);
		testHeadWord(mentions);
		testAcronym(mentions);
	}
	
	public void testSubTreeWordSequence(List<AbstractMention> mentions) {
		for(AbstractMention mention : mentions)
			System.out.println(mention.getWordFrom() + " -> " + mention.getSubTreeWordSequence());
	}
	
	public void testHeadWord(List<AbstractMention> mentions) {
		for(AbstractMention mention : mentions)
			System.out.println(mention.getWordFrom() + " -> " + mention.getHeadNodeWordForm());
	}
	
	public void testAcronym(List<AbstractMention> mentions) {
		for(AbstractMention mention : mentions)
			System.out.println(mention.getWordFrom() + " -> " + mention.getAcronym());
	}
}
