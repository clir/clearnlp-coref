package edu.emory.clir.clearnlp.coreference.sieve;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
/**
 * 
 * @author alexlutz
 * this will be the first sieve that performs exact string matching between mentions
 * need to get getCompound() method from Jinho to incorporate
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
