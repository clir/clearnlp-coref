package edu.emory.clir.clearnlp.coreference.coref;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.EnglishMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.coreference.sieve.AbstractSieve;
import edu.emory.clir.clearnlp.coreference.sieve.RelaxedStringMatch;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSetWithConfidence;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;

public class ExactStringMatchTest
{
	@Test
	public void test() throws IOException
	{
		AbstractSieve sieve = new RelaxedStringMatch();
		
		TSVReader reader = new TSVReader(0, 1, 2, 3, 7, 4, 5, 6, -1, -1);
		reader.open(new FileInputStream("src/test/resources/edu/emory/clir/clearnlp/coreference/mention/input.mention.cnlp"));
//		OutputStream out = new FileOutputStream("src/test/rescources/edu/emory/clir/clearnlp/coreference/ExactString.txt");	this is giving error not sure why atm
		
		List<DEPTree> trees = new ArrayList<>();
		
		DEPTree tree;
		while((tree = reader.next()) != null)
			trees.add(tree);
		
		AbstractMentionDetector detector = new EnglishMentionDetector();
		List<Mention> mentions = detector.getMentionList(trees);
		DisjointSetWithConfidence coreferences = new DisjointSetWithConfidence(mentions.size()); 
		
		sieve.resolute(trees, mentions, coreferences);
		
		reader.close();
		
		System.out.println(sieve.getClass().toGenericString() + " " + coreferences.toString());
	}
}
