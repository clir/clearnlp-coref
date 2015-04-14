package edu.emory.clir.clearnlp.coreference.sieve;

import com.google.common.collect.Sets;
import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.collection.set.DisjointSet;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.dependency.DEPTree;

import java.util.List;
import java.util.Set;

/**
 * @author Alex Lutz ({@code ajlutz@emory.edu})
 * @version	1.0
 * @since 	April 8, 2015
 * need to add:
 * Acronym
 * Demonym
 * Relative Pronoun
 */
public class PreciseConstructMatch extends AbstractSieve
{
    private static Set<String> LV = Sets.newHashSet("be","is","am","is","are","seem","been","become","appear");

    public PreciseConstructMatch(AbstractMentionDetector d)
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
            //first check if some form of verb then next step
            //want to check if thing has LV if yes check until punct for a dobj that is also NN and then all the dependencies that are conj
            curr = mentions.get(i);

            for (j=i-1; j>=0; j--)
            {
                //here I can check if this is appos if yes then need to link appos (dependencies?) and head word
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

    private boolean predicateNominative(Mention prev, Mention curr)
    {

    }
}

