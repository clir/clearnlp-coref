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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.emory.clir.clearnlp.util.Joiner;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 24, 2015
 */
public class Season implements Serializable, Comparable<Season>, Iterable<Episode> {
	private static final long serialVersionUID = 1949692768773037475L;
	
	private int seasonId;
	private Map<Integer, Episode> episodes;
	
	public Season(int id){
		seasonId = id;
		episodes = new TreeMap<>();
	}
	
	public int getId(){
		return seasonId;
	}
	
	public List<Episode> getEpisodes(){
		List<Episode> list = new ArrayList<>(episodes.values());
		Collections.sort(list);
		return list;
	}
	
	public Episode addEpisode(int id){
		return episodes.computeIfAbsent(id, e -> new Episode(id));
	}

	@Override
	public int compareTo(Season o) {
		return getId() - o.getId();
	}
	
	@Override
	public String toString(){
		return Joiner.join(episodes.values(), "\n");
	}

	@Override
	public Iterator<Episode> iterator() {
		List<Episode> episodes = getEpisodes();
		Collections.sort(episodes);
		return episodes.iterator();
	}
}

