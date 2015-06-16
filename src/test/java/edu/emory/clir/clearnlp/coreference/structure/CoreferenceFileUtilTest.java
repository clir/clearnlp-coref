package edu.emory.clir.clearnlp.coreference.structure;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.coreference.utils.util.CoreferenceFileUtil;
import edu.emory.clir.clearnlp.coreference.utils.util.CoreferenceStringUtils;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Joiner;

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

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 12, 2015
 */
public class CoreferenceFileUtilTest {
	public static final String PATH = "/Users/HenryChen/Desktop/MS_Output/withCommonNoun/train";
	
	@Test
	public void test(){
		List<String> l_filePaths = FileUtils.getFileList(PATH, ".reconstructed", false);
		
		String line;
		BufferedReader reader;
		PrintWriter writer;
		for(String filePath : l_filePaths){
			try {
				reader = IOUtils.createBufferedReader(filePath);
				line = Joiner.join(CoreferenceStringUtils.segmentize2Sentences(reader.readLine()), "\n").replaceAll("\\\\", "");
				reader.close();
				
				writer = new PrintWriter(IOUtils.createBufferedPrintStream(filePath + ".segmentized"));
				writer.print(line);
				writer.close();
				
			} catch (Exception e) { e.printStackTrace(); }
		}
		
		CoreferenceFileUtil.concatFiles(FileUtils.getFileList(PATH, ".segmentized", false), "/Users/HenryChen/Desktop/MS_Output/withCommonNoun/mc500.train", "\n\n");
	}
}
