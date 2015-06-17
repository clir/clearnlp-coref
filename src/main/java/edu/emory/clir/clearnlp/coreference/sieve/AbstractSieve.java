/**
 * Copyright 2014, Emory University
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
package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.List;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 23, 2015
 */
abstract public class AbstractSieve{
	public void resolute(List<DEPTree> trees, List<AbstractMention> mentions, CoreferantSet mentionLinks){
		AbstractMention curr, prev;
		int i, j, size = mentions.size();
		
//		for(i = size - 1; i > 0; i--){
		for (i = 1; i < size; i++){
			curr = mentions.get(i);
			
			for(j = i - 1; j >= 0; j--){
				prev = mentions.get(j);
				
				if(match(prev, curr)){
					if(!mentionLinks.isSameSet(i, j))
						mentionLinks.union(j, i);
					break;
				}
			}
		}
	}
	
	abstract public boolean match(AbstractMention prev, AbstractMention curr);
}
