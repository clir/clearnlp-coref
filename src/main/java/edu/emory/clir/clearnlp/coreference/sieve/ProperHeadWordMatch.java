package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.dependency.DEPNode;

/**
 * @author alexlutz
 * @version 1.0
 * need getAncestorList() in order to make sure no mismatches against locations and numbers
 */
public class ProperHeadWordMatch extends AbstractStringMatch
{
	@Override
	protected boolean match(Mention prev, Mention curr)
	{
		Set<String> location = new HashSet<>(Arrays.asList("northern","southern","eastern","western"));
		Set<DEPNode> prevAncestor = prev.getNode().getAncestorSet();
		
		return prevAncestor.stream().anyMatch(x -> x.getWordForm().contains(curr.toString()) && !curr.getNode().isDescendantOf(prev.getNode()));
	}

}
