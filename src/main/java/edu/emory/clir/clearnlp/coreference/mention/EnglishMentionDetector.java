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
package edu.emory.clir.clearnlp.coreference.mention;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.emory.clir.clearnlp.collection.ngram.Unigram;
import edu.emory.clir.clearnlp.constituent.CTLibEn;
import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.coreference.type.NumberType;
import edu.emory.clir.clearnlp.coreference.wildcard_Pronoun.WildcardPronoun_Identifier;
import edu.emory.clir.clearnlp.dependency.DEPLibEn;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.dictionary.PathNamedEntity;
import edu.emory.clir.clearnlp.pos.POSLibEn;
import edu.emory.clir.clearnlp.util.IOUtils;
import utils.Demonym_DictReader;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishMentionDetector extends AbstractMentionDetector
{
	static private final Set<String> S_FEMALE_PRONOUN	= new HashSet<>(Arrays.asList("she","her","hers","herself"));
	static private final Set<String> S_MALE_PRONOUN		= new HashSet<>(Arrays.asList("he","him","his","himself"));
	static private final Set<String> S_SINGULAR_PRONOUN	= new HashSet<>(Arrays.asList("it","its","itself"));
	static private final Set<String> S_PLURAL_PRONOUN	= new HashSet<>(Arrays.asList("they","them","their","theirs","themselves"));

	private WildcardPronoun_Identifier WILDCARD_PRONOUN_IDENTIFIER;
	
	private Unigram<String> m_femaleNames;
	private Unigram<String> m_maleNames;
	Set<String> s_mentionLabels;
	
	public EnglishMentionDetector() throws IOException
	{
		m_femaleNames = new Unigram<>();
		m_maleNames = new Unigram<>();
		WILDCARD_PRONOUN_IDENTIFIER = new WildcardPronoun_Identifier("/Users/HenryChen/Dropbox/Developement/ClearNLP-QA/dictionary/WildcardPronoun.txt");
		
		addFemaleNames(IOUtils.getInputStreamsFromClasspath(PathNamedEntity.US_FEMALE_NAMES));
		addMaleNames  (IOUtils.getInputStreamsFromClasspath(PathNamedEntity.US_MALE_NAMES));
		s_mentionLabels = initMentionLabels();
	}
	
//	====================================== LEXICA ======================================

	public void addFemaleNames(InputStream in)
	{
		addDictionary(in, m_femaleNames);
	}
	
	public void addMaleNames(InputStream in)
	{
		addDictionary(in, m_maleNames);
	}
	
	private Set<String> initMentionLabels()
	{
		Set<String> set = new HashSet<>();
		
		set.add(DEPLibEn.DEP_NSUBJ);
		set.add(DEPLibEn.DEP_NSUBJPASS);
		set.add(DEPLibEn.DEP_AGENT);
		set.add(DEPLibEn.DEP_DOBJ);
		set.add(DEPLibEn.DEP_IOBJ);
		set.add(DEPLibEn.DEP_POBJ);
		
		return set;
	}
	
//	====================================== MENTION TYPE ======================================
	
	@Override
	public Mention getMention(DEPTree tree, DEPNode node)
	{
		Mention mention;

		//might want to change in order to make SpeakerIdentification easier
		//String POSTag = node.getPOSTag();
		//if (POSLibEn.isCommonOrProperNoun(POSTag) || POSLibEn.isPronoun(POSTag) || POSLibEn.isPunctuation(POSTag)) {
		//would need to incorporate setting the types for pronouns as done below
		if ((mention = getPronounMention(tree, node)) != null)				return mention;
		if ((mention = getWildcarPronounMention(tree, node)) != null)		return mention;
		if ((mention = getPersonMention(tree, node)) != null)				return mention;
		if ((mention = getNounMentions(tree, node)) != null)				return mention;
		return null;
	}
	
	public Mention getPronounMention(DEPTree tree, DEPNode node)
	{
		if (node.isPOSTag(CTLibEn.POS_PRP) || node.isPOSTag(CTLibEn.POS_PRPS))	//why not replace with POSLibEn.isPronoun(node.getPOSTag());
		{
			Mention mention = new Mention(tree, node);
			
			if (S_FEMALE_PRONOUN.contains(node.getLemma()))
			{
				mention.setEntityType(EntityType.PRONOUN_FEMALE);
				mention.setNumberType(NumberType.SINGULAR);
			}
			else if (S_MALE_PRONOUN.contains(node.getLemma()))
			{
				mention.setEntityType(EntityType.PRONOUN_MALE);
				mention.setNumberType(NumberType.SINGULAR);
			}
			else if (S_SINGULAR_PRONOUN.contains(node.getLemma()))
			{
				mention.setEntityType(EntityType.PRONOUN_NEUTRAL);
				mention.setNumberType(NumberType.SINGULAR);
			}
			else if (S_PLURAL_PRONOUN.contains(node.getLemma()))
			{
				mention.setEntityType(EntityType.PRONOUN_NEUTRAL);
				mention.setNumberType(NumberType.PLURAL);
			}

			return mention;
		}
		
		return null;
	}

	public Mention getNounMentions(DEPTree tree, DEPNode node)
	{
		if (POSLibEn.isCommonOrProperNoun(node.getPOSTag())) return new Mention(tree, node);

		return null;
	}

	public Mention getWildcarPronounMention(DEPTree tree, DEPNode node){
		return WILDCARD_PRONOUN_IDENTIFIER.getMention(tree, node);
	}
	
	public Mention getPersonMention(DEPTree tree, DEPNode node)
	{
		double f = m_femaleNames.getProbability(node.getLemma());
		double m = m_maleNames  .getProbability(node.getLemma());
		if (f == 0 && m == 0) return null;
		
		Mention mention = new Mention(tree, node, NumberType.SINGULAR);
		if (f > m)	mention.setEntityType(EntityType.PERSON_FEMALE);
		else		mention.setEntityType(EntityType.PERSON_MALE);
		
		return mention;
	}
}
