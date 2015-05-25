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

import java.io.Serializable;
import java.util.List;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 22, 2015
 */
public class Utterance implements Serializable{
	private static final long serialVersionUID = -2554816743533036963L;
	
	private int spearkID;
	private String line_raw;
	private String line_stripped;
	private List<String> l_trees;
	
	public Utterance(int id, String raw){
		spearkID = id;
		line_raw = raw;
		line_stripped = null;
		l_trees = null;
	}
	
	public Utterance(int id, String raw, String stripped, List<String> trees){
		spearkID = id;
		line_raw = raw;
		line_stripped = stripped;
		l_trees = trees;
	}
	
	@Override
	public String toString(){
		return spearkID + ":\t" + line_raw;
	}
}
