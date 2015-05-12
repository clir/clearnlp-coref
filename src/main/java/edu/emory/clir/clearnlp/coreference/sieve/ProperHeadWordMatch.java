package edu.emory.clir.clearnlp.coreference.sieve;

import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.dependency.DEPNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author alexlutz ({@code ajlutz@emory.edu})
 * @version 1.0
 */
public class ProperHeadWordMatch extends AbstractStringMatch
{
    @Override
    protected boolean match(Mention prev, Mention curr)
    {
        Set<String> location = new HashSet<>(Arrays.asList("southern", "northern", "eastern", "western"));

        Set<DEPNode> ancestor = prev.getNode().getAncestorSet();
        return ancestor.stream().anyMatch(x -> x.getWordForm().contains(curr.toString()) && !curr.getNode().isDescendantOf(prev.getNode()) && !x.containsDependent(".*\\d+.*"));
    }
}
