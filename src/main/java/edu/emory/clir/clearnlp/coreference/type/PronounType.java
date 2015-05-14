package edu.emory.clir.clearnlp.coreference.type;

public enum PronounType {
	REGULAR,
	RELATIVE,
	QUANMOD,
	ABSTRACTQUAN,
	UNIT_LIKE,
	INCLUSIVE_STAT,
	UNKNOWN;
	
	public static PronounType toPronounType(String s){
		for(PronounType type : PronounType.values()){
			if(type.toString().equals(s))
				return type;
		}
		return null;
	}
}
