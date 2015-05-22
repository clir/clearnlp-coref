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
package edu.emory.clir.clearnlp.coreference.coref.sieve;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.collection.set.DisjointSet;
import edu.emory.clir.clearnlp.coreference.AbstractCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.SieveSystemCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.config.CorefCongiuration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.path.PathData;
import edu.emory.clir.clearnlp.coreference.sieve.PreciseConstructMatch;
import edu.emory.clir.clearnlp.coreference.utils.CoreferenceTestUtil;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	May 21, 2015
 */
public class PronounMatchTest {
	/* Argument infomation */
	final public static Pattern verb = Pattern.compile("VB[D||P||Z]{0,1}");
	final public static List<String> argumentSlot = new ArrayList<>(Arrays.asList(DEPTagEn.DEP_SUBJ, DEPTagEn.DEP_AGENT, DEPTagEn.DEP_DOBJ, DEPTagEn.DEP_IOBJ, DEPTagEn.DEP_POBJ));
	
	public static int getArgumentHierarchy(DEPNode node){
		return argumentSlot.indexOf(node.getLabel());
	}
	
	@Test
	public void testAdjunctDomain(){
		/* Configuration */
		CorefCongiuration config = new CorefCongiuration();
		config.mountSieves(new PreciseConstructMatch());
		/* ************* */
		
		AbstractCoreferenceResolution coref = new SieveSystemCoreferenceResolution(config);
		List<DEPTree> trees = CoreferenceTestUtil.getTestDocuments(PathData.ENG_COREF_ADJUNTION);
		
		List<AbstractMention> mentions = coref.getMentions(trees);	
		Pair<List<AbstractMention>, DisjointSet> resolution = coref.getEntities(trees);
		
		for(AbstractMention mention : mentions)
			System.out.println(mention);
		
		
		AbstractMention prev = mentions.get(0), curr = mentions.get(1);
		System.out.println("\n" + prev + "\n" + curr);
		System.out.println(curr.isInAdjunctDomainOf(prev));
	}
	
	private static boolean argumentDomain(DEPNode head, DEPNode prev, DEPNode curr){
		return head != null && prev.getHead() == head && curr.getHead() == head && getArgumentHierarchy(prev) > getArgumentHierarchy(curr);
	}
}
