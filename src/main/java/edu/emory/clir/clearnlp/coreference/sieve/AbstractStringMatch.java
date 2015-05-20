package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.List;

import edu.emory.clir.clearnlp.coreference.mention.SingleMention;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSetWithConfidence;
import edu.emory.clir.clearnlp.dependency.DEPTree;
/**
 * 
 * @author alexlutz
 * this will be the first sieve that performs exact string matching between mentions
 */
public abstract class AbstractStringMatch extends AbstractSieve {
	@Override
	public void resolute(List<DEPTree> trees, List<SingleMention> mentions, DisjointSetWithConfidence mentionLinks) {
		SingleMention curr, prev;
		int i, j, size = mentions.size();
		
		for (i=1; i<size; i++){
			curr = mentions.get(i);
			
			for (j=i-1; j>=0; j--){
				prev = mentions.get(i);
				
				if (match(prev, curr)){
					mentionLinks.union(i, j, 0);
					break;
				}
			}
		}
	}
	
	abstract protected boolean match(SingleMention prev, SingleMention curr);
}
