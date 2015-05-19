package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.StringJoiner;

import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.dependency.DEPNode;
/**
 * 
 * @author alexlutz
 * this will be the first sieve that performs exact string matching between mentions
 * need to get getCompound() method from Jinho to incorporate
 */
public class ExactStringMatch extends AbstractStringMatch {
	public ExactStringMatch()
	{
		super();
	}
	
public ExactStringMatch(boolean decapitalize)
	{
		super(decapitalize);
	}
	
	@Override
	protected String getWordSequence(Mention mention){
		StringJoiner joiner = new StringJoiner(" ");
		DEPNode node = mention.getNode();
		
		for(DEPNode sub : node.getSubNodeList()) joiner.add(sub.getWordForm());

		return joiner.toString();
	}
}
