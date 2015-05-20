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
package edu.emory.clir.clearnlp.coreference.utils.retriever.wordnet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 15, 2015
 */
public class WordNetNounGenderDictionaryBuilder {
	public static void main(String[] args){
		
		InputStream in;
		OutputStream out;
		
		List<String> filePaths = FileUtils.getFileList("/Users/HenryChen/Desktop/Wordnet/WordNet-Noun", "", false);
		
		
		String line, word;
		for(String filePath : filePaths){
			try {
				in  = new FileInputStream(filePath);
				out = IOUtils.createBufferedPrintStream(filePath +".parsed");
				
				
				
			} catch (Exception e) { e.printStackTrace(); }
		}
		
		
		
		
//		int index;
//		String[] temp;
//		String line, word;
//		Set<String> connections;
//		Map<String, Set<String>> dict = new HashMap<>();
//		BufferedReader reader  = new BufferedReader(new InputStreamReader(in));
//		
//		while( (line = reader.readLine()) != null){
//			if(!line.isEmpty()){
//				temp = Splitter.split(line, Pattern.compile("->"));
//				
//				index = (temp[0].indexOf('\'') != -1)? temp[0].indexOf('\'') : temp[0].indexOf('(');
//				word = temp[0].substring(0, index).trim();
//				
//				connections = new HashSet<>();
//				temp = Splitter.split(temp[1], Pattern.compile("&"));
//				for(String s : temp){
//					index = (s.indexOf('\'') != -1)? s.indexOf('\'') : s.indexOf('(');
//					connections.add(s.substring(0, index).trim());
//				}
//				
//				if(!dict.containsKey(word))
//					dict.put(word, connections);
//				else
//					dict.get(word).addAll(connections);
//			}
//		}
//		reader.close();
//		
//		PrintWriter writer = new PrintWriter(out);
//		for(Entry<String, Set<String>> e : dict.entrySet())
//			writer.println(e.getKey() + "\t" + Joiner.join(e.getValue(), "\t"));
//		writer.close();
	}
}
