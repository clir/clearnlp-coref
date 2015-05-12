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
package edu.emory.clir.clearnlp.coreference.mention.common.detector;

import edu.emory.clir.clearnlp.constituent.CTLibEn;
import edu.emory.clir.clearnlp.coreference.mention.common.CommonNoun;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 12, 2015
 */
public class EnglishCommonNounDetector extends AbstractCommonNounDetector{
	private static final long serialVersionUID = 3474508950522490238L;
	
	public EnglishCommonNounDetector() { super(TLanguage.ENGLISH); }

	@Override
	public boolean isCommonNoun(DEPTree tree, DEPNode node) {
		return node.isPOSTag(CTLibEn.POS_NN) || node.isPOSTag(CTLibEn.POS_NNS) ; 
	}

	@Override
	public CommonNoun getCommonNoun(DEPTree tree, DEPNode node) {
		CommonNoun commonNoun = new CommonNoun(node.getLemma());
		
		// Setting common 3noun attributes 
		
		return commonNoun;
	}
	
}
