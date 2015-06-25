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
package edu.emory.clir.clearnlp.coreference.type;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 11, 2015
 */
public interface FeatureType {
	final int 
		ExactStringMatch = 0,
		RelaxedString = 1,
		SentenceOffset = 2,
		TokenOffset = 3,
		CurrentPOSTag = 4,
		GenderMatch = 5,
		NumberMatch = 6,
		EntityMatch = 7,
		SpeakerStatus = 8,
		CurrentDEPLabel = 9,
		PronounMatch = 10,
		TokenSentencePos = 11,
		WordFormMatch = 12,
		MultiMentionSizes = 13,
		HeadNodePOSTag = 14,
		PreciseConstuctMatch = 16,
		NEPronounPair = 17,
		PronounType = 18,
		MultiMentionWordForm = 19,
		AppositionMatch = 20,
		PredicateNomMatch = 21,
		AcronymMatch = 22,
		MultiMentionIndex = 23,
		SynonymMatch = 24,
		AntonymMatch = 25,
		HypernymMatch = 26,
		Boolean = 27,
		SubTreePOSTag1 = 29,
		SubTreePOSTag2 = 30,
		SubTreeDEPlabel1 = 31,
		SubTreeDEPlabel2 = 32,
		Attributes1 = 33,
		Attributes2 = 34,
		SubTreeTokens1 = 35,
		SubTreeTokens2 = 36,
		XPronounPair = 37,
		SubTreeTokenOverlap = 38,
		ModifierSet1 = 39,
		ModifierSet2 = 40;
	
	final String 
		TRUE = "true",
		FALSE = "false",
		NULL = "null";
	
	final String 
		HasQuote1 = "Quote1",
		HasQuote2 = "Quote2";
}
