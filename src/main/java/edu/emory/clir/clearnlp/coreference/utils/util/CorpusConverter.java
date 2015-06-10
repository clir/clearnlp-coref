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
package edu.emory.clir.clearnlp.coreference.utils.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.NLPDecoder;
import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Splitter;
import edu.emory.clir.clearnlp.util.constant.StringConst;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 9, 2015
 */
public class CorpusConverter {
	public static void main(String[] args){
		NLPDecoder decoder = new NLPDecoder(TLanguage.ENGLISH);
		List<String> l_filePaths = FileUtils.getFileList("/Users/HenryChen/Desktop/conll-12", ".v4_auto_conll", true);
		
		final String extension = ".cnlp";
		for(String filePath : l_filePaths)
			if(!CoNLL12toCNLP(decoder, IOUtils.createFileInputStream(filePath), IOUtils.createBufferedPrintStream(filePath + extension)))
				System.out.println("WARNING: Empty output for " + filePath);
	}
	
	public static boolean CoNLL12toCNLP(NLPDecoder decoder, InputStream in, OutputStream out){
		Pair<List<List<String>>, List<List<String>>> tokenLinkPair = getCoNLL12Document(in);
		if(tokenLinkPair.o1.isEmpty() || tokenLinkPair.o2.isEmpty())	return false;
		
		PrintWriter writer = new PrintWriter(IOUtils.createBufferedPrintStream(out));
		
		DEPTree tree;	
		List<String> tokens, links;
		int i, i_link, size = tokenLinkPair.o1.size();
		for(i = 0; i < size; i++){
			tokens = tokenLinkPair.o1.get(i);
			links = tokenLinkPair.o2.get(i);
			
			tree = decoder.toDEPTree(tokens);

			i_link = 0;
			for(DEPNode node : tree)	
				writer.println(node.toString() + StringConst.TAB + links.get(i_link++));
			
//			for(j = 1; j < tree.size(); j++){
//				node = tree.get(j);
//				link = (j > 0)? links.get(j-1) : StringConst.HYPHEN;
//				writer.println(node.toString() + "\t" + link);
//			}
			writer.println();
		}
		writer.close();
		return true;
	}
	
	private static Pair<List<List<String>>, List<List<String>>> getCoNLL12Document(InputStream in){
		String line;
		BufferedReader reader; 
		
		List<List<String>> sentences = new ArrayList<>(), links = new ArrayList<>();
		
		try {
			reader = new BufferedReader(IOUtils.createBufferedReader(in));
			
			/* For CoNLL 12 Coref Data */
			String[] cols;
			List<String> sentence = new ArrayList<>(), link = new ArrayList<>(); 
			while( (line = reader.readLine()) != null){
				if(!line.startsWith("#")){
					if(line.isEmpty()){
						links.add(link);			link = new ArrayList<>();
						sentences.add(sentence);	sentence = new ArrayList<>();
						continue;
					}

					cols = Splitter.splitSpace(line);
					
					// Add word token
					sentence.add(cols[3]);
					
					// Add link
					link.add(cols[cols.length-1]);
				}
			}
			
		} catch (Exception e) { e.printStackTrace(); }
		
		return new Pair<>(sentences, links);
	}
}
