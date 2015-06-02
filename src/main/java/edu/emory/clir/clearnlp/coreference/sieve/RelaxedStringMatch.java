package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.dependency.DEPFeat;
import edu.emory.clir.clearnlp.dependency.DEPLibEn;
//improve this
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.pos.POSLibEn;
import edu.emory.clir.clearnlp.util.Joiner;

public class RelaxedStringMatch extends AbstractStringMatch{
	public RelaxedStringMatch(){
		super();
	}
	
	public RelaxedStringMatch(boolean decapitalize){
		super(decapitalize);
	}
	
	@Override
	/* This is wrong! Relaxed String Match = Dropping relative clauses, PP, and participial modifiers */
	protected String getWordSequence(AbstractMention mention){
		List<DEPNode> l_subNodes = new ArrayList<>(mention.getSubTreeNodes());
		DEPNode node = mention.getNode();
		
		removePunctuations(l_subNodes, node);
		removeRelativeClause(l_subNodes, node);
		removePrepositionalMod(l_subNodes, node);
//		removeParticipialMod(l_subNodes, node);

		return Joiner.join(l_subNodes.stream().map(n -> n.getWordForm()).collect(Collectors.toList()), " ");
	}
	
	private void removePunctuations(List<DEPNode> l_subNodes, DEPNode node){
		List<DEPNode> puncts = node.getDependentListByLabel(DEPLibEn.DEP_PUNCT);
		if(puncts != null)	l_subNodes.removeAll(puncts);
	}
	
	private void removeRelativeClause(List<DEPNode> l_subNodes, DEPNode node){
		DEPNode relcl = node.getFirstDependentByLabel(DEPLibEn.DEP_RELCL);
		if(relcl != null)	l_subNodes.removeAll(relcl.getSubNodeList());
	}
	
	private void removePrepositionalMod(List<DEPNode> l_subNodes, DEPNode node){
		DEPNode pp = node.getFirstDependentByLabel(DEPLibEn.DEP_PREP);
		if(pp != null)	l_subNodes.removeAll(pp.getSubNodeList());
	}

	/* This is incorrect! Need to subcategorize participial post-modifier (POSTMOD) */
	private void removeParticipialMod(List<DEPNode> l_subNodes, DEPNode node){
		List<DEPNode> ppmod = node.getDependentListByLabel(DEPLibEn.DEP_AMOD);
		if(ppmod != null)	l_subNodes.removeAll(ppmod);
	}
}
