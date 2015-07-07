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
package edu.emory.clir.clearnlp.coreference.utils.evaluator;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.emory.clir.clearnlp.collection.triple.Triple;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 15, 2015
 */
public abstract class AbstractCoreferenceEvaluator extends AbstractEvaluator<CoreferantSet> {	
	protected boolean includeSingleton;
	protected int MentionCount;
	
	public AbstractCoreferenceEvaluator(){
		includeSingleton = false;
	}
	
	public AbstractCoreferenceEvaluator(boolean includeSingleton){
		this.includeSingleton = includeSingleton;
	}
	
	abstract public double evaluatePrecision(CoreferantSet key, CoreferantSet prediction);
	abstract public double evaluateRecall(CoreferantSet key, CoreferantSet prediction);
	
	// Precision, Recall, F1
	@Override
	public Triple<Double, Double, Double> getEvaluationTriple(CoreferantSet key, CoreferantSet prediction){
		DocCount++; MentionCount+=key.size();
		double precision = evaluatePrecision(key, prediction), recall = evaluateRecall(key, prediction), f1 = evaluateF1Score(precision, recall);
		return new Triple<>(precision, recall, f1);
	}
	
	@Override
	public String getEvaluationSummary(){
		StringBuilder sb = new StringBuilder();
		NumberFormat formatter = new DecimalFormat("#0.000");
		double precision = getAveragePrecision(),
			   recall = getAverageRecall(); 
		
		sb.append("Evaluation document count: "); 	sb.append(DocCount);
		sb.append("\nTotal mention count: "); 		sb.append(MentionCount);
		sb.append("\nPrecision: "); 				sb.append(formatter.format(precision*100));	sb.append("% out of " + PrecisionCount + " predictions.");
		sb.append("\nRecall: ");					sb.append(formatter.format(recall*100));	sb.append("% out of " + RecallCount + " entities.");
		sb.append("\nF1 Score: ");					sb.append(formatter.format(evaluateF1Score(precision, recall)*100) + "%");
		return sb.toString();
	}
}
