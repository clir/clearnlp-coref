package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.List;

import utils.DisjointSetWithConfidence;
import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.dependency.DEPTree;

public class RelaxedStringMatch extends AbstractSieve{
	
	@Override
	public DisjointSetWithConfidence resolute(List<DEPTree> trees, List<Mention> mentions, DisjointSetWithConfidence mentionLinks) {
		Mention curr, prev;
		int i, j, size = mentions.size();
		
		for (i=1; i<size; i++){
			curr = mentions.get(i);
			
			for (j=i-1; j>=0; j--){
				prev = mentions.get(i);
				
				if (headMatch(prev, curr)){
					mentionLinks.union(i, j, 0);
					break;
				}
			}
		}
		
		return mentionLinks;
	}
	
	private boolean headMatch(Mention prev, Mention curr){
		return prev.getNode().getHead().getWordForm().equals(curr.getNode().getHead().getWordForm());
	}
}
