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
	private Set<String> s_articles;
	
	public ProperHeadWordMatch() {
		s_articles = DSUtils.toHashSet("a", "an", "the");
	}
	
	@Override
	protected boolean match(AbstractMention prev, AbstractMention curr){
		return !matchArticle(prev, curr) && !matchNumMod(prev, curr);
	}
	
<<<<<<< HEAD
	private boolean articleMatch(AbstractMention prev, AbstractMention curr)
	{
		DEPNode prevNode = prev.getNode().getFirstDependentByLabel(DEPTagEn.DEP_ATTR);
		DEPNode currNode = curr.getNode().getFirstDependentByLabel(DEPTagEn.DEP_ATTR);

		if (prevNode != null && currNode != null) {
			String prevArticle = prevNode.getWordForm(), currArticle = currNode.getWordForm();
			if ((prevArticle.equalsIgnoreCase("a") || prevArticle.equalsIgnoreCase("the")) && currArticle.equalsIgnoreCase("the")) {
				return true;
			}
=======
	private boolean matchNumMod(AbstractMention prev, AbstractMention curr){
		if(curr.hasSameHeadNode(prev)){
			List<String> 	l_prevDependents = prev.getNode().getDependentListByLabel(DEPTagEn.DEP_NUMMOD).stream().map(node -> node.getWordForm()).collect(Collectors.toList()),
							l_currDependents = curr.getNode().getDependentListByLabel(DEPTagEn.DEP_NUMMOD).stream().map(node -> node.getWordForm()).collect(Collectors.toList());
			return l_prevDependents.equals(l_currDependents);
>>>>>>> 3139f40a299e5df27f7f1c44f1384084f10080ca
		}
		return false;
	}
	
	private boolean matchArticle(AbstractMention prev, AbstractMention curr){
		DEPNode prevFirstDdependent = prev.getNode().getFirstDependentByLabel(DEPTagEn.DEP_ATTR), currFirstDdependent = curr.getNode().getFirstDependentByLabel(DEPTagEn.DEP_ATTR);
		if(prevFirstDdependent != null && currFirstDdependent != null){
			String prevArticle = StringUtils.toLowerCase(prevFirstDdependent.getWordForm()), currArticle = StringUtils.toLowerCase(currFirstDdependent.getWordForm());
			return s_articles.contains(prevArticle) && currArticle.equals("the");
		}
		return false;
	}
}