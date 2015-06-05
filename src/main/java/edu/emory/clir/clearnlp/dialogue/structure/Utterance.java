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
package edu.emory.clir.clearnlp.dialogue.structure;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.AbstractReader;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 22, 2015
 */
public class Utterance implements Serializable{
	private static final long serialVersionUID = -2554816743533036963L;
	
	private int spearkerId;
	private String utterance;
	private String statement;
	private List<String> trees;
	
	public Utterance(int id, String raw){
		spearkerId = id;
		utterance = raw;
		statement = null;
		trees = null;
	}

	public Utterance(int id, String raw, String stripped, List<String> treeList){
		spearkerId = id;
		utterance = raw;
		statement = stripped;
		trees = treeList;
	}
	
	public int getSpeakerId(){
		return spearkerId;
	}
	
	public String getUtterance(){
		return utterance;
	}
	
	public String getStatement(){
		return statement;
	}
	
	public int getTreeCount(){
		return (trees == null)? -1 : trees.size();
	}
	
	public List<String> getRawTrees(){
		return trees;
	}
	
	public List<DEPTree> getDEPTrees(AbstractReader<DEPTree> reader){
		List<DEPTree> list = new ArrayList<>();
		if(trees != null){
			for(String tree : trees){
				reader.open(new ByteArrayInputStream(tree.getBytes()));
				list.add(reader.next());
			}
		}
		return list;
	}
	
	@Override
	public String toString(){
		return spearkerId + ":\t" + utterance;
	}
}
