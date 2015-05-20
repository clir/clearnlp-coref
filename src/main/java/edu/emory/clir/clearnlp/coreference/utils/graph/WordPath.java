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
package edu.emory.clir.clearnlp.coreference.utils.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 19, 2015
 */
public class WordPath implements Comparable<WordPath> {
	private double weight;
	private List<WordNode> path;
	
	public WordPath(){
		weight = 0;
		path = new ArrayList<>();
	}
	
	public WordPath(WordNode startNode){
		weight = 0;
		path = new ArrayList<>();
		path.add(startNode);
	}
	
	public double getWeight(){
		return weight;
	}
	
	public List<WordNode> getPath(){
		return path;
	}
	
	public void setWeight(double w){
		weight = w;
	}
	
	public void addNode(WordNode node, double w){
		weight += w;
		path.add(node);
	}
	
	@Override
	public int compareTo(WordPath p){
		return (int)Math.signum(getWeight() - p.getWeight());
	}
}
