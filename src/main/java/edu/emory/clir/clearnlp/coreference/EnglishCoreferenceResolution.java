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

import java.util.List;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.collection.set.DisjointSet;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.EnglishMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishCoreferenceResolution extends AbstractCoreferenceResolution
{
	private AbstractMentionDetector m_detector;
	
	public EnglishCoreferenceResolution() throws Exception
	{
		m_detector = new EnglishMentionDetector();
	}
	
	@Override
	public Pair<List<Mention>,DisjointSet> getEntities(List<DEPTree> trees)
	{
		List<Mention> mentions = m_detector.getMentionList(trees);
		DisjointSet set = new DisjointSet(mentions.size());
		int i, j, size = mentions.size();
		Mention curr, prev;
		
		for (i=1; i<size; i++)
		{
			curr = mentions.get(i);
			
			for (j=i-1; j>=0; j--)
			{
				prev = mentions.get(j);
				
				if (matchesPerson(curr, prev) || matchesPronoun(curr, prev) || matchesCommonNoun(curr, prev) || matchesWildcardPronoun(curr, prev))
				{
					set.union(i, j);
					break;
				}
			}
		}
		
		return new Pair<List<Mention>, DisjointSet>(mentions, set);
	}
	
	private boolean matchesPerson(Mention curr, Mention prev)
	{
		return (curr.isEntityType(EntityType.PERSON_FEMALE) && prev.isEntityType(EntityType.PERSON_FEMALE)) ||
			   (curr.isEntityType(EntityType.PERSON_MALE)   && prev.isEntityType(EntityType.PERSON_MALE));
	}
	
	private boolean matchesPronoun(Mention curr, Mention prev)
	{
		return (curr.isEntityType(EntityType.PRONOUN_FEMALE) && (prev.isEntityType(EntityType.PRONOUN_FEMALE) || prev.isEntityType(EntityType.PERSON_FEMALE))) ||
			   (curr.isEntityType(EntityType.PRONOUN_MALE)   && (prev.isEntityType(EntityType.PRONOUN_MALE)   || prev.isEntityType(EntityType.PERSON_MALE)));
	}
	
	private boolean matchesCommonNoun(Mention curr, Mention prev)
	{
		// we need to deal with common nouns
		return false;
	}
	
	private boolean matchesWildcardPronoun(Mention curr, Mention prev)
	{
		// Yet to be implemented
		return false;
	}
}
