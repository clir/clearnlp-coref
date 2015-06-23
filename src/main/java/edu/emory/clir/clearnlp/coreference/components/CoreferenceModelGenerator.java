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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import edu.emory.clir.clearnlp.coreference.utils.reader.CoreferenceTSVReader;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 9, 2015
 */
public class CoreferenceModelGenerator {
	public static final int labelCutoff = 0;
	public static final int featureCutoff = 0;
	public static final boolean average = true;
	public static final double alpha = 0.01;
	public static final double rho = 0.1;
	public static final double bias = 0;
	
	public static void main(String[] args) throws IOException{
		CoreferenceTSVReader reader = new CoreferenceTSVReader(true, false, 0, 1, 2, 3, 9, 4, 5, 6, -1, -1, 10);
		CoreferenceComponent component = new CoreferenceComponent(labelCutoff, featureCutoff, average, alpha, rho, bias);
		ObjectOutputStream out = IOUtils.createObjectXZBufferedOutputStream("/Users/HenryChen/Desktop/coref_model.xz");
		
		trainCoNLL(10, reader, component, out);
	}
	
	public static void trainCoNLL(int iter, CoreferenceTSVReader reader, CoreferenceComponent component, ObjectOutputStream out){
		List<String> l_filePaths = FileUtils.getFileList("/Users/HenryChen/Desktop/conll-13-dummy/train", ".cnlp", true);
//		List<String> l_filePaths = FileUtils.getFileList("/Users/HenryChen/Desktop/conll-13", ".cnlp", true);
		
		for(String filePath : l_filePaths){
			System.out.println(filePath);
			reader.open(IOUtils.createFileInputStream(filePath));
			component.train(reader.getGoldCoNLLDocument());
			reader.close();
		}
		
		component.initTrainer();
		component.trainModel(iter);
		component.exportModel(out);
	}
}
