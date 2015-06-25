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
package edu.emory.clir.clearnlp.coreference.dictionary;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 25, 2015
 */
public interface PathDictionary {
	final String 	ROOT = "src/main/resources/edu/emory/clir/clearnlp/dictionary/";
	
	final String 	MENTION = ROOT + "coreference/mention/",
					SIEVE = ROOT + "/coreference/sieve/",
					WORDNET = ROOT + "wordnet/";

	// Mentions
	final String 	ENG_PRONOUN = MENTION + "english_pronouns.txt",
					ENG_COMMON_NOUN = MENTION + "english_common_nouns.txt";
	
	// Sieves
	final String 	ENG_DEMONYM = SIEVE + "english_demonym.txt",
		    		REPORT_VERBS = SIEVE + "english_reportVerbs.txt",
		    		ENG_STOPWORDS = SIEVE + "english_stopwords.txt",
		    		COGNITIVE_NOUNS = SIEVE + "cognitive_nouns.txt",
		    		COGNITIVE_VERBS = SIEVE + "cognitive_verbs.txt";
	
	// WordNet
	final String ENG_WORDNETNOUNS = WORDNET + "eng_wordnet_nouns.xz";
}
