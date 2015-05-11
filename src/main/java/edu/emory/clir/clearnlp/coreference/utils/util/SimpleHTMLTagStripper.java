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

import java.util.ArrayList;
import java.util.List;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 5, 2015
 */
public class SimpleHTMLTagStripper {
	
	public static String stripAllTags(String source){
		StringBuilder sb = new StringBuilder();
		int index = source.indexOf('<'), len = source.length();
		
		while(index >= 0 && index < len){
			while(index < len && source.charAt(index++) != '>');
			for(; index < len && source.charAt(index) != '<'; index++) sb.append(source.charAt(index));
		}
		
		return sb.toString();
	}
	
	public static List<String> getAllTagContent(String source, String tag){
		List<String> list = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		
		// Formulate start and end tags
		sb.append('<'); sb.append(tag);
		String startTag = sb.toString(); sb.setLength(0);
		sb.append("</"); sb.append(tag); sb.append('>');
		String endTag = sb.toString(); sb.setLength(0);
		
		int beginIndex, endIndex, len;
		while((beginIndex = source.toLowerCase().indexOf(startTag)) != -1){
			len = source.length();
			while(beginIndex < len && source.charAt(beginIndex++) != '>');
			endIndex = source.toLowerCase().indexOf(endTag);
			
			list.add(source.substring(beginIndex, endIndex));
			source = source.substring(endIndex+3);
		}
	
		return list;
	}
	
	public static String getContent(String source, String attrName){
		StringBuilder sb = new StringBuilder();
		int index = source.indexOf(attrName), len = source.length();
		
		if(index >= 0){
			while(index < len && source.charAt(index++) != '"');
			for(; index < len && source.charAt(index) != '"'; index++)
				sb.append(source.charAt(index));
		}
		
		return sb.toString();
	}
}
