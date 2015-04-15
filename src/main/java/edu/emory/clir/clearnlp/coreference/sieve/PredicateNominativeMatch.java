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
 * Demonym
 */
public class PredicateNominativeMatch extends AbstractStringMatch
{
    private Set<String> LV = Sets.newHashSet("be","is","am","are","seem","been","become","appear");

    @Override
    protected boolean match(Mention prev, Mention curr) {return prev.getNode().isLabel("subj") && curr.getNode().isLabel("dobj") &&
            LV.contains(curr.getNode().getWordForm());}
}

