package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSetWithConfidence;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @author alexlutz
 * @version 1.0
 */
public class ProperHeadWordMatch extends AbstractSieve
{
	protected boolean match(AbstractMention prev, AbstractMention curr)
	{
		String prevWords = prev.getHeadNodeWordForm();
		String currWords = curr.getHeadNodeWordForm();
		
		List<DEPNode> prevDependents = 	prev.getNode().getDependentListByLabel(DEPTagEn.DEP_NUMMOD); 
		List<DEPNode> currDependents =  curr.getNode().getDependentListByLabel(DEPTagEn.DEP_NUMMOD);
		
		if (prevWords.equals(currWords) && currDependents.size() == prevDependents.size()) {	//also had && prev.Dependents.size() > 1
			for (int i = 0; i < prevDependents.size(); i++) {
				if (!(prevDependents.get(i).getWordForm().equals(currDependents.get(i).getWordForm())))
					return false;
			}
			return true;
		}
		
		return false;
	}
	
	private boolean articleMatch(AbstractMention prev, AbstractMention curr)
	{
		DEPNode prevNode = prev.getNode().getFirstDependentByLabel(DEPTagEn.DEP_ATTR);
		DEPNode currNode = curr.getNode().getFirstDependentByLabel(DEPTagEn.DEP_ATTR);

		if (prevNode != null && currNode != null) {
			String prevArticle = prevNode.getWordForm(), currArticle = currNode.getWordForm();
			if ((prevArticle.equalsIgnoreCase("a") || prevArticle.equalsIgnoreCase("the")) && currArticle.equalsIgnoreCase("the")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void resolute(List<DEPTree> trees, List<AbstractMention> mentions,
			DisjointSetWithConfidence mentionLinks)
	{
		AbstractMention curr, prev;
		int i, j ,size = mentions.size();
		
		for (i = 1;i < size; i++) {
			curr = mentions.get(i);
			
			for (j = i-1; j >= 0; j--){
				prev = mentions.get(j);
				if (match(prev, curr) && articleMatch(prev, curr)) {
					mentionLinks.union(i, j, 0); break;
				}
			}
		}
	}
}