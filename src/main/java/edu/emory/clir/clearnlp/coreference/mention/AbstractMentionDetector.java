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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.collection.ngram.Unigram;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Splitter;
import edu.emory.clir.clearnlp.util.StringUtils;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class AbstractMentionDetector
{
//	====================================== LEXICA ======================================
	
	protected void addDictionary(InputStream in, Unigram<String> map)
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		String line, token;
		String[] t;
		int count;
		
		try
		{
			while ((line = reader.readLine()) != null)
			{
				t = Splitter.splitTabs(line);
				token = StringUtils.toLowerCase(t[0]);
				count = Integer.parseInt(t[1]);
				map.add(token, count);
			}
		}
		catch (IOException e) {e.printStackTrace();}
	}

//	====================================== GETTER ======================================

	public List<Mention> getMentionList(List<DEPTree> trees)
	{
		List<Mention> list = new ArrayList<>();
		
		for (DEPTree tree : trees) list.addAll(getMentionList(tree));
		
		return list;
	}
	
	public List<Mention> getMentionList(DEPTree tree)
	{
		List<Mention> list = new ArrayList<>();
		Mention mention;
		
		for (DEPNode node : tree){
			mention = getMention(tree, node);
			if (mention != null) list.add(mention);
		}
		
		processMentions(tree, list);
		
		return list;
	}
	
	public abstract Mention getMention(DEPTree tree, DEPNode node);
	protected abstract void processMentions(DEPTree tree, List<Mention> mentions);
}
