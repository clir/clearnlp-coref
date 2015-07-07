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
package edu.emory.clir.clearnlp.coreference.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.emory.clir.clearnlp.coreference.utils.evaluator.CoreferenceBCubedEvaluator;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 15, 2015
 */
public class BCubedEvaluatorTest {
	@Test
	public void testRecall(){
		CoreferenceBCubedEvaluator evaluator = new CoreferenceBCubedEvaluator();
		CoreferantSet key = new CoreferantSet(10), prediction = new CoreferantSet(10);
		
		key.union(0, 1); key.union(1, 3);
		key.union(2, 4); key.union(4, 5);
		key.union(6, 7); key.union(8, 9);
		
		prediction.union(1, 3);
		prediction.union(4, 5);
		prediction.union(6, 7);
		prediction.union(8, 9);
		
		assertEquals(evaluator.evaluateRecall(key, prediction), (double)2/3, 0);
		System.out.println(evaluator.getEvaluationTriple(key, prediction));
	}
	
	@Test
	public void testPercision(){
		CoreferenceBCubedEvaluator evaluator = new CoreferenceBCubedEvaluator();
		CoreferantSet key = new CoreferantSet(10), prediction = new CoreferantSet(10);
		
		key.union(0, 1); key.union(1, 3);
		key.union(2, 4); key.union(4, 5);
		key.union(6, 7); key.union(8, 9);
		
		prediction.union(0, 1);
		prediction.union(1, 2);
		prediction.union(2, 3);
		prediction.union(3, 4);
		prediction.union(4, 5);
		prediction.union(5, 6);
		prediction.union(6, 7);
		prediction.union(8, 9);
		
		System.out.println(key.getClusterLists(true));
		System.out.println(prediction.getClusterLists(true));
		
		assertEquals(evaluator.evaluatePrecision(key, prediction), (double)3/16, 0);
		System.out.println(evaluator.getEvaluationTriple(key, prediction));
	}
}
