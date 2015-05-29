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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.emory.clir.clearnlp.util.Joiner;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 24, 2015
 */
public class Episode implements Serializable, Comparable<Episode> {
	private static final long serialVersionUID = 6820752751873428267L;

	private int episodeId;
	private Map<Integer, Scene> scenes;
	
	public Episode(int id){
		episodeId = id;
		scenes = new TreeMap<>();
	}
	
	public int getId(){
		return episodeId;
	}
	
	public List<Scene> getScenes(){
		List<Scene> list = new ArrayList<>(scenes.values());
		Collections.sort(list);
		return list;
	}
	
	public Scene addScene(int id){
		return scenes.computeIfAbsent(id, s -> new Scene(id));
	}

	@Override
	public int compareTo(Episode o) {
		return getId() - o.getId();
	}
	
	@Override
	public String toString(){
		return Joiner.join(scenes.values(), "\n");
	}
}
