package edu.emory.clir.clearnlp.coreference.type;

public enum WildcardPronounType {
	RELATIVE,
	QUANMOD,
	ABSTRACTQUAN,
	UNIT_LIKE,
	INCLUSIVE_STAT,
	UNKNOWN;
	
	public static WildcardPronounType toPronounType(String s){
		for(WildcardPronounType type : WildcardPronounType.values()){
			if(type.toString().equals(s))
				return type;
		}
		return null;
	}
}
