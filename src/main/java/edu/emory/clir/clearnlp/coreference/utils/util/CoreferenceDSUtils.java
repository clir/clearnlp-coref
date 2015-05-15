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

import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.constituent.CTLibEn;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 15, 2015
 */
public class CoreferenceDSUtils {
	
	public static <T extends Comparable<T>>boolean isSequence(T o1, T o2, T o3){
		return o1.compareTo(o2) < 0 && o2.compareTo(o3) < 0;
	}
	
	public static List<int[]> getQuotaionIndices(DEPTree tree){
		List<int[]> list = new ArrayList<>();
		
		int start = -1, end = -1;
		for(DEPNode node : tree){
			if(node.isPOSTag(CTLibEn.POS_LQ))	start = node.getID();
			else if(node.isPOSTag(CTLibEn.POS_RQ))	end = node.getID();
			
			if(start >= 0 && end >= 0){
				list.add(new int[]{start, end});
				start = -1;	end = -1;
			}
		}
		
		return list;
	}
}
