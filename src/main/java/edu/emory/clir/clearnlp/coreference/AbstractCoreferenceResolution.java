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
package edu.emory.clir.clearnlp.coreference;

import java.util.List;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.coreference.config.AbstractCorefConfiguration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.mention.detector.AbstractMentionDetector;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSet;
import edu.emory.clir.clearnlp.dependency.DEPTree;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AbstractCoreferenceResolution {
	
	protected AbstractCorefConfiguration config;
	protected AbstractMentionDetector m_detector;

	public AbstractCoreferenceResolution(AbstractCorefConfiguration config){
		this.config = config;
	}
	
	// Mention Detection
	public List<AbstractMention> getMentions(DEPTree tree){
		return m_detector.getMentionList(tree);
	}
	
	public List<AbstractMention> getMentions(List<DEPTree> trees){
		return m_detector.getMentionList(trees);
	}
	
	// Coreference Resolution
	public abstract Pair<List<AbstractMention>, DisjointSet> getEntities(List<DEPTree> trees);
	protected abstract boolean isSameEntity(List<AbstractMention> mentions, DisjointSet mentionLinks);
}
