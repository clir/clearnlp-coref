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

import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.relation.chunk.AbstractChucker;
import edu.emory.clir.clearnlp.relation.chunk.EnglishProperNounChunker;
import edu.emory.clir.clearnlp.relation.component.entity.MainEntityIdentificationFeatureExtractor;
import edu.emory.clir.clearnlp.relation.feature.MainEntityComponentLabel;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.relation.structure.Entity;
import edu.emory.clir.clearnlp.util.DSUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 20, 2015
 */
public class StatisticalMainEntityExtractor extends AbstractMainEntityExtractor implements MainEntityComponentLabel{

	private StringModel s_model;
	private MainEntityIdentificationFeatureExtractor e_extractor;
	
	public StatisticalMainEntityExtractor(StringModel model){
		/* Default chunker:
		 * - English ProperNoun chunker 
		 * 		with NEChunker integrated to identify "ORG", "PERSON", "LOC", and "GPE" labels
		 */
		super(new EnglishProperNounChunker(DSUtils.toHashSet("ORG", "PERSON", "LOC", "GPE")));
		s_model = model;
		e_extractor = new MainEntityIdentificationFeatureExtractor();
	}
	
	public StatisticalMainEntityExtractor(AbstractChucker chunker, StringModel model){
		super(chunker);
		s_model = model;
		e_extractor = new MainEntityIdentificationFeatureExtractor();
	}
	
	@Override
	public List<Entity> setEntityConfidence(Document document) {
		List<Entity> l_entities = (document.getEntities() == null)? document.getEntities(d_chunker) : document.getEntities();
		
		StringPrediction prediction;
		for(Entity entity : l_entities){
			prediction = s_model.predictBest(e_extractor.getVector(document, entity));
			entity.setEntityConfidence(prediction.getScore());
		}
		
		Collections.sort(l_entities, Collections.reverseOrder());
		return l_entities;
	}

	@Override
	protected List<Entity> getNonMainEntities(Document document) {
		return getPredictions(FALSE, document);
	}

	@Override
	protected List<Entity> getMainEntities(Document document) {	
		return getPredictions(TRUE, document);
	}

	private List<Entity> getPredictions(String label, Document document){
		List<Entity> l_entities = (document.getEntities() == null)? document.getEntities(d_chunker) : document.getEntities(),
					list = new ArrayList<>();
	
		StringPrediction prediction;
		for(Entity entity : l_entities){
			prediction = s_model.predictBest(e_extractor.getVector(document, entity));
			if(prediction.getLabel().equals(label)) list.add(entity);
		}
		
		return list;
	}
}
