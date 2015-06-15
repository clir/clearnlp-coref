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
package edu.emory.clir.clearnlp.coreference.components;

import java.io.ObjectOutputStream;
import java.util.List;

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.trainer.AbstractOnlineTrainer;
import edu.emory.clir.clearnlp.classification.trainer.AdaGradSVM;
import edu.emory.clir.clearnlp.collection.triple.Triple;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.type.CoreferenceLabel;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 9, 2015
 */
public class CoreferenceTrainer implements CoreferenceLabel{
	private StringModel model;
	private AbstractOnlineTrainer trainer;
	private CoreferenceFeatureExtractor extractor;
	
	/* Trainer information */
	private boolean average;
	private int labelCutoff, featureCutoff;
	private double alpha, rho, bias;
	
     /* alpha(0.01) : learning rate, rho(0.1) : regularizaiton, bias(0) */
	public CoreferenceTrainer(int labelCutoff, int featureCutoff, boolean average, double alpha, double rho, double bias){
		model = new StringModel(false);
		extractor = new CoreferenceFeatureExtractor();
		
		this.average = average;
		this.alpha = alpha;	this.rho = rho;	this.bias = bias;
		this.labelCutoff = labelCutoff;	this.featureCutoff = featureCutoff;
	}
	
	public void addDocument(Triple<List<DEPTree>, List<AbstractMention>, CoreferantSet> document){
		CoreferantSet links = document.o3;
		List<DEPTree> trees = document.o1;
		List<AbstractMention> mentions = document.o2;
		
		String label;
		int i, j, size = mentions.size();
		DEPTree prev_tree, curr_tree;
		AbstractMention prev_mention, curr_mention;
		
		for(i = size - 1; i > 0; i--){
			curr_mention = mentions.get(i);
			curr_tree = (curr_mention.hasTree())? curr_mention.getTree() : null;
			for(j = i - 1; j >= 0; j--){
				prev_mention = mentions.get(j);
				prev_tree = (prev_mention.hasTree())? prev_mention.getTree() : null;		
				
				label = getLabel(links, j, i);
				addInstance(label, trees, prev_mention, prev_tree, curr_mention, curr_tree);
				if(label.equals(SHIFT))	break;
			}
		}
	}
	
	private String getLabel(CoreferantSet links, int prevId, int currId){
		if(links.isSameSet(prevId, currId))		return LINK;
		if(prevId < links.findClosest(currId))	return SHIFT;
		return UNLINK;
	}
	
	private void addInstance(String label, List<DEPTree> trees, AbstractMention mention1, DEPTree tree1, AbstractMention mention2, DEPTree tree2){
		StringInstance instance = new StringInstance(label, extractor.getFeatures(trees, mention1, tree1, mention2, tree2));
		model.addInstance(instance);
	}
	
	public StringModel getModel(){
		return model;
	}
	
	public void initTrainer(){
		if(trainer == null)	trainer = new AdaGradSVM(model, labelCutoff, featureCutoff, average, alpha, rho, bias);
	}
	
	public void trainModel(){
		trainer.train();
	}
	
	public void exportModel(ObjectOutputStream out){
		try {
			model.save(out);
	        out.close();
		} catch (Exception e) { e.printStackTrace(); }
	}
}
