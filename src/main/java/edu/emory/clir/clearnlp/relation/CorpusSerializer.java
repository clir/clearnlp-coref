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
package edu.emory.clir.clearnlp.relation;

import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.relation.structure.Corpus;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 2, 2015
 */
public class CorpusSerializer {
	
	public static final String DIR_IN = "/Users/HenryChen/Desktop/NYTimes_Parsed";
	public static final String OBJ_OUT = "/Users/HenryChen/Desktop/NYTimes_Serialized.xz";
	
	public static void main(String[] args){
		
		DEPTree tree; List<DEPTree> trees;
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		List<String> l_filePaths = FileUtils.getFileList(DIR_IN, ".cnlp", true);
		
		Corpus corpus = new Corpus("NYTimes");
		for(String filePath : l_filePaths){
			try {
				reader.open(IOUtils.createFileInputStream(filePath));
				
				trees = new ArrayList<>();
				while( ( tree = reader.next()) != null) trees.add(tree);
				
				corpus.addDocument(FileUtils.getBaseName(filePath), trees);
			} catch (Exception e) { e.printStackTrace(); }
		}
		
		System.out.println(corpus.getCorpusName());
		System.out.println("Document count: " + corpus.getDocumentCount());
		
		try {
			ObjectOutput out = new ObjectOutputStream(IOUtils.createObjectXZBufferedOutputStream(OBJ_OUT));
			try { out.writeObject(corpus); } 
			finally { out.close(); }
		} catch (Exception e) { e.printStackTrace(); }
	}
}
