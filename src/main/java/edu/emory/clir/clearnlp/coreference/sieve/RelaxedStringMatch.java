package edu.emory.clir.clearnlp.coreference.sieve;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
//improve this

public class RelaxedStringMatch extends AbstractStringMatch{
	public RelaxedStringMatch(){
		super();
	}
	
	public RelaxedStringMatch(boolean decapitalize){
		super(decapitalize);
	}
	
	@Override
	protected String getWordSequence(AbstractMention mention){
		return mention.getWordFrom();
	}
}
