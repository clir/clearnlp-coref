package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.List;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;

/**
 * @author alexlutz
 * @version 1.0
 */
public class ProperHeadWordMatch extends AbstractStringMatch
{
	@Override
	protected boolean match(AbstractMention prev, AbstractMention curr)
	{
		String prevWords = getWordSequence(prev);
		String currWords = getWordSequence(curr);
		
		List<DEPNode> prevDependents = prev.getNode().getDependentListByLabel(DEPTagEn.DEP_NUMMOD); //feel like this should just be num
		List<DEPNode> currDependents = curr.getNode().getDependentListByLabel(DEPTagEn.DEP_NUMMOD);
		
		if (prevWords.equals(currWords) && currDependents.size() == prevDependents.size()) {	//also had && prev.Dependents.size() > 1
			for (int i = 0; i < prevDependents.size(); i++) {
				if (!(prevDependents.get(i).getWordForm().equals(currDependents.get(i).getWordForm())))
					return false;
			}
			return true;
		}
		
		return false;
	}

	@Override
	protected String getWordSequence(AbstractMention mention){
		return mention.getHeadNodeWordForm();
	}
}