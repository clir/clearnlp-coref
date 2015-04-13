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
package edu.emory.clir.clearnlp.coreference.sieve;

import java.io.IOException;
import java.util.List;

import utils.DisjointSetWithConfidence;
import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.collection.set.DisjointSet;
import edu.emory.clir.clearnlp.coreference.AbstractCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.EnglishMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 23, 2015
 */
public class SieveSystem extends AbstractCoreferenceResolution{
	private AbstractMentionDetector detector;
	private List<AbstractSieve> sieves;
	
	public SieveSystem() throws IOException{
		// Mention Detector declaration
		detector = new EnglishMentionDetector();
		
		// Sieve layer class declarations
		sieves.add(new ExactStringMatch());
		sieves.add(new RelaxedStringMatch());
	}

	@Override
	public Pair<List<Mention>, DisjointSet> getEntities(List<DEPTree> trees) {
		List<Mention> mentions = detector.getMentionList(trees);
		DisjointSetWithConfidence mentionLinks = new DisjointSetWithConfidence(mentions.size());
		
		for(AbstractSieve sieve : sieves) 
			mentionLinks = sieve.resolute(trees, mentions, mentionLinks);
		return new Pair<List<Mention>, DisjointSet>(mentions, mentionLinks);
	}
}
