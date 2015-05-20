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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import edu.emory.clir.clearnlp.coreference.mention.detector.AbstractMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.detector.EnglishMentionDetector;
import edu.emory.clir.clearnlp.coreference.type.MentionAttributeType;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 15, 2015
 */
public class MentionFeatureTest {
	
	@Test
	@Ignore
	public void testQuote() throws IOException{
		InputStream in = new FileInputStream("src/test/resources/edu/emory/clir/clearnlp/coreference/mention/input.mention.quote.cnlp");
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		reader.open(in);
		
		DEPTree tree;
		List<SingleMention> mentions;
		List<DEPTree> trees = new ArrayList<>();
		
		while ((tree = reader.next()) != null) trees.add(tree);
		
		AbstractMentionDetector detector = new EnglishMentionDetector();
		
		mentions = detector.getMentionList(trees);
		
		for(SingleMention mention : mentions)
			System.out.println(mention.getNode().getWordForm() + " -> " + mention.hasFeature(MentionAttributeType.QUOTE));
	}
	
	@Test
	public void testConjunctions() throws IOException{
		InputStream in = new FileInputStream("src/test/resources/edu/emory/clir/clearnlp/coreference/mention/input.mention.conjunctions.cnlp");
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		reader.open(in);
		
		DEPTree tree;
		List<SingleMention> mentions;
		List<DEPTree> trees = new ArrayList<>();
		
		while ((tree = reader.next()) != null) trees.add(tree);
		
		AbstractMentionDetector detector = new EnglishMentionDetector();
		
		mentions = detector.getMentionList(trees);
		
//		for(SingleMention mention : mentions)	System.out.println(mention);
	}
}
