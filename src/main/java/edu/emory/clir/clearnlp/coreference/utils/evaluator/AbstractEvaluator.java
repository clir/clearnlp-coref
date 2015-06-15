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

import edu.emory.clir.clearnlp.collection.triple.Triple;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 15, 2015
 */
public abstract class AbstractEvaluator {	
	abstract public double evaluatePrecision(CoreferantSet key, CoreferantSet prediction);
	abstract public double evaluateRecall(CoreferantSet key, CoreferantSet prediction);
	
	public double evaluateF1Score(CoreferantSet key, CoreferantSet prediction){
		return evaluateF1Score(evaluatePrecision(key, prediction), evaluateRecall(key, prediction));
	}
	
	// Precision, Recall, F1
	public Triple<Double, Double, Double> getEvaluationTriple(CoreferantSet key, CoreferantSet prediction){
		return new Triple<>(evaluatePrecision(key, prediction), evaluateRecall(key, prediction), evaluateF1Score(key, prediction));
	}
	
	public double evaluateFScore(int f, CoreferantSet key, CoreferantSet prediction){
		return evaluateFScore(f, evaluatePrecision(key, prediction), evaluateRecall(key, prediction));
		
	}
	
	public double evaluateGMeasure(CoreferantSet key, CoreferantSet prediction){
		return evaluateGMeasure(evaluatePrecision(key, prediction), evaluateRecall(key, prediction));
	}
	
	// Protected methods
	protected double evaluateF1Score(double percision, double recall){
		return 2 * (percision * recall) / (percision + recall);
	}
	
	protected double evaluateFScore(int f, double percision, double recall){
		return (1 + f * f) * (percision * recall) / (f * f * percision + recall);
	}
	
	protected double evaluateGMeasure(double percision, double recall){
		return Math.sqrt(percision * recall);
	}
}
