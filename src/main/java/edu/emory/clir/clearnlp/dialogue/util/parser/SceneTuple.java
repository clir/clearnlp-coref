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
package edu.emory.clir.clearnlp.dialogue.util.parser;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 24, 2015
 */
public class SceneTuple {
	public int seasonId;
	public int episodeId;
	public int sceneId;
	
	public SceneTuple(int season_id, int episode_id, int scene_id){
		seasonId = season_id;
		episodeId = episode_id;
		sceneId = scene_id;
	}
	
	@Override
	public String toString(){
		return "Season: " + seasonId + "\tEpsiode: " + episodeId + "\tScene: " + sceneId;
	}
}
