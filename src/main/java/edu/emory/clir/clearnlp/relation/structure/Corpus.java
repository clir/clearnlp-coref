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
package edu.emory.clir.clearnlp.relation.structure;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 5, 2015
 */
public class Corpus implements Serializable, Iterable<Document>{
	private static final long serialVersionUID = 6576324062033594566L;
	
	private String s_corpusName;
	private List<Document> l_documents;
	
	public Corpus(){
		l_documents = new ArrayList<>();
	}
	
	public Corpus(String corpusName){
		s_corpusName = corpusName;
		l_documents = new ArrayList<>();
	}
	
	public Corpus(ObjectInputStream obj){
		try {
		    ObjectInput in = new ObjectInputStream(obj);
		    try {
		    	Corpus object = (Corpus) in.readObject();
		    	this.s_corpusName = object.getCorpusName();
		    	this.l_documents = object.getDocuments();
		    } finally { in.close(); }
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public String getCorpusName(){
		return s_corpusName;
	}
	
	public int getDocumentCount(){
		return l_documents.size();
	}
	
	public List<Document> getDocuments(){
		return l_documents;
	}
	
	public void addDocument(String fileName, List<DEPTree> trees){
		Document document = new Document(fileName);
		document.addTrees(trees);
		l_documents.add(document);
	}
	
	public void addDocument(String fileName, DEPTree titleTree, List<DEPTree> trees){
		Document document = new Document(fileName, titleTree);
		document.addTrees(trees);
		l_documents.add(document);
	}

	@Override
	public Iterator<Document> iterator() {
		Iterator<Document> it = new Iterator<Document>() {
			int i = 0, size = l_documents.size();
			
			@Override
			public Document next() {
				return l_documents.get(i++);
			}
			
			@Override
			public boolean hasNext() {
				return i < size;
			}
		};
		return it;
	}
	
	@Override
	public String toString(){
		return getCorpusName() + StringConst.COLON + StringConst.TAB + getDocumentCount() + StringConst.SPACE + "documents";
	}
}
