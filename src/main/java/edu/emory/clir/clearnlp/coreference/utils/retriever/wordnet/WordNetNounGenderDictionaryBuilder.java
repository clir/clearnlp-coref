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
package edu.emory.clir.clearnlp.coreference.utils.retriever.wordnet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import edu.emory.clir.clearnlp.coreference.dictionary.PathMention;
import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Splitter;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 15, 2015
 */
public class WordNetNounGenderDictionaryBuilder {
	public static void main(String[] args) throws IOException{
		final String filePath = "/Users/HenryChen/Desktop/Wordnet/cwn-noun-lfs.txt";
		final InputStream in  = new FileInputStream(filePath);
		final OutputStream out = IOUtils.createBufferedPrintStream(filePath +".dict");
		
		int index;
		String[] temp;
		String line, word;
		Set<String> connections;
		Map<String, Set<String>> dict = new HashMap<>();
		BufferedReader reader  = new BufferedReader(new InputStreamReader(in));
		
		while( (line = reader.readLine()) != null){
			if(!line.isEmpty()){
				temp = Splitter.split(line, Pattern.compile("->"));
				
				index = (temp[0].indexOf('\'') != -1)? temp[0].indexOf('\'') : temp[0].indexOf('(');
				word = temp[0].substring(0, index).trim();
				
				connections = new HashSet<>();
				temp = Splitter.split(temp[1], Pattern.compile("&"));
				for(String s : temp){
					index = (s.indexOf('\'') != -1)? s.indexOf('\'') : s.indexOf('(');
					connections.add(s.substring(0, index).trim());
				}
				
				if(!dict.containsKey(word))
					dict.put(word, connections);
				else
					dict.get(word).addAll(connections);
			}
		}
		reader.close();
		
		int count = 0, iteration = 100;
		Map<String, Double> male_nouns = new HashMap<>(), female_nouns = new HashMap<>(), neutral_nouns = new HashMap<>();
		
		String[] attr;
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(PathMention.ENG_COMMON_NOUN)));
		while( (line = reader.readLine()) != null){
			attr = Splitter.splitTabs(line);
			switch(EntityType.valueOf(attr[1])){
				case COMMON_MALE:
					male_nouns.put(attr[0], 1d);
					break;
				case COMMON_FEMALE:
					female_nouns.put(attr[0], 1d);
					break;
				default:
						break;
			}
		}
		neutral_nouns.put("person", 1d);	neutral_nouns.put("people", 1d);	neutral_nouns.put("human", 1d);
			
		double delta = 0.95d;
		while(count++ < iteration){
			for(Entry<String, Set<String>> e : dict.entrySet()){
				for(String s : e.getValue())	{
					if(male_nouns.containsKey(s)){		male_nouns.putIfAbsent(e.getKey(), male_nouns.get(s) * Math.pow(delta, count));			break; }
					if(female_nouns.containsKey(s)){	female_nouns.putIfAbsent(e.getKey(), female_nouns.get(s) * Math.pow(delta, count));		break; }
					if(neutral_nouns.containsKey(s)){	neutral_nouns.putIfAbsent(e.getKey(), neutral_nouns.get(s) * Math.pow(delta, count));	break; }
				}
			}
		}
		
		for(Entry<String, Double> e : male_nouns.entrySet()){
			if(female_nouns.containsKey(e.getKey()) && e.getValue() > female_nouns.get(e.getKey()))	female_nouns.remove(e.getKey());
		}
		
		for(Entry<String, Double> e : female_nouns.entrySet()){
			if(male_nouns.containsKey(e.getKey()) && e.getValue() > male_nouns.get(e.getKey()))	male_nouns.remove(e.getKey());
		}
		
		for(String s : neutral_nouns.keySet()){
			if(male_nouns.containsKey(s)) male_nouns.remove(s);
			if(female_nouns.containsKey(s)) female_nouns.remove(s);
		}
		
		PrintWriter writer = new PrintWriter(out);
		for(Entry<String, Double> e : male_nouns.entrySet())	writer.println(e.getKey() + "\t" + EntityType.COMMON_MALE + "\t" + e.getValue());
		for(Entry<String, Double> e : female_nouns.entrySet())	writer.println(e.getKey() + "\t" + EntityType.COMMON_FEMALE + "\t" + e.getValue());
		for(Entry<String, Double> e : neutral_nouns.entrySet())	writer.println(e.getKey() + "\t" + EntityType.COMMON_NEUTRAL + "\t" + e.getValue());
		writer.close();
	}
}
