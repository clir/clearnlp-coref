package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.HashSet;
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
		Set<DEPNode> currAncestor = curr.getNode().getAncestorSet();
		return (prevAncestor.size() > currAncestor.size()) ? anyMatch(prevAncestor, currAncestor) : anyMatch(currAncestor, prevAncestor);
	}
	
	private boolean anyMatch(Set<DEPNode> max, Set<DEPNode> min)
	{
		Set<String> mx = convert(max);
		Set<String> mn = convert(min);
		for (String word : mx) {
			if (mn.contains(word))
				return true;
		}
		return false;
	}
	
	private Set<String> convert(Set<DEPNode> set)
	{
		Set<String> result = new HashSet<>();
		for(DEPNode node : set) {
			result.add(node.getWordForm());
		}
		return result;
	}
}
