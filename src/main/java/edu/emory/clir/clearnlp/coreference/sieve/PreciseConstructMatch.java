package edu.emory.clir.clearnlp.coreference.sieve;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import edu.emory.clir.clearnlp.coreference.dictionary.PathSieve;
import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSetWithConfidence;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.pos.POSTagEn;
import edu.emory.clir.clearnlp.util.IOUtils;

public class PreciseConstructMatch extends AbstractSieve 
{
	private static final char STOPCHAR = ',';
    private static final char KEYBREAK = '\t';
    private static final char NEWLINE = '\n';
    private Map<String, Set<String>> DemonymMap;
	
	public PreciseConstructMatch() throws IOException
	{
		DemonymMap = init(PathSieve.ENG_DEMONYM);
	}
	
	public static Map<String, Set<String>> init(String filepath) throws IOException
    {
        Map<String, Set<String>>  DemonymMap = new HashMap<>();
        Set<String> DemonymSet = new HashSet<>();
        FileInputStream input = IOUtils.createFileInputStream(filepath);
        int i = 0; String key = ""; String line = "";

        while((i = input.read()) != 0) {
        	if ((char) i != STOPCHAR) line += i;
        	else if ((char) i == KEYBREAK) {
                key = line.trim();
                line = "";
            } else if ((char) i == NEWLINE) {
                DemonymMap.put(key, new HashSet<>(DemonymSet));
                DemonymSet.clear();
                key = "";
                line = "";
            }
            else {
                DemonymSet.add(line.trim());
                line = "";
            }
        }
        return DemonymMap;
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