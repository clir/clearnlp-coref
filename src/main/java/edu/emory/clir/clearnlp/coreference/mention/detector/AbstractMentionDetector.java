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
package edu.emory.clir.clearnlp.coreference.mention.detector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.collection.ngram.Unigram;
import edu.emory.clir.clearnlp.coreference.config.MentionConfiguration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
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
	protected MentionConfiguration m_config;
	
	public AbstractMentionDetector(MentionConfiguration config){
		m_config = config;
	}
	
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

	public List<AbstractMention> getMentionList(List<DEPTree> trees){
		int treeCount = 0;
		List<AbstractMention> list = new ArrayList<>();
		
		for (DEPTree tree : trees) 
			list.addAll(getMentionList(treeCount++, tree));
		processMentions(trees, list);
		
		return list;
	}

	public List<AbstractMention> getMentionList(DEPTree tree){
		List<AbstractMention> list = getMentionList(-1, tree);
		for(int i = 0; i < list.size(); i++)
			list.get(i).setMentionId(i);
		return list;
	}
	
	protected List<AbstractMention> getMentionList(int treeId, DEPTree tree){
		List<AbstractMention> list = new ArrayList<>();
		AbstractMention mention;
		
		for (DEPNode node : tree){
			mention = getMention(treeId, tree, node);
			if (mention != null) list.add(mention);
		}
		
		return list;
	}
	
	public abstract AbstractMention getMention(int treeId, DEPTree tree, DEPNode node);
	protected abstract void processMentions(List<DEPTree> tree, List<AbstractMention> mentions);
}
