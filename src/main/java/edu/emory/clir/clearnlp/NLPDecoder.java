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
package edu.emory.clir.clearnlp;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.component.AbstractComponent;
import edu.emory.clir.clearnlp.component.mode.dep.DEPConfiguration;
import edu.emory.clir.clearnlp.component.utils.GlobalLexica;
import edu.emory.clir.clearnlp.component.utils.NLPUtils;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.tokenization.AbstractTokenizer;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 24, 2015
 */
public class NLPDecoder {
	private AbstractComponent[] components;
	private AbstractTokenizer   tokenizer;
	
	public NLPDecoder(TLanguage language)
	{
		tokenizer  = NLPUtils.getTokenizer(language);
		components = getGeneralModels(language);
	}
	
	public AbstractComponent[] getGeneralModels(TLanguage language)
	{
		// initialize global lexicons
		List<String> paths = new ArrayList<>();
		paths.add("brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1.txt.xz");
		
		GlobalLexica.initDistributionalSemanticsWords(paths);
		GlobalLexica.initNamedEntityDictionary("general-en-ner-gazetteer.xz");
		
		// initialize statistical models
		AbstractComponent morph = NLPUtils.getMPAnalyzer(language);
		AbstractComponent pos = NLPUtils.getPOSTagger   (language, "general-en-pos.xz");
		AbstractComponent dep = NLPUtils.getDEPParser   (language, "general-en-dep.xz", new DEPConfiguration("root"));
		AbstractComponent ner = NLPUtils.getNERecognizer(language, "general-en-ner.xz");
		
		return new AbstractComponent[]{pos, morph, dep, ner};
	}
	
	public DEPTree toDEPTree(String line){
		List<String> tokens = tokenizer.tokenize(line);
		DEPTree tree = new DEPTree(tokens);
		
		for (AbstractComponent component : components)
			component.process(tree);
		
		return tree;
	}
	
	public DEPTree toDEPTree(List<String> tokens){
		DEPTree tree = new DEPTree(tokens);
		
		for (AbstractComponent component : components)
			component.process(tree);
		
		return tree;
	}
	
	public List<DEPTree> toDEPTrees(String line){
		List<DEPTree> trees = new ArrayList<>();
		List<List<String>> sentences = tokenizer.segmentize(new ByteArrayInputStream(line.getBytes()));
		
		for(List<String> tokens : sentences)
			trees.add(toDEPTree(tokens));
		
		return trees;
	}
}
