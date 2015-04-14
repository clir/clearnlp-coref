package edu.emory.clir.clearnlp.coreference.sieve;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.collection.set.DisjointSet;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import utils.DisjointSetWithConfidence;

import java.util.List;

/**
 * @author Alex Lutz ({@code ajlutz@emory.edu})
 * @version	1.0
 * @since 	April 11, 2015
 * need to find sentence with quote then find subj of speaking verbs - need to change for iterative
 */
public class SpeakerIdentification extends AbstractSieve
{
    private final String QUOTE = "\"";

    @Override
    public void resolute(List<DEPTree> trees, List<Mention> mentions, DisjointSetWithConfidence mentionLinks)
    {

    }
}
