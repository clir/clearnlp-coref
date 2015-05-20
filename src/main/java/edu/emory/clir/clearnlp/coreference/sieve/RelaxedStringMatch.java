package edu.emory.clir.clearnlp.coreference.sieve;

import edu.emory.clir.clearnlp.coreference.mention.SingleMention;

public class RelaxedStringMatch extends AbstractStringMatch{
	@Override
	protected boolean match(SingleMention prev, SingleMention curr){
		return prev.getNode().isWordForm(curr.getNode().getWordForm());
	}
}
