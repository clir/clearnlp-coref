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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.emory.clir.clearnlp.collection.pair.IntIntPair;
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
		return o1.compareTo(o2) <= 0 && o2.compareTo(o3) <= 0;
	}
	
	public static List<IntIntPair[]> getQuotaionIndices(List<DEPTree> trees){
		List<IntIntPair[]> list = new ArrayList<>();
		
		DEPTree tree;
		int treeIndex, size = trees.size();
		IntIntPair leftQuote = null, rightQuote = null;
		
		for(treeIndex = 0; treeIndex < size; treeIndex++){
			tree = trees.get(treeIndex);
			for(DEPNode node : tree){
				
				if(node.isPOSTag(CTLibEn.POS_LQ))		leftQuote = new IntIntPair(treeIndex, node.getID()); 
				else if(node.isPOSTag(CTLibEn.POS_RQ))	rightQuote = new IntIntPair(treeIndex, node.getID());
				
				if(leftQuote != null && rightQuote != null){
					list.add(new IntIntPair[]{leftQuote, rightQuote});
					leftQuote = null;	rightQuote = null;
				}
			}
		}

		return list;
	}
	
	public static List<DEPNode> getConjunctions(DEPTree tree){
		List<DEPNode> list = new ArrayList<>();
		
		for(DEPNode node : tree)
			if(node.isPOSTag(CTLibEn.POS_CC))
				list.add(node);
		
		return list;
	}
	
	public static <T extends Comparable<T>>void sortListBySublistSizeThenHead(List<List<T>> list, boolean reverseOrder){
		Collections.sort(list, new Comparator<List<T>>() {
			@Override
			public int compare(List<T> o1, List<T> o2) {
				int size1 = o1.size(), size2 = o2.size();
				if(size1 == size2 && size1 > 0 && size2 > 0)
					return o1.get(0).compareTo(o2.get(0));
				return size1 - size2;
			}
		});
		if(reverseOrder) Collections.reverse(list);
	}
	
	public static <T extends Comparable<T>>int getOverlapCount(List<T> list1, List<T> list2, boolean sorted){
		if(!sorted){
			Collections.sort(list1);
			Collections.sort(list2);
		}
		
		if(list1.size() > list2.size()){
			List<T> temp = list1;
			list1 = list2; list2 = temp;
		}
		
		T l1_ele, l2_ele;
		int i = 0, j = 0, comp, count = 0, size1 = list1.size(), size2 = list2.size();
		
		for(; i < size1; i++){
			l1_ele = list1.get(i);
			
			for(; j < size2; j++){
				l2_ele = list2.get(j);
				
				comp = l1_ele.compareTo(l2_ele);
				if(comp > 0)		continue;
				else if(comp == 0){	j++; count++; }
				break;
			}
		}
		
		return count;
	}
}
