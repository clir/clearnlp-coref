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
package edu.emory.clir.clearnlp.coreference.utils.util;

import java.util.Iterator;
import java.util.List;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.utils.structures.MentionPair;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 19, 2015
 */
public class CoreferenceIterator {
	public static Iterator<MentionPair> pairwiseMentionIterator(List<AbstractMention> mentions, boolean reverseOrder){
		return (reverseOrder)? pairwiseMentionIterator_Reverse(mentions) : pairwiseMentionIterator(mentions);
	}
	
	private static Iterator<MentionPair> pairwiseMentionIterator(List<AbstractMention> mentions){
		Iterator<MentionPair> it = new Iterator<MentionPair>() {
			MentionPair pair;
			int i = 0, j = 1, size = mentions.size();
			
			@Override
			public MentionPair next() {
				if(hasNext()){
					pair = new MentionPair(mentions.get(i), mentions.get(j));
					if(++j >= size){ i ++; j = i+1; }
					return pair;
				}
				return null;
			}
			
			@Override
			public boolean hasNext() {
				return i < size - 1 && j <= size;
			}
		};
		return it;
	}
	
	private static Iterator<MentionPair> pairwiseMentionIterator_Reverse(List<AbstractMention> mentions){
		Iterator<MentionPair> it = new Iterator<MentionPair>() {
			MentionPair pair;
			int i = mentions.size() - 1, j = i - 1;
			
			@Override
			public MentionPair next() {
				if(hasNext()){
					pair = new MentionPair(mentions.get(j), mentions.get(i));
					if(--j < 0){ i --; j = i - 1; }
					return pair;
				}
				return null;
			}
			
			@Override
			public boolean hasNext() {
				return i > 0 && j >=0;
			}
		};
		return it;
	}
}
