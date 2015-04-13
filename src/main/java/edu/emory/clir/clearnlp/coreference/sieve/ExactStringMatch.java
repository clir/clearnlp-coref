package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.List;

import utils.DisjointSetWithConfidence;
import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.dependency.DEPTree;
/**
 * 
 * @author alexlutz
 * this will be the first sieve that performs exact string matching between mentions
 */
public class ExactStringMatch extends AbstractSieve {
	
	@Override
	public void resolute(List<DEPTree> trees, List<Mention> mentions, DisjointSetWithConfidence mentionLinks) {
		Mention curr, prev;
		int i, j, size = mentions.size();
		
		for (i=1; i<size; i++){
			curr = mentions.get(i);
			
			for (j=i-1; j>=0; j--){
				prev = mentions.get(i);
				
				if (exactMatch(prev, curr)){
					mentionLinks.union(i, j, 0);
					break;
				}
			}
		}
	}
	
	private boolean exactMatch(Mention prev, Mention curr){
		return prev.getNode().getWordForm().equals(curr.getNode().getWordForm());
	}
}
