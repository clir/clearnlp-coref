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
package edu.emory.clir.clearnlp.coreference.components;

import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.type.FeatureType;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 9, 2015
 */
public class CoreferenceFeatureExtractor implements FeatureType{

	public StringFeatureVector getFeatures(AbstractMention mention1, DEPTree tree1, AbstractMention mention2, DEPTree tree2){
		StringFeatureVector vector = new StringFeatureVector();
		
		if(!mention1.isMultipleMention() && !mention2.isMultipleMention()){
			/* Exact String Match */
			if(mention1.getSubTreeWordSequence().equals(mention2.getSubTreeWordSequence()))	vector.addFeature(ExactString, TRUE);
			else																			vector.addFeature(ExactString, FALSE);
		}
		
		return vector;
	}
}
