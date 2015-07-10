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
package edu.emory.clir.clearnlp.relation.utils.evaluation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.emory.clir.clearnlp.collection.triple.Triple;
import edu.emory.clir.clearnlp.coreference.utils.evaluator.AbstractEvaluator;
import edu.emory.clir.clearnlp.relation.chunk.AbstractChucker;
import edu.emory.clir.clearnlp.relation.structure.Chunk;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.relation.structure.Entity;
import edu.emory.clir.clearnlp.relation.structure.EntityAlias;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 8, 2015
 */
public abstract class AbstractRelationExtrationEvaluator extends AbstractEvaluator<List<Entity>>{
	
	protected AbstractChucker key_chunker;
	
	public AbstractRelationExtrationEvaluator(AbstractChucker chunker){
		key_chunker = chunker;
	}
	
	public double evaluatePrecision(Document document, List<Entity> prediction){
		return evaluatePrecision(generateKeysFromTitle(document), prediction);
	}
	
	public double evaluateRecall(Document document, List<Entity> prediction){
		return evaluateRecall(generateKeysFromTitle(document), prediction);
	}
	
	public Triple<Double, Double, Double> getEvaluationTriple(Document document, List<Entity> prediction){
		DocCount++;
		double 	precision = evaluatePrecision(document, prediction), 
				recall = evaluateRecall(document, prediction), 
				f1 = evaluateF1Score(precision, recall);
		return new Triple<>(precision, recall, f1);
	}
	
	public List<Entity> generateKeysFromTitle(Document document){
		if(key_chunker == null) throw new IllegalArgumentException("Chunker is not initialized in the evaluator.");
		if(document.getTitleTree() == null) throw new IllegalArgumentException("Document " + document.getTitle() + " does not have a title DEPTree initialized.");
		
		List<Entity> keys = new ArrayList<>();
		List<Chunk> chunks = key_chunker.getChunk(document.getTitleTree());
		
		String word;
		Set<String> entity_wordForms = new HashSet<>();
		for(Entity entity : document.getEntities()){
			for(EntityAlias alias : entity.getAliasList()){
				word = alias.getStippedWordForm(true);
				if(!word.isEmpty()) entity_wordForms.add(word);
			}
		}
		
		boolean hasAlias;
		for(Chunk chunk : chunks){			
			if(entity_wordForms.contains(chunk.getStrippedWordForm(true))){
				hasAlias = false;
				for(Entity entity : keys)
					if(entity.addAlias(-1, chunk)){
						hasAlias = true; break;
					}
				if(!hasAlias) keys.add(chunk.toEnity());
			}
		}
		
		return keys;
	}
}
