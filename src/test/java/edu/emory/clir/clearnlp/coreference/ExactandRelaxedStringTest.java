package edu.emory.clir.clearnlp.coreference;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.collection.set.DisjointSet;
import edu.emory.clir.clearnlp.coreference.mention.Mention;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;

public class ExactandRelaxedStringTest
{
	@Test
	public void Sieve1Test() throws Exception
	{
		TSVReader reader = new TSVReader(0, 1, 2, 3, 4, 5, 6, 7);
		reader.open(new FileInputStream("inputFile.cnlp"));
		List<DEPTree> trees = new ArrayList<>();
		DEPTree tree;
		
		while ((tree = reader.next()) != null)
			trees.add(tree);
		
		AbstractCoreferenceResolution coref = new ExactStringMatch();
		Pair<List<Mention>,DisjointSet> entities = coref.getEntities(trees);
		entities.o2.inSameSet(0, 1);
	}
	
	@Test
	public void Sieve2Test() throws Exception
	{
		TSVReader reader = new TSVReader(0, 1, 2, 3, 4, 5, 6, 7);
		reader.open(new FileInputStream("inputFile.cnlp"));
		List<DEPTree> trees = new ArrayList<>();
		DEPTree tree;
		
		while ((tree = reader.next()) != null)
			trees.add(tree);
		
		AbstractCoreferenceResolution coref = new RelaxedStringMatch();
		Pair<List<Mention>,DisjointSet> entities = coref.getEntities(trees);
		entities.o2.inSameSet(0, 1);
	}
}
