package edu.emory.clir.clearnlp.coreference.sieve;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.type.PronounType;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSet;

import java.util.List;

/**
 * @author alexlutz ({@code ajlutz@emory.edu})
 * @version 1.0
 * @since 6/4/15 at 6:07 PM
 * needs a little more refinement
 * */
public class IndefinitePronounMatch extends AbstractSieve
{
    public void resolute(List<AbstractMention> mentions, DisjointSet mentionLinks){
        AbstractMention curr, prev;
        int i = 0, j = 1, size = mentions.size();

        for(; i < size - 1; i++,j++ ){
            prev = mentions.get(i);
            curr = mentions.get(j);
            if(match(prev, curr)){
                if(!mentionLinks.isSameSet(i, j))
                    mentionLinks.union(j, i);
                break;
            }
        }
    }


    protected boolean match(AbstractMention prev, AbstractMention curr)
    {
        return prev.isPronounType(PronounType.INDEFINITE) && prev.getNode().getAncestorSet().stream().filter(x -> x == curr.getHeadNode()).count() > 0;
    }
}
