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
import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.classification.trainer.AbstractOnlineTrainer;
import edu.emory.clir.clearnlp.classification.trainer.AdaGradSVM;
import edu.emory.clir.clearnlp.collection.triple.Triple;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.type.CoreferenceLabel;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 23, 2015
 */
public class CoreferenceComponent implements CoreferenceLabel{
	private CFlag c_flag;
	
	private StringModel model;
	private AbstractOnlineTrainer trainer;
	private CoreferenceFeatureExtractor extractor;
	
	/* Trainer information */
	private boolean average;
	private int labelCutoff, featureCutoff;
	private double alpha, rho, bias;
	
    /* alpha(0.01) : learning rate, rho(0.1) : regularizaiton, bias(0) */
	public CoreferenceComponent(int labelCutoff, int featureCutoff, boolean average, double alpha, double rho, double bias){
		setFlag(CFlag.TRAIN);
		
		model = new StringModel(false);
		extractor = new CoreferenceFeatureExtractor();
		
		this.average = average;
		this.alpha = alpha;	this.rho = rho;	this.bias = bias;
		this.labelCutoff = labelCutoff;	this.featureCutoff = featureCutoff;
	}
	
	public StringModel getModel(){
		return model;
	}
	
	public AbstractOnlineTrainer getTrainer(){
		return trainer;
	}
	
	public CoreferenceFeatureExtractor getExtractor(){
		return extractor;
	}
	
	public void setFlag(CFlag flag){
		c_flag = flag;
	}
	
	private String getLabel(CoreferantSet links, int prevId, int currId){
		if(links.isSameSet(prevId, currId))		return LINK;
		if(prevId < links.findClosest(currId))	return SHIFT;
		return UNLINK;
	}
	
	private CoreferantSet process(List<DEPTree> trees, List<AbstractMention> mentions, CoreferantSet links){
		CoreferantSet bootstrapLinks = new CoreferantSet(mentions.size());
		
		String label = null;
		int i, j, size = mentions.size();
		DEPTree prev_tree, curr_tree;
		AbstractMention prev_mention, curr_mention;
		
		for(i = size - 1; i > 0; i--){
			curr_mention = mentions.get(i);
			curr_tree = (curr_mention.hasTree())? curr_mention.getTree() : null;
			for(j = i - 1; j >= 0; j--){
				prev_mention = mentions.get(j);
				prev_tree = (prev_mention.hasTree())? prev_mention.getTree() : null;		
				
				// create feature vector 
				if(c_flag == CFlag.TRAIN){
					label = getLabel(links, j, i);
					addInstance(label, trees, bootstrapLinks, prev_mention, prev_tree, curr_mention, curr_tree);
				}
				if(c_flag == CFlag.DECODE){
					label = predictBest(trees, bootstrapLinks, prev_mention, prev_tree, curr_mention, curr_tree).getLabel();
				}
				
				if(label.equals(LINK)) bootstrapLinks.union(j, i);
				if(label.equals(SHIFT))	break;
			}
		}
		
		return bootstrapLinks;
	}
	
	// Training
	public void initTrainer(){
		if(trainer == null)	trainer = new AdaGradSVM(model, labelCutoff, featureCutoff, average, alpha, rho, bias);
	}
	
	public void trainModel(int iteration){
		for(int i = 0; i < iteration; i++){
			System.out.println("Iteration #" + i);
			trainer.train();
		}
	}
	
	public void train(List<DEPTree> trees, List<AbstractMention> mentions, CoreferantSet links){
		if(c_flag == CFlag.TRAIN)	process(trees, mentions, links);
	}
	
	public void train(Triple<List<DEPTree>, List<AbstractMention>, CoreferantSet> document){
		train(document.o1, document.o2, document.o3);
	}
	
	private void addInstance(String label, List<DEPTree> trees, CoreferantSet bootstrapLinks, AbstractMention mention1, DEPTree tree1, AbstractMention mention2, DEPTree tree2){
		StringInstance instance = new StringInstance(label, extractor.getFeatures(trees, bootstrapLinks, mention1, tree1, mention2, tree2));
		model.addInstance(instance);
	}
	
	// Decoding
	public CoreferantSet decode(List<DEPTree> trees, List<AbstractMention> mentions){
		return (c_flag == CFlag.DECODE)? process(trees, mentions, null) : null;
	}
	
	public CoreferantSet decode(Triple<List<DEPTree>, List<AbstractMention>, CoreferantSet> document){
		return decode(document.o1, document.o2);
	}
	public StringPrediction[] predictAll(List<DEPTree> trees, CoreferantSet bootstrapLinks, AbstractMention mention1, DEPTree tree1, AbstractMention mention2, DEPTree tree2){
		return model.predictAll(extractor.getFeatures(trees, bootstrapLinks, mention1, tree1, mention2, tree2));
	}
	
	public StringPrediction predictBest(List<DEPTree> trees, CoreferantSet bootstrapLinks, AbstractMention mention1, DEPTree tree1, AbstractMention mention2, DEPTree tree2){
		return model.predictBest(extractor.getFeatures(trees, bootstrapLinks, mention1, tree1, mention2, tree2));
	}
	
	// Model handling
	public void exportModel(ObjectOutputStream out){
		try {
			model.save(out);
	        out.close();
		} catch (Exception e) { e.printStackTrace(); }
	}
}
