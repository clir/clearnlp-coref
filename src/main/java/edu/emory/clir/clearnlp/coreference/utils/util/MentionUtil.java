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

import java.util.List;

import edu.emory.clir.clearnlp.constituent.CTLibEn;
import edu.emory.clir.clearnlp.coreference.mention.MultipleMention;
import edu.emory.clir.clearnlp.coreference.mention.SingleMention;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.util.DSUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 20, 2015
 */
public class MentionUtil {
	@SuppressWarnings("unchecked")
	public static List<SingleMention>[] groupMentions(List<SingleMention> mentions){
		if(!mentions.isEmpty()){
			int i, j, size = mentions.size(), count = 0; 
			int[] groupID = new int[size]; groupID[0] = ++count; 
			
			SingleMention current;
			for(i = 1; i < size; i++){
				current = mentions.get(i);
				for(j = i-1; j >= 0; j--)
					if(mentions.get(j).isParentMention(current))	groupID[i] = groupID[j];
				if(groupID[i] == 0) groupID[i] = ++count;
			}
			
			List<SingleMention>[] groups = (List<SingleMention>[]) DSUtils.createEmptyListArray(count);
			for(i = 0; i < size; i++)	groups[groupID[i]-1].add(mentions.get(i));
			return groups;
		}
		return null;
	}
	
	public static boolean hasConjunctionRelations(SingleMention mention1, SingleMention mention2){
		if(mention1.getTree() == mention2.getTree() && mention1.isParentMention(mention2)){
			for(DEPNode node : mention1.getSubTreeNodes())
				if(node.isPOSTag(CTLibEn.POS_CC) || node.isPOSTag(CTLibEn.POS_COMMA))	return true;
				else if(mention2.getNode() == node)										break;
		}
		return false;
	}
	
	public static boolean hasConjunctionRelations(List<SingleMention> mentions){
		int i, size = mentions.size();
		for(i = 0; i < size-1; i++)
			if(!hasConjunctionRelations(mentions.get(i), mentions.get(i+1)))
				return false;
		return true;
	}
	
	public static MultipleMention mergeSingleMentions(List<SingleMention> mentions){
		return new MultipleMention(mentions);
	}
	
	public static MultipleMention mergeSingleMentions(SingleMention... mentions){
		return new MultipleMention(mentions);
	}
}
