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
package edu.emory.clir.clearnlp.coreference.utils.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.emory.clir.clearnlp.collection.pair.IntIntPair;
import edu.emory.clir.clearnlp.coreference.config.MentionConfiguration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.mention.detector.AbstractMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.detector.EnglishMentionDetector;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSet;
import edu.emory.clir.clearnlp.coreference.utils.structures.Tuple;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 9, 2015
 */
public class CoreferenceTSVReader extends TSVReader{
	private int i_corefLink;
	private AbstractMentionDetector m_detector;
	
	public CoreferenceTSVReader(int iID, int iForm, int iLemma, int iPOSTag, int iNERTag, int iFeats, int iHeadID, int iDeprel, int iXHeads, int iSHeads, int iCorefLink) {
		super(iID, iForm, iLemma, iPOSTag, iNERTag, iFeats, iHeadID, iDeprel, iXHeads, iSHeads);
		i_corefLink = iCorefLink;
		m_detector = new EnglishMentionDetector(new MentionConfiguration(true, true, true));
	}

	public Tuple<List<DEPTree>, List<AbstractMention>, List<IntIntPair>> getCoNLLDocument(){
		List<DEPTree> trees = new ArrayList<>();
		List<IntIntPair> links = new ArrayList<>();
		List<AbstractMention> mentions = new ArrayList<>();
		Map<Integer, Set<Integer>> m_coreferants = new HashMap<>();
		
		int i, m_index, t_index, size;
		String[] line;
		List<String[]> lines;
		DEPTree tree; DEPNode node;
		AbstractMention mention;
		try {
			while( (lines = readLines()) != null){
				t_index = trees.size();
				tree = getDEPTree(lines);
				trees.add(tree);
				
				size = tree.size();
				for(i = 0; i < size; i++){
					node = tree.get(i);
					line = lines.get(i);
					
					if(!line[i_corefLink].equals(StringConst.HYPHEN)){
						m_index = mentions.size();
						mention = m_detector.getMention(t_index, tree, node);
						mentions.add(mention);
						// Handling links here
					}
				}
			}
			
		} catch (Exception e) { e.printStackTrace(); }
		
		return new Tuple<>(trees, mentions, links);
	}
}
