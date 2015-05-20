package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.StringJoiner;

import edu.emory.clir.clearnlp.coreference.mention.SingleMention;
import edu.emory.clir.clearnlp.dependency.DEPNode;
/**
 * 
 * @author alexlutz
 * this will be the first sieve that performs exact string matching between mentions
 */
public class ExactStringMatch extends AbstractStringMatch {
	
	@Override
	protected boolean match(SingleMention prev, SingleMention curr){
		String prevWords = getWordSequence(prev.getNode());
		String currWords = getWordSequence(curr.getNode());
		return prevWords.equals(currWords);
	}
	
	private String getWordSequence(DEPNode node){
		StringJoiner joiner = new StringJoiner(" ");
		
		for(DEPNode sub : node.getSubNodeList()) joiner.add(sub.getWordForm());

		return joiner.toString();
	}
}
