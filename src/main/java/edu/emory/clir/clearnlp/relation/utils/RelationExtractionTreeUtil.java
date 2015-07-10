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
package edu.emory.clir.clearnlp.relation.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.emory.clir.clearnlp.dependency.DEPLibEn;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.ner.BILOU;
import edu.emory.clir.clearnlp.pos.POSLibEn;
import edu.emory.clir.clearnlp.util.DSUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 9, 2015
 */
public class RelationExtractionTreeUtil {
	public static final String DEPModSuffix = "mod";
	public static final String ignoredBILOUTag = BILOU.O.toString();
	public static final Set<String> ignoredWordForm = DSUtils.toHashSet("—", "’s", "n’t", "'", "-", "_", ",", "(", ")", "\"", ".", ":");
	public static final Set<String> ignoredDEPLabels = DSUtils.toHashSet(DEPLibEn.DEP_APPOS, DEPLibEn.DEP_PUNCT, DEPLibEn.DEP_AUX, 
																		 DEPLibEn.DEP_RELCL, DEPLibEn.DEP_POSS, DEPLibEn.DEP_DET, DEPLibEn.DEP_CASE);
	
	public static List<DEPNode> stripSubTree(List<DEPNode> l_subNodes){
		DEPNode node;
		Set<DEPNode> removingHeadNodes = new HashSet<>();
		for(int i = l_subNodes.size()-1; i >= 0; i--){
			node = l_subNodes.get(i);
			
			// Remove selected DEP Labels
			if(!node.isLabel(DEPLibEn.DEP_COMPOUND))
				removingHeadNodes.addAll(node.getDependentListByLabel(ignoredDEPLabels));
			
			// Remove selected word forms & modifiers
			if(ignoredWordForm.contains(node.getLowerSimplifiedWordForm()) 
					|| node.getLabel().endsWith(DEPModSuffix)
					|| POSLibEn.isAdjective(node.getPOSTag()))
				l_subNodes.remove(i);
		}
			
		if(!removingHeadNodes.isEmpty())
			for(DEPNode n : removingHeadNodes) l_subNodes.removeAll(n.getSubNodeSet());
		
		return l_subNodes;
	}
}
