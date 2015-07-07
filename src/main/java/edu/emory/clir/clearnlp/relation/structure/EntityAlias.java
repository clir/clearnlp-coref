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
package edu.emory.clir.clearnlp.relation.structure;

import java.io.Serializable;

import edu.emory.clir.clearnlp.collection.map.IntIntHashMap;
import edu.emory.clir.clearnlp.dependency.DEPNode;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 5, 2015
 */
public class EntityAlias implements Serializable{
	private static final long serialVersionUID = -7908795263358761403L;
	
	private int i_sentenceId;
	private DEPNode n_node;
	private double d_weight;
	
	public EntityAlias(int sentenceId, DEPNode node){
		i_sentenceId = sentenceId;
		n_node = node;
	}
	
	public EntityAlias(int sentenceId, DEPNode node, double weight){
		i_sentenceId = sentenceId;
		n_node = node;
		setWeight(weight);
	}
	
	public int getSentenceId(){
		return i_sentenceId;
	}
	
	public DEPNode getNode(){
		return n_node;
	}
	
	public double getWeight(){
		return d_weight;
	}
	
	public void setWeight(double weight){
		d_weight= weight;
	}
}
