package edu.emory.clir.clearnlp.coreference.mention.wildcardPronoun;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import edu.emory.clir.clearnlp.constituent.CTLibEn;
import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.coreference.type.WildcardPronounType;
import edu.emory.clir.clearnlp.coreference.utils.WildcardPronoun_DictReader;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.pos.POSLibEn;

public class WildcardPronoun_Identifier {
	private Set<String> S_Wildcard_Pronouns;
	private Map<String, WildcardPronounType> M_WildcardPronounTypes;
	
	public WildcardPronoun_Identifier(String dictionaryPath) throws IOException{
		WildcardPronoun_DictReader reader = new WildcardPronoun_DictReader(dictionaryPath);
		S_Wildcard_Pronouns = reader.getStringSet();
		M_WildcardPronounTypes = reader.getPronounMap();
		reader = null;
	}
	
	public Mention getMention(DEPTree tree, DEPNode node){
		if(!isWildcardPronoun(tree, node)) return null;	
		Mention mention = new Mention(tree, node);
		
		mention.setEntityType(EntityType.PRONOUN_WILDCARD);
		mention.setPronounType(getPronounType(node));
		
		return mention;
	}
	
	public boolean isWildcardPronoun(DEPTree tree, DEPNode node){
		//If the node(Lemma) does not exist in the dictionary
		if(!S_Wildcard_Pronouns.contains(node.getLemma()) && !node.isPOSTag(CTLibEn.POS_CD)) return false;
		
		switch (getPronounType(node)) {
			case RELATIVE:
				break;
			case QUANMOD:
				//#'s cases (No Dependents, Head != NN/NNS/NNP/NNPS)
				if(node.isPOSTag(CTLibEn.POS_CD))
					if(!node.getDependentList().isEmpty() || POSLibEn.isNoun(node.getHead().getPOSTag()))
						return false;
			
				break;
			case ABSTRACTQUAN:
				//if the node does not have the POSTag of DT, NN, NNS, NNP, NNPS 
				if(!node.isPOSTag(CTLibEn.POS_DT) || !POSLibEn.isNoun(node.getPOSTag()))
					return false;
				
				break;
			case UNIT_LIKE:
				break;
			case INCLUSIVE_STAT:
				break;
			case UNKNOWN:
				break;
		}
		
		return true;
	}
	
	public WildcardPronounType getPronounType(String s){
		return M_WildcardPronounTypes.computeIfAbsent(s, k -> WildcardPronounType.UNKNOWN);
	}
	public WildcardPronounType getPronounType(DEPNode node){
		//If node is a number, return QUANMOD
		if(node.isPOSTag(CTLibEn.POS_CD)) return WildcardPronounType.QUANMOD;
		
		return getPronounType(node.getLemma());
	}
}
