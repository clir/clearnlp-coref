package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.Set;

import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.dependency.DEPNode;

/**
 * @author alexlutz
 * @version 1.0
 */
public class StrictHeadMatch extends AbstractStringMatch
{
	@Override
	protected boolean match(Mention prev, Mention curr)
	{
		Set<DEPNode> prevAncestor = prev.getNode().getAncestorSet();
		return prevAncestor.stream().anyMatch(x -> x.getWordForm().equals(curr.toString()) && !curr.getNode().isDescendantOf(prev.getNode()));
	}

}
