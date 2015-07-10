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

import java.io.PrintWriter;
import java.util.List;

import edu.emory.clir.clearnlp.NLPDecoder;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.constant.CharConst;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 8, 2015
 */
public class CorpusDecoder {
	public static final String DIR_IN = "/Users/HenryChen/Desktop/NYTimes_Parsed";
	public static final String EXT = ".title";
	
	public static void main(String[] args){
		NLPDecoder decoder = new NLPDecoder(TLanguage.ENGLISH);
		List<String> l_filePaths = FileUtils.getFileList(DIR_IN, ".cnlp", true);
		
		String title;
		DEPTree tree;
		int start, end;
		PrintWriter writer;
		
		
		for(String filePath : l_filePaths){
			title = FileUtils.getBaseName(filePath);
			
			start = title.indexOf(CharConst.UNDERSCORE);
			end = title.lastIndexOf(CharConst.HYPHEN);			
			title = title.substring(start+1, end);
			
			writer = new PrintWriter(IOUtils.createBufferedPrintStream(filePath + EXT));
			tree = decoder.toDEPTree(title);
			for(DEPNode node : tree) writer.println(node.toStringNER());
			writer.close();
		}
	}
}
