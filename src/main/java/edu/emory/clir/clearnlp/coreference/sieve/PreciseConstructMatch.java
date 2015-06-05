package edu.emory.clir.clearnlp.coreference.sieve;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;
import edu.emory.clir.clearnlp.pos.POSTagEn;
public class PreciseConstructMatch extends AbstractSieve{

	@Override
	protected boolean match(AbstractMention prev, AbstractMention curr) {
		return appositiveMatch(prev, curr) || predicateNominativeMatch(prev, curr) || acronymMatch(prev, curr); 
	}
	
	private boolean appositiveMatch(AbstractMention prev, AbstractMention curr){
        return (curr.getNode().isLabel(DEPTagEn.DEP_APPOS) && curr.getNode().getHead() == prev.getNode());
    }

	private boolean predicateNominativeMatch(AbstractMention prev, AbstractMention curr){
    	DEPNode p = prev.getNode(), c = curr.getNode();
        return p.getHead() == c.getHead() && (p.getLabel().startsWith(DEPTagEn.DEP_NSUBJ) && c.isLabel(DEPTagEn.DEP_ATTR));
    }
	
	private boolean acronymMatch(AbstractMention prev, AbstractMention curr){
        if (curr.getNode().isPOSTag(POSTagEn.POS_NNP) && prev.getNode().isPOSTag(POSTagEn.POS_NNP)){
        	String curr_acronym = curr.getAcronym(), prev_acronym = prev.getAcronym();
        	if(curr_acronym != null && prev_acronym != null && curr_acronym.length() > 1)
        		return curr_acronym.equals(prev_acronym);
        }
        return false;
    }    
}
