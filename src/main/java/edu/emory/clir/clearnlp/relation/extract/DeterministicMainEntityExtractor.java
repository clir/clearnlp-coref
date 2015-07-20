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
package edu.emory.clir.clearnlp.relation.extract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.emory.clir.clearnlp.relation.chunk.AbstractChucker;
import edu.emory.clir.clearnlp.relation.chunk.EnglishProperNounChunker;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.relation.structure.Entity;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.MathUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 14, 2015
 */
public class DeterministicMainEntityExtractor extends AbstractMainEntityExtractor{
	/*
	 * Deterministic Main Entity Extractor
	 * 		gives ~80% precision 
	 * 		out of ~10% documents in a corpus
	 */

	private final double d_cutoffThreshold = 0.49d, d_lowCutoffAlpha = 1.9d;
	private final double d_frequencyCount = 0.785d, d_entityConfidence = 0.125d, d_firstAppearance = 0.315d;

	public DeterministicMainEntityExtractor(){
		/* Default chunker:
		 * - English ProperNoun chunker 
		 * 		with NEChunker integrated to identify "ORG", "PERSON", "LOC", and "GPE" labels
		 */
		super(new EnglishProperNounChunker(DSUtils.toHashSet("ORG", "PERSON", "LOC", "GPE")));
	}
	
	public DeterministicMainEntityExtractor(AbstractChucker chunker){
		super(chunker);
	}
	
	@Override
	public List<Entity> setEntityConfidence(Document document) {
		List<Entity> entities = new ArrayList<>(document.getEntities(d_chunker));
		
		int totalSentenceCount = document.getTreeCount(),
				totalAliasCount = entities.stream().mapToInt(Entity::getCount).sum();
		for(Entity entity : entities)
			entity.setEntityConfidence(computeScore(entity, totalAliasCount, totalSentenceCount));
		
		Collections.sort(entities, Collections.reverseOrder());
		return entities;
	}
	
	@Override
	protected List<Entity> getMainEntities(Document document){
		List<Entity> entities = setEntityConfidence(document);
		
		for(int i = 0; i < entities.size(); i++)
			if(entities.get(i).getEntityConfidence() < d_cutoffThreshold)
				return entities.subList(0, i);
		return entities;
	}
	
	@Override
	public List<Entity> getNonMainEntities(Document document) {
		List<Entity> l_entities = (document.getEntities() == null)? document.getEntities(d_chunker) : document.getEntities(),
				l_mainEntities = (document.getMainEntities() == null)? getMainEntities(document) : document.getMainEntities();
		
		// Generate confidence array
		int i, mainEntityCount = l_mainEntities.size(), entityCount = l_entities.size();
		double[] mainEntityConfidences = new double[mainEntityCount], entityConfidences = new double[entityCount];
		for(i = 0; i < entityCount; i++) entityConfidences[i] = l_entities.get(i).getEntityConfidence();
		for(i = 0; i < mainEntityCount; i++) mainEntityConfidences[i] = l_mainEntities.get(i).getEntityConfidence();
		
		// Initialize variables
		int topK = l_mainEntities.size(), lowK = topK;
		double 	mainEntityConfidenceMean = MathUtils.average(mainEntityConfidences),
				entityConfidenceMean = MathUtils.average(entityConfidences),
				meanDifference = mainEntityConfidenceMean - entityConfidenceMean,
				entityConfidenceSD = MathUtils.stdev(entityConfidences),
				mainEntityPercentage = (double) l_mainEntities.size() / l_entities.size();
		
		double lowCutoff = d_lowCutoffAlpha * (mainEntityConfidenceMean / entityConfidenceMean)
											* (meanDifference / entityConfidenceSD - 1)
											* (mainEntityPercentage / (1 - mainEntityPercentage) * mainEntityConfidenceMean);
		
		
		for(i = entityCount - 1; i >= topK; i--){
			if(l_entities.get(i).getEntityConfidence() > lowCutoff){
				lowK = i + 1; break;
			}
		}
		
		return l_entities.subList(lowK, entityCount);
	}
	
	private double computeScore(Entity entity, int aliasCount, int SentenceCount){
		double score = 0d;
		score += d_frequencyCount * logRescale(((double)entity.getCount() / aliasCount));
		score += d_entityConfidence * logRescale(entity.getAliasConfidence());
		score += d_firstAppearance * logRescale(( (1 - (double)entity.getFirstAlias().getSentenceId() / SentenceCount)));
		return score;
	}
	
	private double logRescale(double score){
		return Math.log(score + 1);
	}
}
