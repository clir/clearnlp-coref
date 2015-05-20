package edu.emory.clir.clearnlp.coreference.sieve;

import edu.emory.clir.clearnlp.coreference.mention.SingleMention;
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
	
	protected boolean match(SingleMention prev, SingleMention curr){
		return prev.getNode().isWordForm(curr.getNode().getWordForm());
	}
}
