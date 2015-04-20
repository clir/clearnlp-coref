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
package edu.emory.clir.clearnlp.coreference.utils.structures;

import java.util.Arrays;

import edu.emory.clir.clearnlp.collection.pair.DoubleIntPair;
import edu.emory.clir.clearnlp.collection.set.DisjointSet;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Apr 13, 2015
 */
public class DisjointSetWithConfidence extends DisjointSet{

	private double[] confidence;
	
	public DisjointSetWithConfidence(int size) {
		// Initialize variables size
		super(size);
		confidence = new double[size];
		
		// Initialize variables values
		for(int i = 0; i < size; i++)	confidence[i] = -1d;
	}
	
	public int union(int head, int child, double score){
		confidence[child] = score;
		return union(head, child);
	}

	public DoubleIntPair getHeadConfidence(int id){
		return new DoubleIntPair(confidence[id], find(id));
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("Disjoint Set with Cofidence:\n");
		sb.append("Clusters:\t"+super.toString()+"\n");
		sb.append("Confidence:\t"+Arrays.toString(confidence)+"\n");
		
		return sb.toString();
	}
}
