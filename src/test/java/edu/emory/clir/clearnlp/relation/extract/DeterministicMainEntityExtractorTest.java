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
package edu.emory.clir.clearnlp.relation.extract;

import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.relation.structure.Corpus;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.relation.utils.RelationExtractionTestUtil;
import edu.emory.clir.clearnlp.util.FileUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 14, 2015
 */
public class DeterministicMainEntityExtractorTest {
	private static final String DIR_IN = "/Users/HenryChen/Desktop/NYTimes_Parsed";
	
	@Test
	public void testExtractor(){
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		List<String> l_filePaths = FileUtils.getFileList(DIR_IN, ".cnlp", true);
		
		Corpus corpus = RelationExtractionTestUtil.loadCorpus(reader, l_filePaths, "NYTimes", false);
		AbstractMainEntityExtractor extractor = new DeterministicMainEntityExtractor();

		int count = 0;
		for(Document document : corpus){
			extractor.getMainEntities(document, true);
			if(!document.getMainEntities().isEmpty())	count++;
		}	
		System.out.println(count + "/" + corpus.getDocumentCount());
	}
}
