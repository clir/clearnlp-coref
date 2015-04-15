package edu.emory.clir.clearnlp.coreference.sieve;

import edu.emory.clir.clearnlp.coreference.mention.Mention;

import java.util.function.BiFunction;

/**
 * Created by alexlutz on 4/14/15.
 */
public class PreciseConstructMatch extends AbstractStringMatch
{
    @Override
    protected boolean match(Mention prev, Mention curr)
    {
        BiFunction<Mention, Mention, Boolean> bi = (a,b) -> new AcronymMatcher().match(a,b);
        return bi.apply(prev,curr);
    }
}
