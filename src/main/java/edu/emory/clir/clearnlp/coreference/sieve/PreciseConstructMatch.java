package edu.emory.clir.clearnlp.coreference.sieve;

import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.pos.POSTagEn;
import utils.DisjointSetWithConfidence;

import java.util.*;

/**
 * Created by alexlutz on 4/14/15.
 */
public class PreciseConstructMatch extends AbstractSieve
{
    @Override
    public void resolute(List<DEPTree> trees, List<Mention> mentions, DisjointSetWithConfidence mentionLinks)
    {
        Mention curr, prev;
        int i, j, size = mentions.size();

        for (i = 1; i < size; i++){
            curr = mentions.get(i);

            for (j = i-1; j >= 0; j--){
                prev = mentions.get(i);
                if (acronymMatch(curr, prev) || appositiveMatch(curr, prev) || demonymMatch(curr, prev) ||
                        predicateNominativeMatch(curr, prev)){
                    mentionLinks.union(i, j, 0); break;
                }
            }
        }
    }

    private boolean acronymMatch(Mention curr, Mention prev)
    {
        if (curr.getNode().isPOSTag(POSTagEn.POS_NNP) && prev.getNode().isPOSTag(POSTagEn.POS_NNP)) {
            return acronymTest(getWordSequence(prev.getNode()), getWordSequence(curr.getNode()));
        }
        return false;
    }

    private String getWordSequence(DEPNode node){
        StringJoiner joiner = new StringJoiner("");
        node.getSubNodeList().stream().filter(n -> n.isPOSTag(POSTagEn.POS_NNP)).forEach(n -> joiner.add(n.getWordForm()));
        return joiner.toString();
    }

    private boolean acronymTest(String prev, String curr)
    {
        return (curr.equals(prev.replaceAll("[^A-Z]","")) || prev.equals(curr.replaceAll("[^A-Z]","")));
    }

    private boolean appositiveMatch(Mention curr, Mention prev)
    {
        return (curr.getNode().isLabel(DEPTagEn.DEP_APPOS) && curr.getNode().getHead() == prev.getNode());
    }

    private boolean demonymMatch(Mention curr, Mention prev)
    {
        Set<String> demonym =
    }

    private boolean predicateNominativeMatch(Mention curr, Mention prev)
    {
        Set<String> LV = new HashSet<>(Arrays.asList("be", "is", "am", "are", "seem", "been", "become", "appear"));

        return prev.getNode().isLabel(DEPTagEn.DEP_SUBJ) && curr.getNode().isLabel(DEPTagEn.DEP_DOBJ) &&
                LV.contains(curr.getNode().getWordForm());
    }
}
