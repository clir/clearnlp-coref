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
package edu.emory.clir.clearnlp.coreference.eval.friends;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.coreference.AbstractCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.SieveSystemCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.config.SieveSystemCongiuration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.path.PathVisualization;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
import edu.emory.clir.clearnlp.coreference.visualization.BratCorefVisualizer;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.dialogue.structure.Episode;
import edu.emory.clir.clearnlp.dialogue.structure.Scene;
import edu.emory.clir.clearnlp.dialogue.structure.Season;
import edu.emory.clir.clearnlp.dialogue.util.parser.JSONDialogueReader;
import edu.emory.clir.clearnlp.dialogue.util.parser.SceneTuple;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 5, 2015
 */
public class Season1Test {
	@Test
	public void testSeason1() throws IOException{
		JSONDialogueReader j_reader = new JSONDialogueReader();
		Map<Integer, Season> m_seasons = j_reader.read("/Users/HenryChen/Box Sync/Coref/FriendScripts/FriendScripts.season1.json");
		Season season1 = m_seasons.get(1);
		
		SceneTuple scene_meta;
		TSVReader t_reader = new TSVReader(0, 1, 2, 3, 9, 4, 5, 6, -1, -1);
		BratCorefVisualizer visualizer = new BratCorefVisualizer(PathVisualization.FRIENDS);
		
		/* Coref Configuration */
		SieveSystemCongiuration config = new SieveSystemCongiuration(TLanguage.ENGLISH);
		config.loadDefaultMentionDectors();
		config.loadDefaultSieves(true, true, true, true, true, false, false, false);
		/* ************* */
		
		List<DEPTree> trees; Pair<List<AbstractMention>, CoreferantSet> resolution;
		AbstractCoreferenceResolution coref = new SieveSystemCoreferenceResolution(config);
		
		for(Episode episode : season1){
			for(Scene scene : episode){
				scene_meta = new SceneTuple(season1.getId(), episode.getId(), scene.getId());
				trees = scene.getAllDEPTrees(t_reader);
				
				resolution = coref.getEntities(trees);
				visualizer.export(scene_meta.getFileName(), trees, resolution.o1, resolution.o2);
			}
		}
	}
}
