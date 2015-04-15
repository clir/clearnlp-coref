package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.StringJoiner;

import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.dependency.DEPNode;
/**
 * @author Alex Lutz ({@code ajlutz@emory.edu})
 */
public class ExactStringMatch extends AbstractStringMatch {
	
	@Override
	protected boolean match(Mention prev, Mention curr){
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
