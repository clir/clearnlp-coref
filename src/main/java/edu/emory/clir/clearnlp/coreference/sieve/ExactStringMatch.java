package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.StringJoiner;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.mention.SingleMention;
import edu.emory.clir.clearnlp.dependency.DEPNode;
/**
 * 
 * @author alexlutz
 * this will be the first sieve that performs exact string matching between mentions
 * need to get getCompound() method from Jinho to incorporate
 */
public class ExactStringMatch extends AbstractStringMatch {
	public ExactStringMatch()
	{
		super();
	}
	
	public ExactStringMatch(boolean decapitalize)
	{
		super(decapitalize);
	}

	protected boolean match(AbstractMention prev, AbstractMention curr){
		String prevWords = prev.getSubTreeWordSequence();
		String currWords = curr.getSubTreeWordSequence();
		return prevWords.equals(currWords);
	}

	protected String getWordSequence(AbstractMention Mention)
	{
		return null;
	}
}
