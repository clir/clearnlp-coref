package edu.emory.clir.clearnlp.coreference.coref;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.coreference.mention.SingleMention;
import edu.emory.clir.clearnlp.coreference.mention.detector.AbstractMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.detector.EnglishMentionDetector;
import edu.emory.clir.clearnlp.coreference.sieve.AbstractSieve;
import edu.emory.clir.clearnlp.coreference.sieve.ProperHeadWordMatch;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSetWithConfidence;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;
//need to fix
public class ExactStringMatchTest
{
	@Test
	public void test() throws IOException
	{
		AbstractSieve sieve = new ProperHeadWordMatch();
		
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		reader.open(new FileInputStream("src/test/resources/edu/emory/clir/clearnlp/coreference/mention/input.mention.cnlp"));
		
		List<DEPTree> trees = new ArrayList<>();
		
		DEPTree tree;
		while((tree = reader.next()) != null)
			trees.add(tree);
		
		AbstractMentionDetector detector = new EnglishMentionDetector();
		List<SingleMention> mentions = detector.getMentionList(trees);
		DisjointSetWithConfidence coreferences = new DisjointSetWithConfidence(mentions.size()); 
		
		long start = System.currentTimeMillis();
		
//		sieve.resolute(trees, mentions, coreferences);
		
		long end = System.currentTimeMillis();
		
		System.out.println(end - start);
		
		reader.close();
		
		System.out.println(sieve.getClass().toGenericString() + " " + coreferences.toString());
		
		System.out.println(mentions.get(0).getNode());
	}
}
