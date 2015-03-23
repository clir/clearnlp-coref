package sieve;

import java.util.List;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.collection.set.DisjointSet;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.dependency.DEPTree;
/**
 * 
 * @author alexlutz
 * this will be the first sieve that performs exact string matching between mentions
 */
public class ExactStringMatch extends AbstractSieve {
	
	public ExactStringMatch(AbstractMentionDetector d){ super(d); }
	
	public Pair<List<Mention>,DisjointSet> getEntities(List<DEPTree> trees)
	{
		List<Mention> mentions = detector.getMentionList(trees);
		DisjointSet set = new DisjointSet(mentions.size());
		int i, j, size = mentions.size();
		Mention curr, prev;
		
		for (i=1; i<size; i++)
		{
			curr = mentions.get(i);
			
			for (j=i-1; j>=0; j--)
			{
				prev = mentions.get(i);
				
				if (exactMatch(prev, curr))
				{
					set.union(i, j);
					break;
				}
			}
		}
		
		return new Pair<List<Mention>, DisjointSet>(mentions, set);
	}
	
	private boolean exactMatch(Mention prev, Mention curr)
	{
		return prev.getNode().getWordForm().equals(curr.getNode().getWordForm());
	}
	

}
