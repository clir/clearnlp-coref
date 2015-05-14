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
package edu.emory.clir.clearnlp.coreference.mention.common.detector;

import java.io.Serializable;
import java.util.Map;

import edu.emory.clir.clearnlp.coreference.mention.common.CommonNoun;
import edu.emory.clir.clearnlp.coreference.mention.pronoun.Pronoun;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 12, 2015
 */
public abstract class AbstractCommonNounDetector implements Serializable{
	private static final long serialVersionUID = -4535842837728435354L;

	protected TLanguage language;
	protected Map<String, CommonNoun> m_common_nouns;
	
	public AbstractCommonNounDetector(TLanguage l){ 
		language = l;
		m_common_nouns = initDictionary();
	}

	abstract protected  Map<String, CommonNoun> initDictionary();
	
	abstract public boolean isCommonNoun(DEPTree tree, DEPNode node);
	
	abstract public CommonNoun getCommonNoun(DEPTree tree, DEPNode node);
}
