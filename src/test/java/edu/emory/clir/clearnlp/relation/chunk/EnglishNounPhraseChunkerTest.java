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
package edu.emory.clir.clearnlp.relation.chunk;

import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.relation.structure.Chunk;
import edu.emory.clir.clearnlp.relation.structure.Corpus;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.relation.utils.RelationExtractionFileUtil;
import edu.emory.clir.clearnlp.util.FileUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 9, 2015
 */
public class EnglishNounPhraseChunkerTest {
	private static final String DIR_IN = "/Users/HenryChen/Desktop/NYTimes_Test";
	
	@Test
	public void testChunker(){
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		List<String> l_filePaths = FileUtils.getFileList(DIR_IN, ".cnlp", true);
		Corpus corpus = RelationExtractionFileUtil.loadCorpus(reader, l_filePaths, "NYTimes_Test", false);
		
		AbstractChucker chunker = new EnglishNounChucker();
		
		int count = 10;
		for(Document document : corpus){
			System.out.println(document.getTitle());
			for(Chunk chunk : chunker.getChunks(document.getTrees()))
				System.out.println(chunk + " -> " + chunk.getStrippedWordForm(true));
			System.out.println();
			
			if(count-- <= 0) break;
		}
	}
}
