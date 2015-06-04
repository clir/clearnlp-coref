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
import edu.emory.clir.clearnlp.coreference.config.CorefConfiguration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.mention.detector.EnglishMentionDetector;
import edu.emory.clir.clearnlp.coreference.sieve.AbstractSieve;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSet;
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
public class SieveSystemCoreferenceResolution extends AbstractCoreferenceResolution {
	private List<AbstractSieve> sieves;
	
	public SieveSystemCoreferenceResolution(CorefConfiguration config){
		
		// Mention Detector declaration
		super(TLanguage.ENGLISH);
		if(config.getMentionConfig() == null)	
			throw new IllegalArgumentException("Mention detector configuration not sepecified.");
		m_detector = new EnglishMentionDetector(config.getMentionConfig());
		
		// Sieve layer class declarations
		if(config.getSieves().isEmpty()) 
			System.out.println("WARNING: No coreference sieve initialized.");
		sieves = config.getSieves();
	}

	@Override
	public Pair<List<AbstractMention>, DisjointSet> getEntities(List<DEPTree> trees) {

		// Mention Detection
		List<AbstractMention> mentions = m_detector.getMentionList(trees);
		DisjointSet mentionLinks = new DisjointSet(mentions.size(), true);
		
		// Coreference Resolution
		for(AbstractSieve sieve : sieves) sieve.resolute(trees, mentions, mentionLinks);
		
		return new Pair<List<AbstractMention>, DisjointSet>(mentions, mentionLinks);
	}

	@Override
	protected boolean isSameEntity(List<AbstractMention> mentions, DisjointSet mentionLinks) {
		// TODO Auto-generated method stub
		return false;
	}
}
