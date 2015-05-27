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
package edu.emory.clir.clearnlp.coreference.structure;

import org.junit.Test;

import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSet;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 27, 2015
 */
public class DisjointSetTest {
	@Test
	public void main(){
		
		DisjointSet set = new DisjointSet(10, false);
		
		System.out.println(set.union(1, 3));
		System.out.println(set.union(3, 1));
	}
}
