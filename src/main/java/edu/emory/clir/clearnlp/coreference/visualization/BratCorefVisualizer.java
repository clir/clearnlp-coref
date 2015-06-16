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
package edu.emory.clir.clearnlp.coreference.visualization;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.type.AttributeType;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
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
	
	public static void export(String rootPath, String fileName, List<DEPTree> trees, List<AbstractMention> mentions){
		new BratCorefVisualizer(rootPath).export(fileName, trees, mentions);
	}
	
	public static void export(String rootPath, String fileName, List<DEPTree> trees, List<AbstractMention> mentions, CoreferantSet links){
		new BratCorefVisualizer(rootPath).export(fileName, trees, mentions, links);
	}
	
	public static void export(String rootPath, String fileName, List<String> prefixes, List<DEPTree> trees, List<AbstractMention> mentions, CoreferantSet links){
		new BratCorefVisualizer(rootPath).export(fileName, prefixes, trees, mentions, links);
	}
	
	public void export(String fileName, List<DEPTree> trees, List<AbstractMention> mentions){
		try {
			PrintWriter annotation_writer = new PrintWriter(IOUtils.createBufferedPrintStream(rootPath + fileName + ".ann")),
						sentence_writer = new PrintWriter(IOUtils.createBufferedPrintStream(rootPath + fileName + ".txt"));
			StringJoiner annotation_constructor = new StringJoiner(StringConst.NEW_LINE),
						 sentence_constructor = new StringJoiner(StringConst.SPACE);
			
			int n_index = 0, attrIndex = 0, size = mentions.size(), relCount = 1;
			AbstractMention mention = mentions.get(n_index);
			Map<AbstractMention, Integer> m_mentionIndex = new HashMap<>();
			
			// List out mentions
			for(DEPTree tree : trees){
				for(DEPNode node : tree){
					if(mention != null && node == mention.getNode()){
						n_index++;
						m_mentionIndex.put(mention, n_index);
						annotation_constructor.add(getMentionAnnotation(mention, n_index, sentence_constructor.length()));
						
						// List out attributes
						annotation_constructor.add(getAttributeAnnotation(++attrIndex, n_index, "EntityType", mention.getEntityType().toString()));
						annotation_constructor.add(getAttributeAnnotation(++attrIndex, n_index, "NumberType", mention.getNumberType().toString()));
						annotation_constructor.add(getAttributeAnnotation(++attrIndex, n_index, "GenderType", mention.getGenderType().toString()));
						if(mention.getPronounType() != null) 				annotation_constructor.add(getAttributeAnnotation(++attrIndex, n_index, "PronounType", mention.getPronounType().toString()));
						if(mention.hasAttribute(AttributeType.QUOTE))			annotation_constructor.add(getAttributeAnnotation(++attrIndex, n_index, "Quotation", "true"));
//						if(mention.hasAttribute(AttributeType.CONJUNCTION)){
//							annotation_constructor.add(getAttributeAnnotation(++attrIndex, n_index, "Conjunction", "true"));
//							annotation_constructor.add(getConjuncRelationAnnotation(relCount++, m_mentionIndex.get(mention.getConjunctionMention()), n_index));
//						}
						
						mention = (n_index < size)? mentions.get(n_index) : null;
					}
					sentence_constructor.add(node.getWordForm());
				}
			}
			
			annotation_writer.println(annotation_constructor.toString());	annotation_writer.close();
			sentence_writer.println(sentence_constructor.toString());		sentence_writer.close();
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public void export(String fileName, List<DEPTree> trees, List<AbstractMention> mentions, CoreferantSet links){
		try {
			PrintWriter annotation_writer = new PrintWriter(IOUtils.createBufferedPrintStream(rootPath + fileName + ".ann")),
					sentence_writer = new PrintWriter(IOUtils.createBufferedPrintStream(rootPath + fileName + ".txt"));
			StringJoiner annotation_constructor = new StringJoiner(StringConst.NEW_LINE),
						 sentence_constructor = new StringJoiner(StringConst.SPACE);
			
			int n_index = 0, attrIndex = 0, size = mentions.size(), link, relCount = 1;
			AbstractMention mention = mentions.get(n_index);
			Map<AbstractMention, Integer> m_mentionIndex = new HashMap<>();
			
			// List out mentions
			for(DEPTree tree : trees){
				for(DEPNode node : tree){
					if(mention != null && node == mention.getNode()){
						n_index++;
						m_mentionIndex.put(mention, n_index);
						annotation_constructor.add(getMentionAnnotation(mention, n_index, sentence_constructor.length()));
						
						// List out attributes
						annotation_constructor.add(getAttributeAnnotation(++attrIndex, n_index, "EntityType", mention.getEntityType().toString()));
						annotation_constructor.add(getAttributeAnnotation(++attrIndex, n_index, "NumberType", mention.getNumberType().toString()));
						annotation_constructor.add(getAttributeAnnotation(++attrIndex, n_index, "GenderType", mention.getGenderType().toString()));
						if(mention.getPronounType() != null) 				annotation_constructor.add(getAttributeAnnotation(++attrIndex, n_index, "PronounType", mention.getPronounType().toString()));
						if(mention.hasAttribute(AttributeType.QUOTE))			annotation_constructor.add(getAttributeAnnotation(++attrIndex, n_index, "Quotation", "true"));
//						if(mention.hasAttribute(AttributeType.CONJUNCTION)){
//							annotation_constructor.add(getAttributeAnnotation(++attrIndex, n_index, "Conjunction", "true"));
//							annotation_constructor.add(getConjuncRelationAnnotation(relCount++, m_mentionIndex.get(mention.getConjunctionMention()), n_index));
//						}
						
						mention = (n_index < size)? mentions.get(n_index) : null;
					}
					sentence_constructor.add(node.getWordForm());
				}
			}
			
			// List out links
			for(n_index = 0; n_index < size; n_index++)
				if( (link = links.findClosest(n_index)) != n_index)
					annotation_constructor.add(getCorefRelationAnnotation(relCount++, link, n_index));
			
			annotation_writer.println(annotation_constructor.toString());	annotation_writer.close();
			sentence_writer.println(sentence_constructor.toString());		sentence_writer.close();
		} catch (Exception e) { e.printStackTrace(); }
	}
	 
	public void export(String fileName, List<String> prefixes, List<DEPTree> trees, List<AbstractMention> mentions, CoreferantSet links){
		if(prefixes.size() != trees.size()) throw new IllegalArgumentException("Prefix counts do not match with tree counts.");
		try {
			PrintWriter annotation_writer = new PrintWriter(IOUtils.createBufferedPrintStream(rootPath + fileName + ".ann")),
					sentence_writer = new PrintWriter(IOUtils.createBufferedPrintStream(rootPath + fileName + ".txt"));
			StringJoiner annotation_constructor = new StringJoiner(StringConst.NEW_LINE),
						 sentence_constructor = new StringJoiner(StringConst.SPACE);
			
			int t_index = 0, n_index = 0, attrIndex = 0, size = mentions.size(), link, relCount = 1;
			AbstractMention mention = mentions.get(n_index);
			Map<AbstractMention, Integer> m_mentionIndex = new HashMap<>();
			
			// List out mentions
			for(DEPTree tree : trees){
				sentence_constructor.add(prefixes.get(t_index++)+":");
				for(DEPNode node : tree){
					if(mention != null && node == mention.getNode()){
						n_index++;
						m_mentionIndex.put(mention, n_index);
						annotation_constructor.add(getMentionAnnotation(mention, n_index, sentence_constructor.length()));
						
						// List out attributes
						annotation_constructor.add(getAttributeAnnotation(++attrIndex, n_index, "EntityType", mention.getEntityType().toString()));
						annotation_constructor.add(getAttributeAnnotation(++attrIndex, n_index, "NumberType", mention.getNumberType().toString()));
						annotation_constructor.add(getAttributeAnnotation(++attrIndex, n_index, "GenderType", mention.getGenderType().toString()));
						if(mention.getPronounType() != null) 				annotation_constructor.add(getAttributeAnnotation(++attrIndex, n_index, "PronounType", mention.getPronounType().toString()));
						if(mention.hasAttribute(AttributeType.QUOTE))			annotation_constructor.add(getAttributeAnnotation(++attrIndex, n_index, "Quotation", "true"));
//						if(mention.hasAttribute(AttributeType.CONJUNCTION)){
//							annotation_constructor.add(getAttributeAnnotation(++attrIndex, n_index, "Conjunction", "true"));
//							annotation_constructor.add(getConjuncRelationAnnotation(relCount++, m_mentionIndex.get(mention.getConjunctionMention()), n_index));
//						}
						
						mention = (n_index < size)? mentions.get(n_index) : null;
					}
					sentence_constructor.add(node.getWordForm());
				}
			}
			
			// List out links
			for(n_index = 0; n_index < size; n_index++)
				if( (link = links.findClosest(n_index)) != n_index)
					annotation_constructor.add(getCorefRelationAnnotation(relCount++, link, n_index));
			
			annotation_writer.println(annotation_constructor.toString());	annotation_writer.close();
			sentence_writer.println(sentence_constructor.toString());		sentence_writer.close();
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	private String getMentionAnnotation(AbstractMention mention, int index, int offset){
		String wordForm = mention.getWordFrom();
		StringJoiner joiner = new StringJoiner(StringConst.TAB);
		
		if(offset > 0) offset ++;
		joiner.add("T"+index);
		joiner.add("Mention" + " " + offset + " " + (offset+wordForm.length()));
		joiner.add(wordForm);
		
		return joiner.toString();
	}
	
	private String getCorefRelationAnnotation(int relId, int prevId, int currId){
		StringJoiner joiner = new StringJoiner(StringConst.SPACE, "R"+relId+"\t", "");
		joiner.add("Coreference");
		joiner.add("Arg1:T"+(prevId+1));
		joiner.add("Arg2:T"+(currId+1));
		return joiner.toString();
	}
	
	private String getConjuncRelationAnnotation(int relId, int prevId, int currId){
		StringJoiner joiner = new StringJoiner(StringConst.SPACE, "R"+relId+"\t", "");
		joiner.add("Conjunction");
		joiner.add("Arg1:T"+(currId));
		joiner.add("Arg2:T"+(prevId));
		return joiner.toString();
	}
	
	private String getAttributeAnnotation(int AttrIndex, int MentionIndex, String AttrType, String AttrVal){
		StringJoiner joiner = new StringJoiner(StringConst.SPACE, "A"+AttrIndex+"\t", "");
		joiner.add(AttrType);
		joiner.add("T"+MentionIndex);
		joiner.add(AttrVal);
		return joiner.toString();
	}
}
