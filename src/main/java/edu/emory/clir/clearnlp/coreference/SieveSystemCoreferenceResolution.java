/**
 * Copyright 2014, Emory University
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
package edu.emory.clir.clearnlp.coreference;

import java.util.List;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.collection.set.DisjointSet;
import edu.emory.clir.clearnlp.coreference.config.CorefCongiuration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.mention.detector.EnglishMentionDetector;
import edu.emory.clir.clearnlp.coreference.sieve.AbstractSieve;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSetWithConfidence;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 23, 2015
 * need to add Speaker Identification
 * might want to look more into versions of Strict Head Match
 * might want to add Relaxed Head Match?
 */
public class SieveSystemCoreferenceResolution extends AbstractCoreferenceResolution 
{
	private List<AbstractSieve> sieves;
	
	public SieveSystemCoreferenceResolution(CorefCongiuration config){
		
		// Mention Detector declaration
		super(TLanguage.ENGLISH);
		m_detector = new EnglishMentionDetector();
		
		// Sieve layer class declarations
		sieves = config.getSieves();
	}

	@Override
	public Pair<List<AbstractMention>, DisjointSet> getEntities(List<DEPTree> trees) {

		// Mention Detection
		List<AbstractMention> mentions = m_detector.getMentionList(trees);
		DisjointSetWithConfidence mentionLinks = new DisjointSetWithConfidence(mentions.size());
		
		// Coreference Resolution
		for(AbstractSieve sieve : sieves) sieve.resolute(trees, mentions, mentionLinks);
		
		return new Pair<List<AbstractMention>, DisjointSet>(mentions, mentionLinks);
	}
}
