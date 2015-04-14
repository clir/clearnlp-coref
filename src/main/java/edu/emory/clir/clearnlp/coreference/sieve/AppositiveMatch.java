package edu.emory.clir.clearnlp.coreference.sieve;

import edu.emory.clir.clearnlp.coreference.mention.Mention;

/**
 * @author Alex Lutz
 */
public class AppositiveMatch extends AbstractStringMatch
{
    @Override
    protected boolean match(Mention prev, Mention curr)
    {
        if (curr.getNode().getPOSTag().equals("appos") && curr.getNode().getHead() == prev.getNode()) {
            return true;
        }
        return false;
    }
}
