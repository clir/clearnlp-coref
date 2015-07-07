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


/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 7, 2015
 */
public abstract class AbstractEvaluator<T> {
	protected double PrecisionSumSore, RecallSumScore;
	protected int DocCount, PrecisionCount, RecallCount;
	
	abstract public double evaluatePrecision(T key, T prediction);
	abstract public double evaluateRecall(T key, T prediction);
	
	public Triple<Double, Double, Double> getEvaluationTriple(T key, T prediction){
		DocCount++;
		double 	precision = evaluatePrecision(key, prediction), 
				recall = evaluateRecall(key, prediction), 
				f1 = evaluateF1Score(precision, recall);
		return new Triple<>(precision, recall, f1);
	}
	
	public double evaluateF1Score(T key, T prediction){
		return evaluateF1Score(evaluatePrecision(key, prediction), evaluateRecall(key, prediction));
	}
	
	public double evaluateFScore(int f, T key, T prediction){
		return evaluateFScore(f, evaluatePrecision(key, prediction), evaluateRecall(key, prediction));
		
	}
	
	// Evaluation helper functions
	public double evaluateGMeasure(T key, T prediction){
		return evaluateGMeasure(evaluatePrecision(key, prediction), evaluateRecall(key, prediction));
	}
	
	protected double evaluateF1Score(double precision, double recall){
		return 2 * (precision * recall) / (precision + recall);
	}
	
	protected double evaluateFScore(int f, double precision, double recall){
		return (1 + f * f) * (precision * recall) / (f * f * precision + recall);
	}
	
	protected double evaluateGMeasure(double precision, double recall){
		return Math.sqrt(precision * recall);
	}
	
	// Stats
	public double getAveragePrecision(){
		return PrecisionSumSore/PrecisionCount;
	}
	
	public double getAverageRecall(){
		return RecallSumScore/RecallCount;
	}
	
	public double getAverageF1Score(){
		return evaluateF1Score(getAveragePrecision(), getAverageRecall());
	}
	
	public String getEvaluationSummary(){
		StringBuilder sb = new StringBuilder();
		NumberFormat formatter = new DecimalFormat("#0.000");
		double precision = getAveragePrecision(),
			   recall = getAverageRecall(); 
		
		sb.append("Evaluation document count: "); 	sb.append(DocCount);
		sb.append("\nPrecision: "); 				sb.append(formatter.format(precision*100));	sb.append("% out of " + PrecisionCount + " predictions.");
		sb.append("\nRecall: ");					sb.append(formatter.format(recall*100));	sb.append("% out of " + RecallCount + " entities.");
		sb.append("\nF1 Score: ");					sb.append(formatter.format(evaluateF1Score(precision, recall)*100) + "%");
		return sb.toString();
	}
}
