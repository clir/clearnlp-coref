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

import java.util.List;

import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.type.CoreferenceLabel;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 9, 2015
 */
public class CoreferenceDecoder implements CoreferenceLabel{
	private StringModel model;
	private CoreferenceFeatureExtractor extractor;  
	
	public CoreferenceDecoder(StringModel model){
		this.model = model;
	}
	
	public StringPrediction[] predictAll(AbstractMention mention1, DEPTree tree1, AbstractMention mention2, DEPTree tree2){
		return model.predictAll(extractor.getFeatures(mention1, tree1, mention2, tree2));
	}
	
	public StringPrediction predictBest(AbstractMention mention1, DEPTree tree1, AbstractMention mention2, DEPTree tree2){
		return model.predictBest(extractor.getFeatures(mention1, tree1, mention2, tree2));
	}
	
	public CoreferantSet decode(List<DEPTree> trees, List<AbstractMention> mentions, boolean confidence){
		String label;
		DEPTree prev_tree, curr_tree;
		int i, j, size = mentions.size();
		AbstractMention prev_mention, curr_mention;
		CoreferantSet links = new CoreferantSet(size, confidence, true);
		
		for(i = size - 1; i > 0; i--){
			curr_mention = mentions.get(i);
			curr_tree = (curr_mention.hasTree())? curr_mention.getTree() : null;
			for(j = i - 1; j >= 0; j--){
				prev_mention = mentions.get(j);
				prev_tree = (prev_mention.hasTree())? prev_mention.getTree() : null;		
				
				label = predictBest(prev_mention, prev_tree, curr_mention, curr_tree).getLabel();
				if(label.equals(LINK))			links.union(j, i);
				else if(label.equals(SHIFT))	break;
			}
		}
		
		return links;
	}
}
