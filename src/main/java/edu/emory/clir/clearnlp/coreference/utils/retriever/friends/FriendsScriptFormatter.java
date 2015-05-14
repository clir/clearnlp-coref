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
package edu.emory.clir.clearnlp.coreference.utils.retriever.friends;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.List;

import edu.emory.clir.clearnlp.util.FileUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 8, 2015
 */
public class FriendsScriptFormatter {
	public static void main(String[] args) throws Exception{
		final String inputDir = "/Users/HenryChen/Desktop/FriendScripts/season1";
		final String inputExt = "";
		
		List<String> filenames = FileUtils.getFileList(inputDir, inputExt, true);
		Collections.sort(filenames);	filenames.remove(0);
		
		String line;
		StringBuilder sb = new StringBuilder();
		BufferedReader reader; 
		BufferedWriter writer;
		
		for(String filename : filenames){
			// Info display
			System.out.println("Stripping empty line for " + filename + " ...");
			
			// Reading and formatting data
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			
			while((line = reader.readLine()) != null){
				if(!line.isEmpty()){
					sb.append(line);
					sb.append('\n');
				}
			}
			
			reader.close();
			
			//==============//
			// Write to file
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)));
			writer.write(sb.toString());
			writer.close();
			
			//==============//
			// Reset objects
			sb.setLength(0);
		}
	}
}
