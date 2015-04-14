package edu.emory.clir.clearnlp.coreference.sieve;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.collection.set.DisjointSet;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.dependency.DEPTree;

import java.util.List;

/**
 * @author Alex Lutz ({@code ajlutz@emory.edu})
 * @version	1.0
 * @since 	April 11, 2015
 */
public class SpeakerIdentification extends AbstractSieve
{
    private final String QUOTE = "\"";

    public SpeakerIdentification(AbstractMentionDetector d)
    {
        super(d);
    }

    @Override
    public Pair<List<Mention>, DisjointSet> getEntities(List<DEPTree> trees)
    {
        List<Mention> mentions = detector.getMentionList(trees);
        DisjointSet set = new DisjointSet(mentions.size());
        int i, j, size = mentions.size();
        Mention curr, prev;

        for (i=1; i<size; i++)
        {
            curr = mentions.get(i);

            for (j=i-1; j>=0; j--)
            {
                prev = mentions.get(i);

                if (predicateNominative(prev, curr))
                {
                    set.union(i, j);
                    break;
                }
            }
        }
        return new Pair<List<Mention>, DisjointSet>(mentions, set);
    }
}
