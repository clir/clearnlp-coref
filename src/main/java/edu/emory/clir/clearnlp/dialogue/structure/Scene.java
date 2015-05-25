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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.emory.clir.clearnlp.util.Joiner;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 22, 2015
 */
public class Scene implements Serializable, Comparable<Scene>{
	private static final long serialVersionUID = 5315569961140509729L;
	
	private int sceneId;
	private int speakerCount;
	private Map<Integer, String> speakerIDs;
	private List<Utterance> utterances;
	
	public Scene(int id){
		sceneId = id;
		speakerCount = 0;
		speakerIDs = new HashMap<>();
		utterances = new ArrayList<>();
	}
	
	public int getId(){
		return sceneId;
	}
	
	public int addSpeaker(String speaker){
		if(speaker == null) return -1;
		if(!speakerIDs.containsValue(speaker))
			speakerIDs.put(speakerCount, speaker);
		return speakerCount++;
	}
	
	public Utterance addUtterance(int speakerID, String raw, String stripped, List<String> trees){
		Utterance utterance = new Utterance(speakerID, raw, stripped, trees);
		utterances.add(utterance);
		return utterance;
	}
	
	public Utterance addUtterance(int id, String raw){
		Utterance utterance = new Utterance(id, raw);
		utterances.add(utterance);
		return utterance;
	}

	@Override
	public int compareTo(Scene o) {
		return getId() - o.getId();
	}
	
	@Override
	public String toString(){
		return Joiner.join(utterances, "\n");
	}
}
