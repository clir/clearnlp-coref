package edu.emory.clir.clearnlp.coreference.sieve;

import edu.emory.clir.clearnlp.coreference.mention.Mention;

/**
 * @author Alex Lutz
 * @version 1.0
 */
public class AppositiveMatch extends AbstractStringMatch
{
    @Override
    protected boolean match(Mention prev, Mention curr) {return (curr.getNode().isLabel("appos") && curr.getNode().getHead() == prev.getNode());}
}
