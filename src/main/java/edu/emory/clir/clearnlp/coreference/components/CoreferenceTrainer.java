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

import edu.emory.clir.clearnlp.classification.instance.StringInstance;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.trainer.AbstractOnlineTrainer;
import edu.emory.clir.clearnlp.classification.trainer.AdaGradSVM;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 9, 2015
 */
public class CoreferenceTrainer {
	private StringModel model;
	private AbstractOnlineTrainer trainer;
	private CoreferenceFeatureExtractor extractor;
	
     /* alpha(0.01) : learning rate, rho(0.1) : regularizaiton, bias(0) */
	public CoreferenceTrainer(int labelCutoff, int featureCutoff, boolean average, double alpha, double rho, double bias){
		model = new StringModel(false);
		extractor = new CoreferenceFeatureExtractor();
		trainer = new AdaGradSVM(model, labelCutoff, featureCutoff, average, alpha, rho, bias);
	}
	
	public String getLabel(AbstractMention mention1, DEPTree tree1, AbstractMention mention2, DEPTree tree2){
		return null;
	}
	
	public void addInstance(AbstractMention mention1, DEPTree tree1, AbstractMention mention2, DEPTree tree2){
		addInstance(getLabel(mention1, tree1, mention2, tree2), mention1, tree1, mention2, tree2);
	}
	
	private void addInstance(String label, AbstractMention mention1, DEPTree tree1, AbstractMention mention2, DEPTree tree2){
		StringInstance instance = new StringInstance(label, extractor.getFeatures(mention1, tree1, mention2, tree2));
		model.addInstance(instance);
	}
	
	public StringModel getModel(){
		return model;
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
