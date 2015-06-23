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

import java.util.Arrays;
import java.util.List;

import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
import edu.emory.clir.clearnlp.coreference.utils.util.CoreferenceDSUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 15, 2015
 */
public class BCubedEvaluator extends AbstractEvaluator{
	
	@Override
	public double evaluatePrecision(CoreferantSet key, CoreferantSet prediction) {
		return evaluatePrecision(null, key, prediction);
	}
	
	@Override
	public double evaluateRecall(CoreferantSet key, CoreferantSet prediction) {
		return evaluateRecall(null, key, prediction);
	}
	
	public double evaluateF1Score(double[] p_weights, double[] r_weights, CoreferantSet key, CoreferantSet prediction){
		return evaluateF1Score(evaluatePrecision(p_weights, key, prediction), evaluateRecall(r_weights, key, prediction));
	}
	
	public double evaluateRecall(double[] s_weights, CoreferantSet key, CoreferantSet prediction) {
		List<List<Integer>> l_truthChains = key.getClusterLists(includeSingleton), l_outputChains = prediction.getClusterLists(includeSingleton);
		
		double score = 0d;
		List<Integer> truthChain, outputChain;
		int i, i_truthChain, i_outputChain = 0, truth_size = l_truthChains.size(), output_size = l_outputChains.size();
		double[] s_scores = new double[truth_size];
		
		for(i_truthChain = 0; i_truthChain < truth_size; i_truthChain++){
			truthChain = l_truthChains.get(i_truthChain);
			for(; i_outputChain < output_size; i_outputChain++){
				outputChain = l_outputChains.get(i_outputChain);

				if(truthChain.get(0) == outputChain.get(0)){
					s_scores[i_truthChain] = (double) CoreferenceDSUtils.getOverlapCount(truthChain, outputChain, true) / truthChain.size();
					
					i_outputChain++;
					break;
				}
				if(truthChain.get(0) < outputChain.get(0))	break;	
			}
		}
		
		if(s_weights == null){
			s_weights = new double[truth_size];
			Arrays.fill(s_weights, 1d);
		}
		
		double singleScore;
		for(i = 0; i < truth_size; i++){
			singleScore = s_weights[i] * s_scores[i];
			score += singleScore; 
			RecallSumScore+= singleScore;
		}
		
		RecallCount += truth_size;
		return score / truth_size;
	}

	
	
	public double evaluatePrecision(double[] s_weights, CoreferantSet key, CoreferantSet prediction) {
		List<List<Integer>> l_truthChains = key.getClusterLists(includeSingleton), l_outputChains = prediction.getClusterLists(includeSingleton);
		
		double score = 0d;
		List<Integer> truthChain, outputChain;
		int i, j, i_truthChain, i_outputChain = 0, truth_size = l_truthChains.size(), output_size = l_outputChains.size();
		int[] truth_index = new int[output_size];
		double[] s_scores = new double[output_size];
		
		for(i_truthChain = 0; i_truthChain < truth_size; i_truthChain++){
			truthChain = l_truthChains.get(i_truthChain);
			for(; i_outputChain < output_size; i_outputChain++){
				outputChain = l_outputChains.get(i_outputChain);
				
				if(truthChain.get(0) == outputChain.get(0)){
					truth_index[i_outputChain] = i_truthChain;
					s_scores[i_outputChain] = (double) CoreferenceDSUtils.getOverlapCount(truthChain, outputChain, true) / outputChain.size();
					
					i_outputChain++;
					break;
				}
				if(truthChain.get(0) < outputChain.get(0))	break;	
			}
		}
		
		if(s_weights == null){
			s_weights = new double[truth_size];
			Arrays.fill(s_weights, 1d);
		}
		
		double singleScore;
		for(i = 0, j = 0; i < output_size; i++){
			singleScore = (s_scores[i] == 0)? 0 : s_weights[j++] * s_scores[i];
			score += singleScore;
			PrecisionSumSore += singleScore;
		}
			 
		PrecisionCount += output_size;
		return score / output_size;
	}
}
