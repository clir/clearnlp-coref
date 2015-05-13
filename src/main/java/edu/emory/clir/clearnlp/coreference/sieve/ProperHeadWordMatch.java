package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.dependency.DEPNode;

/**
 * @author alexlutz ({@code ajlutz@emory.edu})
 * @version 1.0
 * need to getDependent(Pattern) in order to get each ancestor which contains digit
 * need to getAncestorList() in order to check each part against the other
 */
public class ProperHeadWordMatch extends AbstractStringMatch
{
    @Override
    protected boolean match(Mention prev, Mention curr)
    {
        Set<String> location = new HashSet<>(Arrays.asList("southern", "northern", "eastern", "western"));

        Set<DEPNode> prevAncestor = prev.getNode().getAncestorSet();
        Set<DEPNode> currAncestor = curr.getNode().getAncestorSet();
        if (prevAncestor.stream().anyMatch(x -> location.contains(x))) {
        	
        }
        return prevAncestor.stream().anyMatch(x -> x.getWordForm().contains(curr.toString()) && !curr.getNode().isDescendantOf(prev.getNode()) && !x.containsDependent(".*\\d+.*"));
    }
}
