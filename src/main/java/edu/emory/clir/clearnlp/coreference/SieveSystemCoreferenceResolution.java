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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.coreference.sieve.*;
import utils.DisjointSetWithConfidence;
import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.collection.set.DisjointSet;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.EnglishMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 23, 2015
 */
public class SieveSystemCoreferenceResolution extends AbstractCoreferenceResolution{
	private AbstractMentionDetector detector;
	private List<AbstractSieve> sieves;
	
	public SieveSystemCoreferenceResolution() throws IOException{
		
		// Mention Detector declaration
		detector = new EnglishMentionDetector();
		
		// Sieve layer class declarations
		sieves = new ArrayList<>();
		
		/* Sieve 2 : Exact String Match */
		sieves.add(new ExactStringMatch());
		/* Sieve 3 : Relaxed String Match */
		sieves.add(new RelaxedStringMatch());
		/* Sieve 4 : Precise Constructs */
		sieves.add(new PreciseConstructMatch("/Users/alexlutz/ExtraneousStuffForProgramming/TextFiles/DemonymList.txt"));
		/* Sieve 5 : Strict Head Match */
		sieves.add(new StrictHeadMatch());
		/* Sieve 10 : Pronoun Match */
		sieves.add(new PronounMatch());
	}

	@Override
	public Pair<List<Mention>, DisjointSet> getEntities(List<DEPTree> trees) {

		// Mention Detection
		List<Mention> mentions = detector.getMentionList(trees);
		DisjointSetWithConfidence mentionLinks = new DisjointSetWithConfidence(mentions.size());
		
		// Coreference Resolution
		for(AbstractSieve sieve : sieves) sieve.resolute(trees, mentions, mentionLinks);
		
		
		//DEBUG OUTPUT
		int c = 0;
		for(Mention m : mentions)	System.out.println(c++ + ": " + m.toString());
		System.out.println("===================================");
		System.out.println(mentionLinks);
		//////////////
		
		return new Pair<List<Mention>, DisjointSet>(mentions, mentionLinks);
	}
}
