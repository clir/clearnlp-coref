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
package edu.emory.clir.clearnlp.coreference.utils.util;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.StringJoiner;

import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 8, 2015
 */
public class CoreferenceFileUtil {
	public static void concatFiles(List<String> filePaths, String output_fileName, String delim){
		String line;
		BufferedReader reader;
		
		StringJoiner d_joiner, f_joiner = new StringJoiner(delim);
		PrintWriter writer = new PrintWriter(IOUtils.createBufferedPrintStream(output_fileName));
		
		try {
			for(String filePath : filePaths){
				d_joiner = new StringJoiner(" ");
				reader = IOUtils.createBufferedReader(filePath);
				
				while( (line = reader.readLine()) != null)	d_joiner.add(line);
				f_joiner.add(d_joiner.toString());
			}
		} catch (Exception e) { e.printStackTrace(); }
				
		writer.print(f_joiner.toString());
		writer.close();
	}
}
