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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.coreference.utils.retriever.AbstractRestAPIDataRetriever;
import edu.emory.clir.clearnlp.coreference.utils.util.SimpleHTMLTagStripper;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 5, 2015
 */
public class FriendsScriptRetriever extends AbstractRestAPIDataRetriever{
	final static String ROOT_URL = "http://www.livesinabox.com/friends/";
	final static String INDEX_URL = "http://www.livesinabox.com/friends/scripts.shtml";
	final static String OUTPUT_DIRPATH = "/Users/HenryChen/Desktop/FriendScripts/";
	public FriendsScriptRetriever(String url) { super(url); }
	
	public static void main(String[] args) throws Exception{
		FriendsScriptRetriever retreiver = new FriendsScriptRetriever(INDEX_URL);
		
		// Getting all scripts' urls
		List<String> scriptUrls = new ArrayList<>();
		String indexSourceCode = retreiver.getSourceCode(INDEX_URL).substring(10000);
		
		for(String s : SimpleHTMLTagStripper.getAllTagContent(indexSourceCode, "li"))
			scriptUrls.add(ROOT_URL + SimpleHTMLTagStripper.getContent(s, "href"));
		
		String title;
		List<String> lines;
		for(String s : scriptUrls){
			System.out.println(s);
			title = s.substring(s.lastIndexOf('/')+1);
			title = title.substring(0, title.lastIndexOf('.'));
			lines = retreiver.parseScript(s);
			
			File fout = new File(OUTPUT_DIRPATH+title);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fout)));
			for(String line : lines){
				writer.write(line);
				writer.newLine();
			}
			writer.close();
		}
	}
	
	private List<String> parseScript(String url) throws Exception{
		List<String> lines = new ArrayList<>();
		String source = getSourceCode(url); 
		
		for(String s : SimpleHTMLTagStripper.getAllTagContent(source, "p"))
			lines.add(SimpleHTMLTagStripper.stripAllTags(s).replaceAll("\n", " "));
		
		return lines;
	}
	
}
