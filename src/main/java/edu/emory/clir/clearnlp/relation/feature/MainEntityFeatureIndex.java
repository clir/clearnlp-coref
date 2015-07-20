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
package edu.emory.clir.clearnlp.relation.feature;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 6, 2015
 */
public interface MainEntityFeatureIndex {
	
	// For deterministic main entity extractor
	int FREQUENCY_COUNT = 0,
		ENTITY_CONFIDENCE = 1,
		FIRST_APPEARENCE_SENTENCE_ID = 2;
	
	// For unsupervised LR model
	int SentencePOSTags = 0,
		SentenceDEPLables = 1,
		AliasHeadNodeId = 2,
		AliasChunkPOSTags = 3,
		AliasChunkDEPLabels = 4,
		AliasChunkNERTags = 5,
		FirstAppearSentenceId = 6,
		EntityFrequencyCount = 7,
		EntityTag = 8,
		AliasChunkModifiers = 9,
		AliasHeadSubNodeIgnoredDEPLabels = 10,
		AliasWordForm = 11,
		AliasStrippedWordForm = 12,
		AliasSentenceId = 13,
		DocumentInfo = 14,
		EntityId = 15,
		DocumentTiltleTokenMatch = 16,
		SentenceNERTags = 17,
		DocumentTiltleTokens = 18,
		AliasHeadNodeRank = 19;
}
