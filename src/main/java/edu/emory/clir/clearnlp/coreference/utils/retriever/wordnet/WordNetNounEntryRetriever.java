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
package edu.emory.clir.clearnlp.coreference.utils.retriever.wordnet;

import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.emory.clir.clearnlp.coreference.type.WordNetFeatureType;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 24, 2015
 */
public class WordNetNounEntryRetriever implements WordNetFeatureType {
	private int defaultSynonymRank = 2;
	private int defaultAntonymRank = 1;
	private int defaultHypernymRank = 3;
	
	private WordNetDatabase db;
	private Map<Integer, Map<String, Set<String>>> db_serialized;
	
	public WordNetNounEntryRetriever(){
		System.setProperty("wordnet.database.dir", "src/main/resources/edu/emory/clir/clearnlp/dictionary/wordnet/WordNet-3.0/dict");
		db = WordNetDatabase.getFileInstance();
	}
	
	public WordNetNounEntryRetriever(String in){
		initModel(IOUtils.createObjectXZBufferedInputStream(in));
	}
	
	public WordNetNounEntryRetriever(ObjectInputStream in){
		initModel(IOUtils.createObjectXZBufferedInputStream(in));
	}
	
	@SuppressWarnings("unchecked")
	private void initModel(ObjectInputStream in){
		try {
			db_serialized = (Map<Integer, Map<String, Set<String>>>) in.readObject();
			in.close();
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	// Synonyms
	public Set<String> getSynonyms(String wordForm){
		if(db_serialized == null)
			return getSynonyms(wordForm, defaultSynonymRank);
		return db_serialized.get(Synonym).getOrDefault(wordForm, new HashSet<>());
	}
	
	public Set<String> getSynonyms(String wordForm, int rank){
		if(db == null) throw new IllegalArgumentException("WordNetDatabase is not initialized.");
		Set<String> set = new HashSet<>();
		getSynonyms_Aux(set, wordForm, rank);
		return set;			
	}
	
	private void getSynonyms_Aux(Set<String> set, String wordForm, int rank){
		if(rank <= 0)	return;
		
		Synset[] synsets = db.getSynsets(wordForm, SynsetType.NOUN);
		
		for(Synset synset : synsets){
			for(String synsetWordForm : synset.getWordForms()){
				set.add(synsetWordForm);
				getHypernyms(synsetWordForm, rank-1);
			}
		}
	}
	
	public boolean hasSameSynonym(String wordForm1, String wordForm2){
		return DSUtils.hasIntersection(getSynonyms(wordForm1), getSynonyms(wordForm2));
	}
	
	public boolean hasSameSynonym(String wordForm1, String wordForm2, int rank){
		if(db == null) throw new IllegalArgumentException("WordNetDatabase is not initialized.");
		return DSUtils.hasIntersection(getSynonyms(wordForm1, rank), getSynonyms(wordForm2, rank));
	}
	
	// Antonyms
	public Set<String> getAntonyms(String wordForm){
		if(db_serialized == null)
			return getAntonyms(wordForm, defaultAntonymRank);
		return db_serialized.get(Antonym).getOrDefault(wordForm, new HashSet<>());
	}
	
	public Set<String> getAntonyms(String wordForm, int rank){
		if(db == null) throw new IllegalArgumentException("WordNetDatabase is not initialized.");
		Set<String> set = new HashSet<>();
		getAntonyms_Aux(set, wordForm, rank);
		return set;
	}
	
	private void getAntonyms_Aux(Set<String> set, String wordForm, int rank){
		if(rank <= 0)	return;
		
		WordSense[] antonymWordSenses;
		Synset[] synsets = db.getSynsets(wordForm, SynsetType.NOUN);
		
		for(Synset synset : synsets){
			antonymWordSenses = ((NounSynset)synset).getAntonyms(wordForm);
			for(WordSense antonymWordSense : antonymWordSenses) {
	            set.add(antonymWordSense.getWordForm());
	            getSynonyms_Aux(set, wordForm, rank-1);
	        }
		}
	}
	
	public boolean isAntonyms(String wordForm1, String wordForm2){
		return getAntonyms(wordForm1).contains(wordForm2) || getAntonyms(wordForm2).contains(wordForm1);
	}
	
	public boolean isAntonyms(String wordForm1, String wordForm2, int rank){
		if(db == null) throw new IllegalArgumentException("WordNetDatabase is not initialized.");
		return getAntonyms(wordForm1, rank).contains(wordForm2) || getAntonyms(wordForm2, rank).contains(wordForm1);
	}
	
	// Hypernyms
	public Set<String> getHypernyms(String wordForm){
		if(db_serialized == null)
			return getHypernyms(wordForm, defaultHypernymRank);
		return db_serialized.get(Hypernym).getOrDefault(wordForm, new HashSet<>());
	}
	
	public Set<String> getHypernyms(String wordForm, int rank){
		if(db == null) throw new IllegalArgumentException("WordNetDatabase is not initialized.");
		Set<String> set = new HashSet<>();
		getHypernyms_Aux(set, wordForm, rank);
		return set;
	}
	
	private void getHypernyms_Aux(Set<String> set, String wordForm, int rank){
		if(rank <= 0)	return;
		
		NounSynset[] hypernymSynsets;
		Synset[] synsets = db.getSynsets(wordForm, SynsetType.NOUN);
		
		for(Synset synset : synsets){
			hypernymSynsets = ((NounSynset)synset).getHypernyms();
			for(NounSynset hypernymSynset : hypernymSynsets)
				for(String hypernymWordForm : hypernymSynset.getWordForms()){
					set.add(hypernymWordForm);
					getHypernyms_Aux(set, hypernymWordForm, rank-1);
				}
		}
	}
	
	public boolean hasSameHypernym(String wordFrom1, String wordForm2){
		Set<String> set1 = getHypernyms(wordFrom1), set2 = getHypernyms(wordForm2);
		set1.add(wordFrom1); set2.add(wordForm2);
		return DSUtils.hasIntersection(set1, set2);
	}
	
	public boolean hasSameHypernym(String wordFrom1, String wordForm2, int rank){
		if(db == null) throw new IllegalArgumentException("WordNetDatabase is not initialized.");
		Set<String> set1 = getHypernyms(wordFrom1, rank), set2 = getHypernyms(wordForm2, rank);
		set1.add(wordFrom1); set2.add(wordForm2);
		return DSUtils.hasIntersection(set1, set2);
	}
}
