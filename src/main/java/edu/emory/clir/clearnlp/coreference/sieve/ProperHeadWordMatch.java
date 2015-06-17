package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.StringUtils;

/**
 * @author alexlutz
 * @version 1.0
 */
public class ProperHeadWordMatch extends AbstractSieve
{
	private final Set<String> s_articles = DSUtils.toHashSet("a", "an", "the");
	
	@Override
	public boolean match(AbstractMention prev, AbstractMention curr){
		if(!prev.isMultipleMention() && !curr.isMultipleMention())
			return !matchArticle(prev, curr) && !matchNumMod(prev, curr);
		return false;
	}
	

	private boolean matchNumMod(AbstractMention prev, AbstractMention curr){
		if(curr.hasSameHeadNode(prev)){
			List<String> 	l_prevDependents = prev.getNode().getDependentListByLabel(DEPTagEn.DEP_NUMMOD).stream().map(node -> node.getWordForm()).collect(Collectors.toList()),
							l_currDependents = curr.getNode().getDependentListByLabel(DEPTagEn.DEP_NUMMOD).stream().map(node -> node.getWordForm()).collect(Collectors.toList());
			return l_prevDependents.equals(l_currDependents);
		}
		return true;
	}
	
	private boolean matchArticle(AbstractMention prev, AbstractMention curr){
		DEPNode prevFirstDdependent = prev.getNode().getFirstDependentByLabel(DEPTagEn.DEP_ATTR), currFirstDdependent = curr.getNode().getFirstDependentByLabel(DEPTagEn.DEP_ATTR);
		if(prevFirstDdependent != null && currFirstDdependent != null){
			String prevArticle = StringUtils.toLowerCase(prevFirstDdependent.getWordForm()), currArticle = StringUtils.toLowerCase(currFirstDdependent.getWordForm());
			return s_articles.contains(prevArticle) && currArticle.equals("the");
		}
		return true;
	}
}