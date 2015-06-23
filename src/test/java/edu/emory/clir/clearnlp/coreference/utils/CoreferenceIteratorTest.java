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
package edu.emory.clir.clearnlp.coreference.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.mention.EnglishMention;
import edu.emory.clir.clearnlp.coreference.utils.structures.MentionPair;
import edu.emory.clir.clearnlp.coreference.utils.util.CoreferenceIterator;
import edu.emory.clir.clearnlp.dependency.DEPNode;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 19, 2015
 */
public class CoreferenceIteratorTest {
	@Test
	public void testPairwiseMentionIterator(){
		List<AbstractMention> mentions = new ArrayList<>();
		mentions.add(new EnglishMention(new DEPNode(0, "A")));
		mentions.add(new EnglishMention(new DEPNode(1, "B")));
		mentions.add(new EnglishMention(new DEPNode(2, "C")));
		mentions.add(new EnglishMention(new DEPNode(3, "D")));
		mentions.add(new EnglishMention(new DEPNode(4, "E")));
		
		MentionPair pair; 
		Iterator<MentionPair> it;
		
		it = CoreferenceIterator.pairwiseMentionIterator(mentions, false);
		while(it.hasNext()){
			pair = it.next();
			System.out.println(pair.m1.getWordFrom() + " <-> " + pair.m2.getWordFrom());
		}
		
		System.out.println();
		
		it = CoreferenceIterator.pairwiseMentionIterator(mentions, true);
		while(it.hasNext()){
			pair = it.next();
			System.out.println(pair.m1.getWordFrom() + " <-> " + pair.m2.getWordFrom());
		}
	}
}
