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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.relation.chunk.AbstractChucker;
import edu.emory.clir.clearnlp.relation.structure.Entity;
import edu.emory.clir.clearnlp.relation.structure.EntityAlias;
import edu.emory.clir.clearnlp.util.DSUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 7, 2015
 */
public class MainEntityEvaluator extends AbstractRelationExtrationEvaluator{

	public MainEntityEvaluator() {
		super(null);
	}
	
	public MainEntityEvaluator(AbstractChucker chunker) {
		super(chunker);
	}

	@Override
	public double evaluatePrecision(List<Entity> keys, List<Entity> predictions) {
		int correct = 0;
		
		String word;
		Set<String> prediction_wordFroms = new HashSet<>();
		for(Entity prediction : predictions)
			for(EntityAlias alias : prediction.getAliasList()){
				word = alias.getStippedWordForm(true);
				if(!word.isEmpty()) prediction_wordFroms.add(word);
			}
		
		for(Entity e_key : keys)
			if(matchAnyPredictions(e_key, prediction_wordFroms))	correct++;
		
		double precision = (double)correct / predictions.size();
		PrecisionCount++; PrecisionSumSore += precision;
		return precision;
	}

	@Override
	public double evaluateRecall(List<Entity> keys, List<Entity> predictions) {
		int correct = 0;
		
		String word;
		Set<String> prediction_wordFroms = new HashSet<>();
		for(Entity prediction : predictions)
			for(EntityAlias alias : prediction.getAliasList()){
				word = alias.getStippedWordForm(true);
				if(!word.isEmpty()) prediction_wordFroms.add(word);
			}
		
		for(Entity e_key : keys)
			if(matchAnyPredictions(e_key, prediction_wordFroms))	correct++;
		
		double recall =  (double)correct / keys.size();
		RecallCount++; RecallSumScore += recall;
		return recall;
	}
	
	private boolean matchAnyPredictions(Entity key, Set<String> wordFroms){
		Set<String> key_wordForms = key.getAliasList().stream().map(a -> a.getStippedWordForm(true)).collect(Collectors.toSet());
		return DSUtils.hasIntersection(key_wordForms, wordFroms);
	}
	
	public double evaluatePrecisionOnDocumentTitle(String title, List<Entity> predictions){
		int correct = 0;
		
		for(Entity prediction : predictions)
			for(EntityAlias alias : prediction)
				if(title.contains(alias.getStippedWordForm(false))){
					correct++; break;
				}
		
		double precision = (predictions.isEmpty())? 0d : (double)correct / predictions.size();
		PrecisionCount++; PrecisionSumSore += precision;
		return precision;
	}
	
	public double evaluateRecallOnDocumentTitle(String title, List<Entity> predictions){
		return 0d;
	}
}
