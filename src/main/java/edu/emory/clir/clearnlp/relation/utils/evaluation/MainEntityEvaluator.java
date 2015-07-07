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
package edu.emory.clir.clearnlp.relation.utils.evaluation;

import java.util.List;

import edu.emory.clir.clearnlp.coreference.utils.evaluator.AbstractEvaluator;
import edu.emory.clir.clearnlp.relation.structure.Entity;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 7, 2015
 */
public class MainEntityEvaluator extends AbstractEvaluator<List<Entity>>{

	@Override
	public double evaluatePrecision(List<Entity> key, List<Entity> prediction) {
		int correct = 0;
		
		for(Entity e_key : key)
			if(matchAnyPredictions(e_key, prediction))	correct++;
		return (double)correct / prediction.size();
	}

	@Override
	public double evaluateRecall(List<Entity> key, List<Entity> prediction) {
		double recall =  (double)key.size() / prediction.size();
		RecallCount++; RecallSumScore += recall;
		return recall;
	}
	
	private boolean matchAnyPredictions(Entity key, List<Entity> predictions){
		String key_wordForm = key.getFirstAlias().getNode().getWordForm();
		
		for(Entity prediction : predictions)
			if(prediction.getFirstAlias().getNode().getWordForm().equals(key_wordForm))
				return true;
		return false;
	}
}
