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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.tokenization.AbstractTokenizer;
import edu.emory.clir.clearnlp.tokenization.EnglishTokenizer;
import edu.emory.clir.clearnlp.util.CharUtils;
import edu.emory.clir.clearnlp.util.Joiner;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Apr 19, 2015
 */
public class CoreferenceStringUtils {
	private CoreferenceStringUtils(){ }
	
	public static String connectStrings(String... strings){
		StringBuilder sb = new StringBuilder();
		for(String s : strings)	sb.append(s);
		return sb.toString();
	}
	
	public static String getAllUpperCaseLetters(String string){
		StringBuilder sb = new StringBuilder();
		
		for(char c : string.toCharArray())
			if(CharUtils.isUpperCase(c))	sb.append(c);
		
		return sb.toString();
	}
	
	public static List<Integer> getAllIndicesOf(String line, char delim){
		List<Integer> list = new ArrayList<>();
		
		int i, size = line.length();
		for(i = 0; i < size; i++)
			if(line.charAt(i) == delim)
				list.add(i);
		
		return list;
	}
	
	public static List<Integer> getAllIndicesOf(String line, Set<Character> delims){
		List<Integer> list = new ArrayList<>();
		
		int i, size = line.length();
		for(i = 0; i < size; i++)
			if(delims.contains(line.charAt(i)))	
				list.add(i);
		
		return list;
	}
	
	public static List<String> tokenize(String line){
		AbstractTokenizer tokenizer = new EnglishTokenizer();
		return tokenizer.tokenize(line);
	}
	
	public static List<String> segmentize2Sentences(String document){
		AbstractTokenizer tokenizer = new EnglishTokenizer();
		InputStream in = new ByteArrayInputStream(document.getBytes());
		return tokenizer.segmentize(in).stream().map(l -> Joiner.join(l, " ")).collect(Collectors.toList());
	}
	
	public static List<List<String>> segmentize2Tokens(String document){
		AbstractTokenizer tokenizer = new EnglishTokenizer();
		InputStream in = new ByteArrayInputStream(document.getBytes());
		return tokenizer.segmentize(in);
	}
}
