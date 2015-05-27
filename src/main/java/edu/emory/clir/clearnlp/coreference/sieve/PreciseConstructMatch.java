package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.List;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSet;
import edu.emory.clir.clearnlp.coreference.utils.util.CoreferenceStringUtils;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.pos.POSTagEn;
import edu.emory.clir.clearnlp.util.Joiner;
/*
 * need to fix init, and acronym 
 */
public class PreciseConstructMatch extends AbstractSieve{	
		
	@Override
	public void resolute(List<DEPTree> trees, List<AbstractMention> mentions, DisjointSet mentionLinks)
	{
		AbstractMention curr, prev;
		int i, j ,size = mentions.size();
		
		for (i = 1;i < size; i++) {
			curr = mentions.get(i);
			
			for (j = i-1; j >= 0; j--){
				prev = mentions.get(j);
				if (acronymMatch(curr, prev) || appositiveMatch(curr, prev) ||  predicateNominativeMatch(curr,prev)) {
					mentionLinks.union(i, j, 0); break;
				}
			}
		}
	}
	
	private boolean acronymMatch(AbstractMention curr, AbstractMention prev)
    {
        if (curr.getNode().isPOSTag(POSTagEn.POS_NNP) && prev.getNode().isPOSTag(POSTagEn.POS_NNP)) {
            return compareUpperCases(getWordSequence(prev), getWordSequence(curr));
        }
        return false;
    }

    private String getWordSequence(AbstractMention mention){
    	return Joiner.join(mention.getSubTreeNodes().stream().filter(node -> node.isPOSTag(POSTagEn.POS_NNP)).collect(Collectors.toList()), "");
    }

    private boolean compareUpperCases(String s1, String s2)
    {
    	String u1 = CoreferenceStringUtils.getAllUpperCaseLetters(s1);
    	String u2 = CoreferenceStringUtils.getAllUpperCaseLetters(s2);
    	return u1.equals(u2);
    }
    
    private boolean appositiveMatch(AbstractMention curr, AbstractMention prev){
        return (curr.getNode().isLabel(DEPTagEn.DEP_APPOS) && curr.getNode().getHead() == prev.getNode());
    }

    private boolean predicateNominativeMatch(AbstractMention curr, AbstractMention prev){
    	DEPNode p = prev.getNode(), c = curr.getNode();
        return p.getHead() == c.getHead() && p.getLabel().startsWith(DEPTagEn.DEP_NSUBJ) && c.isLabel(DEPTagEn.DEP_ATTR);
    }

}
