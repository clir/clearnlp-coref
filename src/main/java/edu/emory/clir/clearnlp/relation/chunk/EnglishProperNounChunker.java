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
package edu.emory.clir.clearnlp.relation.chunk;

import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.dependency.DEPLibEn;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.pos.POSLibEn;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 7, 2015
 */
public class EnglishProperNounChunker extends AbstractChucker {

	public EnglishProperNounChunker(){
		super(TLanguage.ENGLISH);
	}

	@Override
	public List<List<DEPNode>> getChunk(DEPTree tree) {
		List<List<DEPNode>> list = new ArrayList<>();
		
		for(DEPNode node : tree)
			if(node.getPOSTag().startsWith(POSLibEn.POS_NNP) && !node.isLabel(DEPLibEn.DEP_COMPOUND))
				list.add(node.getSubNodeList());
		return list;
	}
	
	
}
