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

import java.util.List;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.utils.reader.CoreferenceTSVReader;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSet;
import edu.emory.clir.clearnlp.coreference.utils.structures.Tuple;
import edu.emory.clir.clearnlp.dependency.DEPTree;
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
	
	public static void main(String[] args){
		CoreferenceTSVReader reader = new CoreferenceTSVReader(0, 1, 2, 3, 9, 4, 5, 6, -1, -1, 10);
		CoreferenceTrainer trainer = new CoreferenceTrainer(labelCutoff, featureCutoff, average, alpha, rho, bias);
		
		trainCoNLL(reader, trainer);
	}
	
	public static void trainCoNLL(CoreferenceTSVReader reader, CoreferenceTrainer trainer){
		List<String> l_filePaths = FileUtils.getFileList("/Users/HenryChen/Desktop/conll-12", ".cnlp", true);
//		List<String> l_filePaths = FileUtils.getFileList("/Users/HenryChen/Desktop/conll-13", ".cnlp", true);
		
		int i, j, size;
		DEPTree tree1, tree2;
		AbstractMention mention1, mention2;
		Tuple<List<DEPTree>, List<AbstractMention>, DisjointSet> document;
		for(String filePath : l_filePaths){
			System.out.println(filePath);
			reader.open(IOUtils.createFileInputStream(filePath));
			document = reader.getCoNLLDocument();
			
			for(List<Integer> cluster : document.t3.getClusterLists(false, true)){
				size = cluster.size();
				for(i = 0; i < size-1; i++){
					mention1 = document.t2.get(i);
					tree1 = document.t1.get(mention1.getTreeId());
					
					for(j = i + 1; j < size; j++){
						mention2 = document.t2.get(j);
						tree2 = document.t1.get(mention2.getTreeId());
						
						trainer.addInstance(mention1, tree1, mention2, tree2);
					}
				}
			}
			
			reader.close();
		}
	}
}
