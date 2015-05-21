package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.List;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.mention.SingleMention;
import edu.emory.clir.clearnlp.coreference.type.PronounType;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;

/**
 * @author alexlutz
 * @version 1.0
 * need to add article mismatch here - no need to add before the first sieve
 */
public class ProperHeadWordMatch extends AbstractStringMatch
{
	@Override
	protected boolean match(AbstractMention prev, AbstractMention curr)
	{
		String prevWords = prev.getHeadNodeWordForm();
		String currWords = curr.getHeadNodeWordForm();
		
		List<DEPNode> prevDependents = ((DEPNode) prev.getNode()).getDependentListByLabel(DEPTagEn.DEP_NUMMOD); //this should be num but I do not have up to date clear snapshot
		List<DEPNode> currDependents = ((DEPNode) curr.getNode()).getDependentListByLabel(DEPTagEn.DEP_NUMMOD);
		
		if (prevWords.equals(currWords) && currDependents.size() == prevDependents.size()) {	//also had && prev.Dependents.size() > 1
			for (int i = 0; i < prevDependents.size(); i++) {
				if (!(prevDependents.get(i).getWordForm().equals(currDependents.get(i).getWordForm())))
					return false;
			}
			return true;
		}
		
		return false;
	}
}