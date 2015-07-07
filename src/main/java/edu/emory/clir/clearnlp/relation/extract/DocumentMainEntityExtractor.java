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

import edu.emory.clir.clearnlp.collection.map.IntDoubleHashMap;
import edu.emory.clir.clearnlp.relation.feature.MainEntityFeatureIndex;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.relation.structure.Entity;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 5, 2015
 */
public class DocumentMainEntityExtractor implements MainEntityFeatureIndex{
	private double d_cutoffThreshold;
	private double d_gapThreshold;
	private IntDoubleHashMap m_weights;
	
	public DocumentMainEntityExtractor(double cutoffThreshold, double gapThreshold, IntDoubleHashMap weights){
		d_cutoffThreshold = cutoffThreshold;
		d_gapThreshold = gapThreshold;
		m_weights = weights;
	}
	
	public List<Entity> getMainEntities(Document document){
		List<Entity> entities = new ArrayList<>(document.getEntities());
		int totalSentenceCount = document.getTreeCount(),
			totalAliasCount = entities.stream().mapToInt(Entity::getCount).sum();
		
		for(Entity entity : entities){
			entity.setEntityConfidence(computeScore(entity, totalAliasCount, totalSentenceCount));
		}
		
		double score, gap;
		Collections.sort(entities, Collections.reverseOrder());
		for(int i = 0; i < entities.size()-1; i++){
			score = entities.get(i).getEntityConfidence();
			if(score < d_cutoffThreshold){
				entities = entities.subList(0, i);
				break;
			}
				
			gap = score - entities.get(i+1).getEntityConfidence();
			if(gap > d_gapThreshold){
				entities = entities.subList(0, i+1);
				break;
			}
		}

		return entities;
	}
	
	private double computeScore(Entity entity, int aliasCount, int SentenceCount){
		double score = 0d;
		
		score += m_weights.get(FREQUENCY_COUNT) * ((double)entity.getCount() / aliasCount);
		score += m_weights.get(EntityConfidence) * entity.getAliasConfidence();
		score += m_weights.get(FirstApearSentenceId) *  (1 - (double)entity.getFirstAlias().getSentenceId() / SentenceCount);
		
		return score;
	}
}
