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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.util.Joiner;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 21, 2015
 */
public class CoreferenceTestUtil {
	public static List<DEPTree> getTestDocuments(String path){
		DEPTree tree;
		List<DEPTree> trees = new ArrayList<>();
		
		try {
			InputStream in = new FileInputStream(path);
			TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
			reader.open(in);

			while ((tree = reader.next()) != null) trees.add(tree);
		} catch (Exception e) {	e.printStackTrace(); }
		
		return trees;
	}
	
	public static List<DEPTree> getTestDocuments(String path, int startIndex, int endIndex, int NERpos){
		return getTestDocuments(path, NERpos).subList(startIndex, endIndex);
	}
	
	public static List<DEPTree> getTestDocuments(String path, int NERpos){
		DEPTree tree;
		List<DEPTree> trees = new ArrayList<>();
		
		try {
			InputStream in = new FileInputStream(path);
			TSVReader reader = new TSVReader(0, 1, 2, 3, NERpos, 4, 5, 6, -1, -1);
			reader.open(in);

			while ((tree = reader.next()) != null) trees.add(tree);
		} catch (Exception e) {	e.printStackTrace(); }
		
		return trees;
	}
	
	public static List<DEPTree> getTestDocuments(String path, int startIndex, int endIndex){
		return getTestDocuments(path).subList(startIndex, endIndex);
	}
	
	public static void printSentences(List<DEPTree> trees){
		System.out.println("===== Sentences =====");
		StringBuilder sb = new StringBuilder();
		for(DEPTree tree : trees){
			for(DEPNode node : tree){
				sb.append(node.getWordForm());
				sb.append(' ');
			}
			sb.append('\n');
		}
		System.out.println(sb.toString());
	}
	
	public static void printCorefCluster(Pair<List<AbstractMention>, CoreferantSet> resolution){
		printCorefCluster(resolution.o1, resolution.o2);
	}
	
	public static void printCorefCluster(List<AbstractMention> mentions, CoreferantSet set){
		System.out.println("===== Clusters =====");

		AbstractMention mention;
		int i, linkedId, size =  mentions.size();
		
		for(i = 0; i < size; i++){
			mention = mentions.get(i);
			linkedId = set.findClusterHead(i);
			
			if(linkedId == i)
				System.out.println(i + ": " + mention.getWordFrom() + "\t(Singleton)");
			else
				System.out.println(i + ": " + mention.getWordFrom() + "\t->\t" + linkedId + ": " + mentions.get(linkedId).getWordFrom());
		}	
	}

	public static void printResolutionResult(Pair<List<AbstractMention>, CoreferantSet> resolution){
		System.out.println("===== Mentions =====");	System.out.println(Joiner.join(resolution.o1, "\n"));
		System.out.println();
		System.out.println("===== Results =====");	System.out.println(resolution.o2);
	}
}
