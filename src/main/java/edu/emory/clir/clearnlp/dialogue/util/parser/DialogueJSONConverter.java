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

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import edu.emory.clir.clearnlp.NLPDecoder;
import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.dialogue.structure.Episode;
import edu.emory.clir.clearnlp.dialogue.structure.Scene;
import edu.emory.clir.clearnlp.dialogue.structure.Season;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 24, 2015
 */
public class DialogueJSONConverter {
	private final static String ROOT_DIR = "/Users/HenryChen/Desktop/FriendScripts/season1/scene";
	private final static String OUTPUT_PATH = "/Users/HenryChen/Desktop/FriendScripts/FriendScripts.json";
	private final static NLPDecoder decoder = new NLPDecoder(TLanguage.ENGLISH);
	
	public static void main(String[] args){
		Gson gson = new Gson();
		Map<Integer, Season> m_seasons = new TreeMap<>();
		List<String> l_filePaths = FileUtils.getFileList(ROOT_DIR, ".raw", false);
		
		BufferedReader reader;
		int speakerId;
		List<String> trees;
		Map<String, Integer> m_speakerIds;
		String line, speaker, statement, statement_stripped;
		Pair<String, String> speakerUtterancePair;
		Season season; Episode episode; Scene scene;
		
		for(String filePath : l_filePaths){
			SceneTuple meta = getSceneMeta(filePath);
			
			season = m_seasons.computeIfAbsent(meta.seasonId, s -> new Season(meta.seasonId));
			episode = season.addEpisode(meta.episodeId);
			scene = episode.addScene(meta.sceneId);
			
			try {
				m_speakerIds = new HashMap<>();
				reader = IOUtils.createBufferedReader(filePath);
				while( (line = reader.readLine()) != null ){
					speakerUtterancePair = getSpeakerUtterancePair(line);
					speaker = speakerUtterancePair.o1;
					statement = speakerUtterancePair.o2;

					if(speaker == null)							speakerId = -1;
					else if(m_speakerIds.containsKey(speaker))	speakerId = m_speakerIds.get(speaker);
					else{
						speakerId = scene.addSpeaker(speaker);
						m_speakerIds.put(speaker, speakerId);
					}
					
					if(speakerId < 0)
						scene.addUtterance(speakerId, statement);
					else{
						statement_stripped = getStrippedStatement(statement);
						trees = decoder.toDEPTrees(statement_stripped).stream().map(tree -> tree.toString()).collect(Collectors.toList());
						scene.addUtterance(speakerId, statement, statement_stripped, trees);
					}	
				}
				reader.close();
			} catch (Exception e) { e.printStackTrace(); }
		}	
		
		PrintWriter writer = new PrintWriter(IOUtils.createFileOutputStream(OUTPUT_PATH));
		writer.write(gson.toJson(m_seasons));
		writer.close();
	}
	
	public static SceneTuple getSceneMeta(String path){
		if(path.lastIndexOf('/') < 0)	return null;
		
		int index, season, episode, scene;
		String fileName = path.substring(path.lastIndexOf('/')+1);
		
		// Get Season
		index = fileName.indexOf('e');
		season = Integer.parseInt(fileName.substring(1, index));
		fileName = fileName.substring(index+1);
		
		// Get Episode
		index = fileName.indexOf('.');
		episode = Integer.parseInt(fileName.substring(0, index));
		fileName = fileName.substring(index+1);
		
		// Get Scene
		index = fileName.indexOf('.');
		scene = Integer.parseInt(fileName.substring(5, index));
		
		return new SceneTuple(season, episode, scene);
	}
	
	public static Pair<String, String> getSpeakerUtterancePair(String line){
		int pos = line.indexOf(':');	
		if(pos < 0)	return new Pair<>(null, line);
		else		return new Pair<>(line.substring(0, pos), line.substring(pos+1));
	}
	
	public static String getStrippedStatement(String statement){
		return statement
				.replaceAll("\\(.+?\\)", "")
				.replaceAll("- ", " ")
				.trim();
	}
}
