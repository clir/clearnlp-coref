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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import edu.emory.clir.clearnlp.collection.pair.IntIntPair;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.coreference.config.MentionConfiguration;
import edu.emory.clir.clearnlp.coreference.mention.detector.AbstractMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.detector.EnglishMentionDetector;
import edu.emory.clir.clearnlp.coreference.utils.reader.CoreferenceTSVReader;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 11, 2015
 */
public class MentionRecallTest {
	private static final boolean STRICT = false;
	private static int DOCUMENT = 0, CORRECT = 0, TOTAL = 0;
	
	@Test
	@Ignore
	public void testOmission(){
		List<String> l_filePaths = FileUtils.getFileList("/Users/HenryChen/Desktop/conll-12", ".cnlp", true);
//		List<String> l_filePaths = FileUtils.getFileList("/Users/HenryChen/Desktop/conll-13", ".cnlp", true);
		
		List<AbstractMention> mentions;
		Pair<List<DEPTree>, Map<Integer, List<ObjectIntPair<IntIntPair>>>> document;
		CoreferenceTSVReader reader = new CoreferenceTSVReader(0, 1, 2, 3, 9, 4, 5, 6, -1, -1, 10);
		AbstractMentionDetector m_detector = new EnglishMentionDetector(new MentionConfiguration(true, true, true));
		
		IntIntPair result;
		for(String filePath : l_filePaths){
			reader.open(IOUtils.createFileInputStream(filePath));
			DOCUMENT++;
			
			document = reader.getCoNLLDocumentMentionSpan();
			mentions = m_detector.getMentionList(document.o1);
			result = (STRICT)? testOmission_Strict(document.o1, mentions, document.o2) : testOmission_Relax(document.o1, mentions, document.o2);
			
			TOTAL += result.i2;
			CORRECT += result.i1;
			
			reader.close();
		}
		
		System.out.println("Document count: " + DOCUMENT);
		System.out.printf("Recall result: %d/%d, %.3f%%", CORRECT, TOTAL, (double)CORRECT/TOTAL*100);
	}
	
	@Test
	@Ignore
	public void testSurplus(){
		List<String> l_filePaths = FileUtils.getFileList("/Users/HenryChen/Desktop/conll-12", ".cnlp", true);
//		List<String> l_filePaths = FileUtils.getFileList("/Users/HenryChen/Desktop/conll-13", ".cnlp", true);
		
		List<AbstractMention> mentions;
		Pair<List<DEPTree>, Map<Integer, List<ObjectIntPair<IntIntPair>>>> document;
		CoreferenceTSVReader reader = new CoreferenceTSVReader(0, 1, 2, 3, 9, 4, 5, 6, -1, -1, 10);
		AbstractMentionDetector m_detector = new EnglishMentionDetector(new MentionConfiguration(true, true, true));
		
		IntIntPair result;
		for(String filePath : l_filePaths){
			reader.open(IOUtils.createFileInputStream(filePath));
			DOCUMENT++;
			
			document = reader.getCoNLLDocumentMentionSpan();
			mentions = m_detector.getMentionList(document.o1);
			result = testSurplus_Relax(document.o1, mentions, document.o2);
			
			TOTAL += result.i2;
			CORRECT += result.i1;
			
			reader.close();
		}
		
		System.out.println("Document count: " + DOCUMENT);
		System.out.printf("Recall result: %d/%d, %.3f%%", CORRECT, TOTAL, (double)CORRECT/TOTAL*100);
	}
	
	private IntIntPair testOmission_Strict(List<DEPTree> trees, List<AbstractMention> mentions, Map<Integer, List<ObjectIntPair<IntIntPair>>> clusters){
		int correctCount = 0, totalCount = 0;
		
		Set<DEPNode> mentionNodes = new HashSet<>();
		for(AbstractMention mention : mentions)	
			if(!mention.isMultipleMention())	mentionNodes.add(mention.getNode());
		
		int i; DEPTree tree; DEPNode node;
		for(Entry<Integer, List<ObjectIntPair<IntIntPair>>> cluster : clusters.entrySet()){
			for(ObjectIntPair<IntIntPair> span : cluster.getValue()){
				tree = trees.get(span.i);
				
				for(i = span.o.i1; i < span.o.i2; i++){
					node = tree.get(i);
					if(mentionNodes.contains(node)){
						correctCount++;	break;
					}
				}
				
				totalCount++;
			}
		}
		
		return new IntIntPair(correctCount, totalCount);
	}
	
	private IntIntPair testOmission_Relax(List<DEPTree> trees, List<AbstractMention> mentions, Map<Integer, List<ObjectIntPair<IntIntPair>>> clusters){
		int correctCount = 0, totalCount = 0;
		
		Set<DEPNode> mentionNodes = new HashSet<>();
		for(AbstractMention mention : mentions)	
			if(mention.hasSubTreeNodes())	mentionNodes.addAll(mention.getSubTreeNodes());
		
		int i; DEPTree tree; DEPNode node;
		for(Entry<Integer, List<ObjectIntPair<IntIntPair>>> cluster : clusters.entrySet()){
			for(ObjectIntPair<IntIntPair> span : cluster.getValue()){
				tree = trees.get(span.i);
				
				for(i = span.o.i1; i < span.o.i2; i++){
					node = tree.get(i);
					if(mentionNodes.contains(node)){
						correctCount++;	break;
					}
				}
				
				totalCount++;
			}
		}
		
		return new IntIntPair(correctCount, totalCount);
	}
	
	private IntIntPair testSurplus_Relax(List<DEPTree> trees, List<AbstractMention> mentions, Map<Integer, List<ObjectIntPair<IntIntPair>>> clusters){
		int surplusCount = 0, totalCount = 0;
		
		int i; DEPTree tree;
		Set<DEPNode> mentionNodes = new HashSet<>();
		for(Entry<Integer, List<ObjectIntPair<IntIntPair>>> cluster : clusters.entrySet()){
			for(ObjectIntPair<IntIntPair> span : cluster.getValue()){
				tree = trees.get(span.i);
				for(i = span.o.i1; i < span.o.i2; i++)	mentionNodes.add(tree.get(i));
				totalCount++;
			}
		}
		
		for(AbstractMention mention : mentions)
			if(mention.isMultipleMention())
				if(!DSUtils.hasIntersection(mentionNodes, mention.getSubMentions().stream().map(m -> m.getNode()).collect(Collectors.toSet())))
						surplusCount++;
			else if(!mentionNodes.contains(mention.getNode()))	surplusCount++;
		
		return new IntIntPair(surplusCount, totalCount);
	}
}
