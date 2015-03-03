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
package edu.emory.clir.clearnlp.coreference;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.collection.set.DisjointSet;
import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CoreferenceTest
{
	@Test
	public void demo() throws Exception
	{
		TSVReader reader = new TSVReader(0, 1, 2, 3, 4, 5, 6, 7);
		reader.open(new FileInputStream("src/test/resources/arith-qs.ak.cnlp"));
		List<DEPTree> trees = new ArrayList<>();
		DEPTree tree;
		
		while ((tree = reader.next()) != null)
			trees.add(tree);
		
		AbstractCoreferenceResolution coref = new EnglishCoreferenceResolution();
		Pair<List<Mention>,DisjointSet> entities = coref.getEntities(trees);
		List<Mention> mentions = entities.o1;
		DisjointSet set = entities.o2;
		
		for (int i=0; i<mentions.size(); i++)
			System.out.println(i+": "+mentions.get(i).getNode().getWordForm());
		
		for (int i=0; i<mentions.size()-1; i++)
			for (int j=i+1; j<mentions.size(); j++)
				System.out.println("("+i+","+j+") "+set.inSameSet(i, j));
	}
}
