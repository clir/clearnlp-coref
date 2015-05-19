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

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 19, 2015
 */
public class WordEdge implements Comparable<WordEdge>{
	private WordNode source;
	private WordNode target;
	private double weight;
	
	public WordEdge(WordNode s, WordNode t){
		source = s;
		target = t;
		weight = -1;
	}
	
	public WordEdge(WordNode s, WordNode t, double w){
		source = s;
		target = t;
		weight = w;
	}
	
	public WordNode getSoruce(){
		return source;
	}
	
	public WordNode getTarget(){
		return target;
	}
	
	public double getWeight(){
		return weight;
	}

	@Override
	public int compareTo(WordEdge o) {
		return (int)Math.signum(weight - o.getWeight());
	}
	
	@Override
	public String toString(){
		return source + "\t->\t" + target + "\t" + weight;
	}
}
