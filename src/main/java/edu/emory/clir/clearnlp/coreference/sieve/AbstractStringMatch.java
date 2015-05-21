package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.List;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSetWithConfidence;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.StringUtils;
/**
 * 
 * @author alexlutz
 * this will be the first sieve that performs exact string matching between mentions
 */
public abstract class AbstractStringMatch extends AbstractSieve {
	
	protected boolean decapitalize;
	
	public AbstractStringMatch()
	{
		decapitalize = false;
	}
	
	public AbstractStringMatch(boolean decapitalize)
	{
		this.decapitalize = decapitalize;
	}
	
	protected boolean match(AbstractMention prev, AbstractMention curr){
		String prevWords = getWordSequence(prev);
		String currWords = getWordSequence(curr);
		
		if (decapitalize)
		{
			prevWords = StringUtils.toLowerCase(prevWords);
			currWords = StringUtils.toLowerCase(currWords);
		}
		
		return prevWords.equals(currWords);
	}
	
	abstract protected String getWordSequence(AbstractMention mention);
	
	@Override
	public void resolute(List<DEPTree> trees, List<AbstractMention> mentions, DisjointSetWithConfidence mentionLinks) {
		AbstractMention curr, prev;
		int i, j, size = mentions.size();
		
		for (i=1; i<size; i++){
			curr = mentions.get(i);
			
			for (j=i-1; j>=0; j--){
				prev = mentions.get(j);
				
				if (match(prev, curr)){
					mentionLinks.union(i, j, 0);
					break;
				}
			}
		}
	}
}
