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
package edu.emory.clir.clearnlp.coreference.coref;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.collection.set.DisjointSet;
import edu.emory.clir.clearnlp.coreference.AbstractCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.SieveSystemCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Apr 13, 2015
 */
public class SieveSystemTest {
	
	@Test
	public void corefTest() throws IOException{
		AbstractCoreferenceResolution coref = new SieveSystemCoreferenceResolution();
		InputStream in = new FileInputStream("src/test/resources/edu/emory/clir/clearnlp/coreference/mention/input.mention.cnlp");
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		reader.open(in);
		
		DEPTree tree;
		List<DEPTree> trees = new ArrayList<>();
		
		while ((tree = reader.next()) != null) trees.add(tree);

		Pair<List<Mention>, DisjointSet> resolution = coref.getEntities(trees);
		System.out.println(resolution);
	}
}
