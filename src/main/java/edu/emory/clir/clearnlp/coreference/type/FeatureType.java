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
	int ExactString = 0,
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
		IsParentRel = 11;
	
	String TRUE = "true",
		   FALSE = "false";
}
