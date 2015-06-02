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
package edu.emory.clir.clearnlp.coreference.mention;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.coreference.config.MentionConfiguration;
import edu.emory.clir.clearnlp.coreference.mention.detector.AbstractMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.detector.EnglishMentionDetector;
import edu.emory.clir.clearnlp.coreference.path.PathData;
import edu.emory.clir.clearnlp.coreference.utils.CoreferenceTestUtil;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 12, 2015
 */
public class MentionDetectorTest {
	
	@Test
	public void getMentionsTest() throws IOException{
		List<DEPTree> trees = CoreferenceTestUtil.getTestDocuments(PathData.ENG_MENTION);
		OutputStream out = IOUtils.createFileOutputStream(PathData.ENG_MENTION + ".out"); 
		
		StringBuilder sb = new StringBuilder();
		PrintWriter writer = new PrintWriter(out);
		
		List<AbstractMention> mentions;
		AbstractMentionDetector detector = new EnglishMentionDetector(new MentionConfiguration(true, true, true));
		
		for(DEPTree t : trees){
			for(DEPNode n : t)	sb.append(n.getWordForm()+" ");
			writer.println(sb.toString());
			
			mentions = detector.getMentionList(t);
			writer.println(mentions.toString());
			
			writer.println();	sb.setLength(0);
		}
		
		out.close();
		writer.close();
	}
}
