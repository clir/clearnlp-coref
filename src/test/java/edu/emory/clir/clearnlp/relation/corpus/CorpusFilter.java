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
package edu.emory.clir.clearnlp.relation.corpus;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.relation.extract.AbstractMainEntityExtractor;
import edu.emory.clir.clearnlp.relation.extract.DeterministicMainEntityExtractor;
import edu.emory.clir.clearnlp.relation.structure.Corpus;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.relation.utils.RelationExtractionFileUtil;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 18, 2015
 */
public class CorpusFilter {
	private static final String DIR_IN = "/Users/HenryChen/Desktop/NYTimes_Parsed";
	private static final String DIR_OUT = "/Users/HenryChen/Desktop/NYTimes_Seed";
	
	@Test
	public void filter(){
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		List<String> l_filePaths = FileUtils.getFileList(DIR_IN, ".cnlp", true);
		
		Corpus corpus = RelationExtractionFileUtil.loadCorpus(reader, l_filePaths, "NYTimes", false);
		AbstractMainEntityExtractor extractor = new DeterministicMainEntityExtractor();

		// Clean up directory
		for(String filePath : FileUtils.getFileList(DIR_OUT, ".cnlp", true))
			new File(filePath).delete();
		
		// Export seed files
		PrintWriter writer; String filePath_out; 
		for(Document document : corpus){
			extractor.getMainEntities(document, true);
			
			if(!document.getMainEntities().isEmpty()){
				filePath_out = DIR_OUT + "/" + document.getTitle();
				
				writer = new PrintWriter(IOUtils.createBufferedPrintStream(filePath_out));
				for(DEPTree tree : document.getTrees()){
					for(DEPNode node : tree)
						writer.println(node.toStringNER());
					writer.println();
				}
				writer.close();
			}
		}	
	}

}
