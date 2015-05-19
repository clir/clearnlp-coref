package edu.emory.clir.clearnlp.coreference.sieve;

import edu.emory.clir.clearnlp.coreference.mention.Mention;
//improve this
public class RelaxedStringMatch extends AbstractStringMatch{
	public RelaxedStringMatch()
	{
		super();
	}
	
	public RelaxedStringMatch(boolean decapitalize)
	{
		super(decapitalize);
	}
	
	@Override
	protected String getWordSequence(Mention mention){
		return mention.getNode().getWordForm();
	}
}
