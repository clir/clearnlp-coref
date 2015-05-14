package edu.emory.clir.clearnlp.coreference.sieve;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.coreference.utils.Demonym_DictReader;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSetWithConfidence;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.pos.POSTagEn;

public class PreciseConstructMatch extends AbstractSieve
{
	private Map<String, Set<String>> DemonymMap;
	
	public PreciseConstructMatch(String filepath) throws IOException
	{
		DemonymMap = Demonym_DictReader.init(filepath);
	}
	
	@Override
	public void resolute(List<DEPTree> trees, List<Mention> mentions,
			DisjointSetWithConfidence mentionLinks)
	{
		Mention curr, prev;
		int i, j ,size = mentions.size();
		
		for (i = 1;i < size; i++) {
			curr = mentions.get(i);
			
			for (j = i-1; j >= 0; j--){
				prev = mentions.get(j);
				if (acronymMatch(curr, prev) || appositiveMatch(curr, prev) || demonymMatch(curr, prev) 
						|| predicateNominativeMatch(curr,prev)) {
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
    	return Pattern.matches("[^A-Z]", prev) || Pattern.matches("[^A-Z]", curr); 
//        return (curr.equals(prev.replaceAll("[^A-Z]","")) || prev.equals(curr.replaceAll("[^A-Z]","")));
    }

    private boolean appositiveMatch(Mention curr, Mention prev)
    {
        return (curr.getNode().isLabel(DEPTagEn.DEP_APPOS) && curr.getNode().getHead() == prev.getNode());
    }

    private boolean demonymMatch(Mention curr, Mention prev)
    {
         return (DemonymMap.keySet().contains(prev.toString()) && DemonymMap.values().contains(curr.toString()));
    }

    private boolean predicateNominativeMatch(Mention curr, Mention prev)
    {
        Set<String> LV = new HashSet<>(Arrays.asList("be", "is", "am", "are", "seem", "been", "become", "appear"));

        return prev.getNode().isLabel(DEPTagEn.DEP_SUBJ) && curr.getNode().isLabel(DEPTagEn.DEP_DOBJ) &&
                LV.contains(curr.getNode().getWordForm());
    }

}
