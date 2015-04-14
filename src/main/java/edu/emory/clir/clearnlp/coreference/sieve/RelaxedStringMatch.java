package edu.emory.clir.clearnlp.coreference.sieve;

import edu.emory.clir.clearnlp.coreference.mention.Mention;

public class RelaxedStringMatch extends AbstractStringMatch{
	@Override
	protected boolean match(Mention prev, Mention curr){
		return prev.getNode().isWordForm(curr.getNode().getWordForm());
	}
}
