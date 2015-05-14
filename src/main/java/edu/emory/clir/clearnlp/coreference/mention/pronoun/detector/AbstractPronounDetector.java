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
package edu.emory.clir.clearnlp.coreference.mention.pronoun.detector;

import java.io.Serializable;
import java.util.Map;

import edu.emory.clir.clearnlp.coreference.mention.pronoun.Pronoun;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 11, 2015
 */
public abstract class AbstractPronounDetector implements Serializable{
	private static final long serialVersionUID = -4449253641861306153L;
	
	protected TLanguage language;
	protected Map<String, Pronoun> m_pronouns;
	
	public AbstractPronounDetector(TLanguage l){ 
		language = l; 
		m_pronouns = initDictionary();
	}
	
	abstract protected  Map<String, Pronoun> initDictionary();
	
	public Map<String, Pronoun> getDictionary() {	
		return m_pronouns; 
	}
	
	public boolean isPronoun(String word){
		return m_pronouns.containsKey(word); 
	}
	abstract public boolean isPronoun(DEPTree tree, DEPNode node);
	
	public Pronoun getPronoun(String word){	
		return m_pronouns.get(word); 
	}
	abstract public Pronoun getPronoun(DEPTree tree, DEPNode node);
}
