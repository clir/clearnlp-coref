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
package edu.emory.clir.clearnlp.relation.extract;

import java.util.List;

import edu.emory.clir.clearnlp.relation.chunk.AbstractChucker;
import edu.emory.clir.clearnlp.relation.structure.Document;
import edu.emory.clir.clearnlp.relation.structure.Entity;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 14, 2015
 */
public abstract class AbstractMainEntityExtractor {
	protected AbstractChucker d_chunker;
	
	public AbstractMainEntityExtractor(AbstractChucker chunker){
		d_chunker = chunker;
	}
	
	abstract public List<Entity> getNonMainEntities(Document document);
	abstract protected List<Entity> getMainEntities(Document document);
	
	public List<Entity> getMainEntities(Document document, boolean setEntity2Document){
		List<Entity> l_mainEntiies = getMainEntities(document);
		if(setEntity2Document) document.setMainEnities(l_mainEntiies);
		return l_mainEntiies;
	}
}
