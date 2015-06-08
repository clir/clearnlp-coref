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
package edu.emory.clir.clearnlp.coreference.utils;

import java.io.PrintWriter;
import java.util.List;
import java.util.StringJoiner;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.coreference.AbstractCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.SieveSystemCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.config.SieveSystemCongiuration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSet;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 8, 2015
 */
public class CorpusReconstructor {
	public static void reconstruct(String input, String output, boolean isDirectory){
		List<DEPTree> trees;
		PrintWriter writer;
		Pair<List<AbstractMention>, DisjointSet> resolution;
		
		/* Coref Configuration */
		SieveSystemCongiuration config = new SieveSystemCongiuration(TLanguage.ENGLISH);
		config.loadMentionDectors(true, true, true);
		config.loadDefaultSieves(true, true, true, true, true, true, true, true);
		AbstractCoreferenceResolution coref = new SieveSystemCoreferenceResolution(config);
		/* ************* */
		
		if(isDirectory){
			List<String> l_filePaths = FileUtils.getFileList(input, ".cnlp", true);
			for(String filePath : l_filePaths){
				trees = CoreferenceTestUtil.getTestDocuments(filePath, 9);
				resolution = coref.getEntities(trees);
				
				writer = new PrintWriter(IOUtils.createBufferedPrintStream(output + FileUtils.getBaseName(filePath) + ".reconstructed"));
				writer.println(reconstruct(trees, resolution.o1, resolution.o2));
				writer.close();
			}
		}
		else{
			trees = CoreferenceTestUtil.getTestDocuments(input, 9);
			resolution = coref.getEntities(trees);
			
			writer = new PrintWriter(IOUtils.createBufferedPrintStream(output + ".reconstructed"));
			writer.println(reconstruct(trees, resolution.o1, resolution.o2));
			writer.close();
		}
	}
	
	private static String reconstruct(List<DEPTree> trees, List<AbstractMention> mentions, DisjointSet links){
		StringJoiner joiner = new StringJoiner(StringConst.SPACE);
		
		String token;
		int m_index = 0, m_size = mentions.size();
		AbstractMention mention = mentions.get(m_index);
		
		for(DEPTree tree : trees){
			for(DEPNode node : tree){
				token = node.getWordForm();
				if(node == mention.getNode()){
					if(!links.isSingleton(m_index))	token = mentions.get(links.findHead(m_index)).getWordFrom();
					if(m_index + 1 < m_size)		mention = mentions.get(++m_index);
				}
				joiner.add(token);
			}
		}
		
		return joiner.toString();
	}
}
