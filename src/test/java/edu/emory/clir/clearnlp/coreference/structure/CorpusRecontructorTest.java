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
package edu.emory.clir.clearnlp.coreference.structure;

import java.io.BufferedReader;
import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.NLPDecoder;
import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.coreference.AbstractCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.SieveSystemCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.config.SieveSystemCongiuration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.sieve.ExactStringMatch;
import edu.emory.clir.clearnlp.coreference.sieve.RelaxedStringMatch;
import edu.emory.clir.clearnlp.coreference.sieve.SimplePronounMatch;
import edu.emory.clir.clearnlp.coreference.sieve.SpeakerIdentification;
import edu.emory.clir.clearnlp.coreference.utils.CorpusReconstructor;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Splitter;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 8, 2015
 */
public class CorpusRecontructorTest {
	@Test
	public void test(){
		NLPDecoder decoder = new NLPDecoder(TLanguage.ENGLISH);
		List<String> l_filePath = FileUtils.getFileList("/Users/HenryChen/Desktop/MS_Input", ".tsv", true);
		
		/* Configuration */
		SieveSystemCongiuration config = new SieveSystemCongiuration(TLanguage.ENGLISH);
		config.loadMentionDectors(true, true, true);
		config.mountSieves(new SpeakerIdentification(), new ExactStringMatch(), new RelaxedStringMatch(), new SimplePronounMatch());
		/* ************* */
		
		Pair<List<AbstractMention>, CoreferantSet> resolution;
		AbstractCoreferenceResolution coref = new SieveSystemCoreferenceResolution((SieveSystemCongiuration)config);
		
		int count;
		String line, out;
		List<DEPTree> trees;
		BufferedReader reader;
		for(String filePath : l_filePath){
			count = 0;
			
			try {
				reader = IOUtils.createBufferedReader(filePath);
				
				while( (line = reader.readLine()) != null){
					line = Splitter.splitTabs(line)[2].replaceAll("\\\\newline", "");
					trees = decoder.toDEPTrees(line);
					resolution = coref.getEntities(trees);
					
					out = "/Users/HenryChen/Desktop/MS_Output/" + FileUtils.getBaseName(filePath) + ".comprehension" + count++;
					CorpusReconstructor.reconstruct(trees, resolution.o1, resolution.o2, out, true);
				}
				
				reader.close();
			} catch (Exception e) {	e.printStackTrace(); }
		}
	}
}
