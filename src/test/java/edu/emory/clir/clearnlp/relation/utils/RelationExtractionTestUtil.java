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
package edu.emory.clir.clearnlp.relation.utils;

import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.AbstractReader;
import edu.emory.clir.clearnlp.relation.structure.Corpus;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 6, 2015
 */
public class RelationExtractionTestUtil {
	public static Corpus loadCorpus(AbstractReader<DEPTree> reader, List<String> l_filePaths, String corpusName){
		DEPTree tree; List<DEPTree> trees;
		
		boolean existed; String fileName;
		Corpus corpus = new Corpus(corpusName);
		for(String filePath : l_filePaths){
			existed = false;
			fileName = FileUtils.getBaseName(filePath);
			for(Document document : corpus)
				if(document.getTitle().equals(fileName)){
					existed = true; break;
				}
					
			if(!existed){
				try {
					reader.open(IOUtils.createFileInputStream(filePath));
					
					trees = new ArrayList<>();
					while( ( tree = reader.next()) != null) trees.add(tree);
					
					corpus.addDocument(fileName, trees);
				} catch (Exception e) { e.printStackTrace(); }
			}
		}
		
		return corpus;
	}
}
