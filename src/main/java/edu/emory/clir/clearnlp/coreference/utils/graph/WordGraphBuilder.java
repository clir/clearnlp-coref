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
package edu.emory.clir.clearnlp.coreference.utils.graph;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.Splitter;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 18, 2015
 */
public class WordGraphBuilder {
	public static void main(String[] args){
		List<String> paths = FileUtils.getFileList("/Users/HenryChen/Desktop/WordGraphDict/", ".parsed", false);
		
		int i;
		String line; 
		String[] words;
		BufferedReader reader;

		WordNode n1, n2;
		WordGraph graph = new WordGraph();
		
		for(String path : paths){
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
				
				while( (line = reader.readLine()) != null){
					words = Splitter.splitTabs(line);
					
					n1 = graph.addNode(words[0]);
					for(i = 1; i < words.length; i++){
						n2 = graph.addNode(words[i]);
						graph.connenctNodes(n1, n2);
					}
				}
				
				reader.close();
			} catch (Exception e) {	e.printStackTrace(); }
		}
		
		System.out.println(graph.getNode("a"));
	}
}
