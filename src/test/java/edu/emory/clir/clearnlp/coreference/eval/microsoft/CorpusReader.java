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
package edu.emory.clir.clearnlp.coreference.eval.microsoft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import edu.emory.clir.clearnlp.NLPDecoder;
import edu.emory.clir.clearnlp.coreference.path.PathData;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Splitter;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 27, 2015
 */
public class CorpusReader {
	public static void main(String[] args) throws IOException{
		String line;
		NLPDecoder decoder = new NLPDecoder(TLanguage.ENGLISH);
		BufferedReader reader = IOUtils.createBufferedReader(PathData.ENG_COREF_MICROSOFT_RAW);
		PrintWriter writer;
		
		int count = 0;
		List<DEPTree> trees;
		while( (line = reader.readLine()) != null){
			writer = new PrintWriter(IOUtils.createFileOutputStream("/Users/HenryChen/Documents/ClearNLP-QA/clearnlp-coref/src/test/resources/edu/emory/clir/clearnlp/coreference/coref/microsoft/parsed/mc500.dev.comprehension"+(++count)+".cnlp"));
			trees = decoder.toDEPTrees(Splitter.splitTabs(line)[2]);
			for(DEPTree tree : trees)	writer.println(tree+"\n");
			writer.close();
		}
	}
}
