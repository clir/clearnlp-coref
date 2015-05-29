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
package edu.emory.clir.clearnlp.coreference.path;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 21, 2015
 */
public interface PathData {
	String 	ROOT = "src/test/resources/edu/emory/clir/clearnlp/coreference/",
			COREF = ROOT + "coref/",
			MENTION = ROOT + "mention/";
	
	String 	ENG_COREF_ADJUNTION = COREF + "input.coref.adjunction.cnlp",
			ENG_COREF_MICROSOFT_RAW = COREF + "microsoft/mc500.dev.tsv",
			ENG_COREF_MICROSOFT_PARSED_DIR = COREF + "microsoft/parsed/";
	
	String 	ENG_MENTION = MENTION + "input.mention.cnlp",
			ENG_MENTION_QUOTE = MENTION + "input.mention.quote.cnlp",
			ENG_MENTION_CONJUNCTION = MENTION + "input.menton.conjunctions.cnlp";
}
