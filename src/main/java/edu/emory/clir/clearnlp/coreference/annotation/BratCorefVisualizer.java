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
package edu.emory.clir.clearnlp.coreference.annotation;

import java.io.PrintWriter;
import java.util.List;
import java.util.StringJoiner;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSet;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.constant.CharConst;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 4, 2015
 */
public class BratCorefVisualizer {
	private String rootPath;
	
	public BratCorefVisualizer(){
		this.rootPath = "";
	}
	
	public BratCorefVisualizer(String rootPath){
		setRootPath(rootPath);
	}
	
	public String getRootPath(){
		return rootPath;
	}
	
	public void setRootPath(String rootPath){
		if(rootPath.charAt(rootPath.length()-1) != CharConst.FW_SLASH);
			rootPath = rootPath + "/";
		this.rootPath = rootPath;
	}
	
	public static void export(String rootPath, String fileName, List<DEPTree> trees, List<AbstractMention> mentions, DisjointSet links){
		new BratCorefVisualizer(rootPath).export(fileName, trees, mentions, links);
	}
	
	public void export(String fileName, List<DEPTree> trees, List<AbstractMention> mentions, DisjointSet links){
		PrintWriter annotation_writer = new PrintWriter(IOUtils.createBufferedPrintStream(rootPath + fileName + ".ann")),
					sentence_writer = new PrintWriter(IOUtils.createBufferedPrintStream(rootPath + fileName + ".txt"));
		StringJoiner annotation_constructor = new StringJoiner(StringConst.NEW_LINE),
					 sentence_constructor = new StringJoiner(StringConst.SPACE);
		
		int index = 0, size = mentions.size();
		AbstractMention mention = mentions.get(index);
		
		// List out mentions
		for(DEPTree tree : trees)
			for(DEPNode node : tree){
				if(mention != null && node == mention.getNode()){
					index++;
					annotation_constructor.add(getMentionAnnotation(mention, index, sentence_constructor.length()));
					mention = (index < size)? mentions.get(index) : null;
				}
				sentence_constructor.add(node.getWordForm());
			}
		
		// List out links
		int link, relCount = 1;
		for(index = 0; index < size; index++)
			if( (link = links.findClosest(index)) != index)
				annotation_constructor.add(getRelationAnnotation(relCount++, link, index));
		
		annotation_writer.println(annotation_constructor.toString());	annotation_writer.close();
		sentence_writer.println(sentence_constructor.toString());		sentence_writer.close();
	}
	 
	private String getMentionAnnotation(AbstractMention mention, int index, int offset){
		String wordForm = mention.getWordFrom();
		StringJoiner joiner = new StringJoiner(StringConst.TAB);
		
		offset ++;
		joiner.add("T"+index);
		joiner.add("Mention" + " " + offset + " " + (offset+wordForm.length()));
		joiner.add(wordForm);
		
		return joiner.toString();
	}
	
	private String getRelationAnnotation(int id, int prevId, int currId){
		StringJoiner joiner = new StringJoiner(StringConst.SPACE, "R"+id+"\t", "");
		joiner.add("Coreference");
		joiner.add("Arg1:T"+(prevId+1));
		joiner.add("Arg2:T"+(currId+1));
		return joiner.toString();
	}
}
