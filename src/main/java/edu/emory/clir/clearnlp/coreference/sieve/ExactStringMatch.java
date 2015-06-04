package edu.emory.clir.clearnlp.coreference.sieve;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;

/**
 * @author alexlutz
 */
public class ExactStringMatch extends AbstractStringMatch {
	public ExactStringMatch(){
		super();
	}
	
	public ExactStringMatch(boolean decapitalize){
		super(decapitalize);
	}
	
	@Override
	protected String getWordSequence(AbstractMention mention){
		return mention.getSubTreeWordSequence();
	}
}
