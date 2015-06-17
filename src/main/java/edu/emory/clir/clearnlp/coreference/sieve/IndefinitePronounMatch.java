package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.List;
import java.util.Set;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.type.PronounType;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.DSUtils;

/**
 * @author alexlutz ({@code ajlutz@emory.edu})
 * @version 1.0
 * @since 6/4/15 at 6:07 PM
 * needs a little more refinement
 **/
public class IndefinitePronounMatch extends AbstractSieve{
    private final Set<String> bannedWords = DSUtils.toHashSet("everyone", "everybody", "everything", "anything");
    
    @Override
    public void resolute(List<DEPTree> trees, List<AbstractMention> mentions, CoreferantSet mentionLinks)
    {
        int i = 0, size = mentions.size();
        AbstractMention prev, curr = null;
        for (; i < size-1; i++) {
            if ((prev = mentions.get(i)).isPronounType(PronounType.INDEFINITE)) {
                if (match(prev, curr)) {
                    if (!mentionLinks.isSameSet(i, i+1))
                        mentionLinks.union(i, i+1);
                }
            }
        }
    }

    @Override
    protected boolean match(AbstractMention prev, AbstractMention curr)
    {
        DEPTree tree = prev.getTree();
        DEPNode nextWord = tree.get(prev.getNode().getID() + 1);
        if (nextWord != null) 
        	return nextWord.isLabel(DEPTagEn.DEP_PUNCT) 
        			&& nextWord.getAncestorSet().contains(prev.getHeadNode()) 
        			&& !bannedWords.contains(prev.getWordFrom());
        return false;
    }
}
