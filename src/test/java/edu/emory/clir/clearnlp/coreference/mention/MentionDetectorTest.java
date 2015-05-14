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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 12, 2015
 */
public class MentionDetectorTest {
	
	@Test
	public void getMentionsTest() throws IOException{
		InputStream in = new FileInputStream("src/test/resources/edu/emory/clir/clearnlp/coreference/mention/input.mention.cnlp");
		OutputStream out = new FileOutputStream("src/test/resources/edu/emory/clir/clearnlp/coreference/mention/input.mention.out");
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		reader.open(in);
		
		DEPTree tree;
		List<Mention> mentions;
		List<DEPTree> trees = new ArrayList<>();
		
		while ((tree = reader.next()) != null) trees.add(tree);
		
		StringBuilder sb = new StringBuilder();
		PrintWriter writer = new PrintWriter(out);
		AbstractMentionDetector detector = new EnglishMentionDetector();
		
		for(DEPTree t : trees){
			for(DEPNode n : t)	sb.append(n.getWordForm()+" ");
			writer.println(sb.toString());
			
			mentions = detector.getMentionList(t);
			writer.println(mentions.toString());
			
			writer.println();
			sb.setLength(0);
		}
		
		out.close();
		writer.close();
	}
}
