package edu.emory.clir.clearnlp.coreference.sieve;

import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.dependency.DEPNode;

import java.util.StringJoiner;

/**
 * @author Alex Lutz ({@code ajlutz@emory.edu})
 * @version 1.0
 */
public class AcronymMatcher extends AbstractStringMatch
{
    @Override
    protected boolean match(Mention prev, Mention curr)
    {
        if (curr.getNode().isPOSTag("NNP") && prev.getNode().isPOSTag("NNP")) {
            return acronymTest(getWordSequence(prev.getNode()), getWordSequence(curr.getNode()));   //no getAllDescendentsByPOSTag()
        }
        return false;
    }

    private String getWordSequence(DEPNode node){
        StringJoiner joiner = new StringJoiner("");
        node.getSubNodeList().stream().filter(n -> n.isPOSTag("NNP")).forEach(n -> joiner.add(n.getWordForm()));
        return joiner.toString();
    }

    private boolean acronymTest(String prev, String curr)
    {
        return (curr.equals(prev.replaceAll("[^A-Z]","")) || prev.equals(curr.replaceAll("[^A-Z]","")));
    }
}
