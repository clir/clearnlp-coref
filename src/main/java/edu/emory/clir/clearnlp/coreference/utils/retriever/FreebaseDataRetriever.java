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
package edu.emory.clir.clearnlp.coreference.utils.retriever;

import edu.emory.clir.clearnlp.coreference.utils.structures.ParameterPair;


/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Apr 19, 2015
 */
public class FreebaseDataRetriever extends AbstractRestAPIDataRetriever {
	
	public FreebaseDataRetriever(String url){
		super(url);
	}
	
	public static void main(String[] args) throws Exception{
		AbstractRestAPIDataRetriever retriever = new FreebaseDataRetriever("https://www.googleapis.com/freebase/v1/topic/en/");
		String result = retriever.REST_GetRequest("barack_obama", new ParameterPair("filter", "/people"));
		
		System.out.println(result);
	}
}
