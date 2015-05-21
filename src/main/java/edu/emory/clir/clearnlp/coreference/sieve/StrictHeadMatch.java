package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.Set;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;

/**
 * @author alexlutz
 * @version 1.0
 */
public class StrictHeadMatch extends AbstractStringMatch
{
	@Override
	protected boolean match(AbstractMention prev, AbstractMention curr)
	{
		Set<String> prevAncestor = prev.getAncestorWords(), currAncestor = curr.getAncestorWords();
		return (prevAncestor.size() > currAncestor.size()) ? anyMatch(prevAncestor, currAncestor) : anyMatch(currAncestor, prevAncestor);
	}
	
	private boolean anyMatch(Set<String> max, Set<String> min)
	{
		for (String word : max)
			if (min.contains(word))	return true;
		return false;
	}
	
	@Override
	protected String getWordSequence(AbstractMention mention)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
