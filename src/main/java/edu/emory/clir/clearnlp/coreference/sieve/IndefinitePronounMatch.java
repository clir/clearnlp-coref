package edu.emory.clir.clearnlp.coreference.sieve;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.type.PronounType;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSet;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.DSUtils;

import java.util.List;
import java.util.Set;

/**
 * @author alexlutz ({@code ajlutz@emory.edu})
 * @version 1.0
 * @since 6/4/15 at 6:07 PM
 * needs a little more refinement
 * */
public class IndefinitePronounMatch extends AbstractSieve
{
    private final Set<String> bannedWords = DSUtils.toHashSet("everyone", "everybody", "everything", "anything");

    public void resolute(List<DEPTree> trees, List<AbstractMention> mentions, DisjointSet mentionLinks)
    {
        int i = 0, size = mentions.size();
        AbstractMention mention;
        for (; i < size; i++) {
            if ((mention = mentions.get(i)).isPronounType(PronounType.INDEFINITE)) {
                DEPTree tree = mention.getTree();
                int index = mention.getNode().getID() + 1;
                if (!(tree.get(index).isLabel(DEPTagEn.DEP_PUNCT)) && tree.get(index).getAncestorSet().contains(mention.getHeadNode()) && !bannedWords.contains(mention.getWordFrom()) && adverbTest(mention.getWordFrom(), tree.get(index).getWordForm())) {
                    if (!mentionLinks.isSameSet(i, i+1))
                        mentionLinks.union(i, i+1);
                }
            }
        }
    }

    private boolean adverbTest(String mention, String next)
    {
        return mention.equals("all") && next.equals("over") || mention.equals("each") && next.equals("other");
    }

    @Override
    protected boolean match(AbstractMention prev, AbstractMention curr)
    {
        return false;
    }
}