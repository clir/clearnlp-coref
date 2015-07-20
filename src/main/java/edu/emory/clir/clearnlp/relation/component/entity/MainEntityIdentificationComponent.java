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
package edu.emory.clir.clearnlp.relation.component.entity;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.prediction.StringPrediction;
import edu.emory.clir.clearnlp.classification.trainer.AbstractOnlineTrainer;
import edu.emory.clir.clearnlp.classification.trainer.AdaGradSVM;
import edu.emory.clir.clearnlp.component.utils.CFlag;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.relation.structure.Entity;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 15, 2015
 */
public class MainEntityIdentificationComponent {
	private static String TRUE = "true", FALSE = "false";
	
	private CFlag c_flag;
	
	private StringModel model;
	private AbstractOnlineTrainer trainer;
	private MainEntityIdentificationFeatureExtractor extractor;
	
	/* Trainer information */
	private boolean average;
	private int labelCutoff, featureCutoff;
	private double alpha, rho, bias;
	
	/* alpha(0.01) : learning rate, rho(0.1) : regularizaiton, bias(0) */
	public MainEntityIdentificationComponent(int labelCutoff, int featureCutoff, boolean average, double alpha, double rho, double bias){
		setFlag(CFlag.TRAIN);
		
		model = new StringModel(true);
		extractor = new MainEntityIdentificationFeatureExtractor();
		
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
	
	public MainEntityIdentificationFeatureExtractor getExtractor(){
		return extractor;
	}
	
	public void setFlag(CFlag flag){
		c_flag = flag;
	}
	
	// Process
	private List<Entity> process(Document document){
		switch(c_flag){
			case TRAIN:
				List<Entity> l_mainEntities = document.getMainEntities(),
							l_nonMainEntities = document.getNonMainEntities();
				
				for(Entity mainEntity : l_mainEntities)	
					model.addInstance(new StringInstance(TRUE, extractor.getVector(document, mainEntity)));
				for(Entity nonMainEnitty : l_nonMainEntities) 
					model.addInstance(new StringInstance(FALSE, extractor.getVector(document, nonMainEnitty)));
				return l_mainEntities;
				
			case DECODE:
				StringPrediction prediction;
				List<Entity> entities = new ArrayList<>();
				
				for(Entity entity : document.getEntities()){
					prediction = model.predictBest(extractor.getVector(document, entity));
					if(prediction.getLabel().equals(TRUE))	entities.add(entity);
				}
				return entities;
				
			default:
				break;
		}
		return null;
	}
	
	// Training
	public void initTrainer(){
		if(trainer == null)	trainer = new AdaGradSVM(model, labelCutoff, featureCutoff, average, alpha, rho, bias);
	}
		
	public void initTrainer(int iter){
		if(trainer == null)	trainer = new AdaGradSVM(model, labelCutoff, featureCutoff, average, alpha, rho, bias);
		while(iter-- > 0) trainModel();
	}
		
	public void trainModel(){
		trainer.train();
	}
		
	public void trainModel(int iteration){
		for(int i = 0; i < iteration; i++){
			System.out.println("Iteration #" + i);
			trainer.train();
		}
	}
	
	public void train(Document document){
		if(c_flag == CFlag.TRAIN) process(document);
		else throw new IllegalStateException("Component is not set to train.");
	}
	
	// Decoding
	public List<Entity> decode(Document document){
		if(c_flag == CFlag.DECODE) return process(document);
		else throw new IllegalStateException("Component is not set to decode.");
	}
		
	// Model handling
	public void exportModel(ObjectOutputStream out){
		try {
			model.save(out);
		    out.close();
		} catch (Exception e) { e.printStackTrace(); }
	}
}
