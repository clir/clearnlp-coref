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
package edu.emory.clir.clearnlp.coreference.coref.corpus;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.triple.Triple;
import edu.emory.clir.clearnlp.coreference.config.MentionConfiguration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.utils.reader.CoreferenceTSVReader;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
import edu.emory.clir.clearnlp.coreference.utils.util.CoreferenceDSUtils;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 22, 2015
 */
public class CoNLLCorpusFilter {
	public static int DOC_COUNT = 0, CORRECT_COUNT = 0, MENTION_COUNT = 0, CLUSTER_COUNT = 0;
	public static int OVERLAP_CLUSTER_COUNT = 0, OVERLAP_MENTION_COUNT = 0;
	
	@Test
	public void	 testCorrectCorpus(){
		MentionConfiguration m_config = new MentionConfiguration(true, true, true);
		CoreferenceTSVReader reader = new CoreferenceTSVReader(m_config, false, false, 0, 1, 2, 3, 9, 4, 5, 6, -1, -1, 10);
		List<String> trn_filePaths = FileUtils.getFileList("/Users/HenryChen/Desktop/conll-13/train", ".cnlp", true);
		
		trn_filePaths.addAll(FileUtils.getFileList("/Users/HenryChen/Desktop/conll-13/development", ".cnlp", true));
		trn_filePaths.addAll(FileUtils.getFileList("/Users/HenryChen/Desktop/conll-13/test", ".cnlp", true));
		
		Triple<List<DEPTree>, List<AbstractMention>, CoreferantSet> document;
		for(String filePath : trn_filePaths){
			reader.open(IOUtils.createFileInputStream(filePath));
			
			document = reader.getGoldCoNLLDocument();
			if(!hasOverlapCluster(document.o3)){
				CORRECT_COUNT ++;
				MENTION_COUNT += document.o2.size();
			}
			else
				augmentOverlapCount(document.o3);
			
			reader.close(); DOC_COUNT++;
		}
		
		NumberFormat formatter = new DecimalFormat("#0.00");
		System.out.println("Correct clusters: " + CLUSTER_COUNT + " (" + MENTION_COUNT + " mentions)");
		System.out.println("Correct documents: " + formatter.format((double) CORRECT_COUNT / DOC_COUNT * 100) + "% (" + CORRECT_COUNT + "/" + DOC_COUNT + " documents)");
		System.out.println();
		System.out.println("Overlap clusters: " + OVERLAP_CLUSTER_COUNT);
		System.out.println("Overlap mentions: " + OVERLAP_MENTION_COUNT);
	}
	
	public boolean hasOverlapCluster(CoreferantSet set){
		Set<Integer> existed = new HashSet<>();
		List<List<Integer>> clusters = set.getClusterLists(false);
		
		for(List<Integer> cluster : clusters)
			for(int index : cluster){
				if(existed.contains(index))
					return true;
				existed.add(index);
			}
		
		CLUSTER_COUNT += clusters.size();
		return false;
	}
	
	public void augmentOverlapCount(CoreferantSet set){
		List<List<Integer>> clusters = set.getClusterLists(false);
		int i, j, size = clusters.size();
		
		List<Integer> prev_cluster, curr_cluster;
		for(i = 0; i < size - 1; i++){
			prev_cluster = clusters.get(i);
			for(j = i + 1; j < size; j++){
				curr_cluster = clusters.get(j);
				OVERLAP_MENTION_COUNT += CoreferenceDSUtils.getOverlapCount(prev_cluster, curr_cluster, true);
			}
		}
		OVERLAP_CLUSTER_COUNT++;
	}
}
