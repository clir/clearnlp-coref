package edu.emory.clir.clearnlp.coreference.coref.sieve;

import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.mention.detector.EnglishMentionDetector;
import edu.emory.clir.clearnlp.coreference.path.PathData;
import edu.emory.clir.clearnlp.coreference.sieve.AbstractSieve;
import edu.emory.clir.clearnlp.coreference.sieve.PronounMatch;
import edu.emory.clir.clearnlp.coreference.type.PronounType;
import edu.emory.clir.clearnlp.coreference.utils.CoreferenceTestUtil;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSetWithConfidence;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * exact string is working correctly
 * relaxed string match now working properly for decapitalized versions as well as capitalized
 * predicate nominative is working correctly, appos works but can give incorrect results,
 * fixed bug with ProperHeadWordMatch, numeric mismatch working correctly,
 */
public class ExactStringMatchTest
{
	@Test
	public void test() throws IOException
	{
//		AbstractSieve test = new ExactStringMatch(true);
		AbstractSieve test = new PronounMatch();
		List<DEPTree> trees = CoreferenceTestUtil.getTestDocuments(PathData.ENG_MENTION, 130, 132);
		DisjointSetWithConfidence set = new DisjointSetWithConfidence(10);
		List<AbstractMention> list = new EnglishMentionDetector().getMentionList(trees);
		test.resolute(trees, list, set);
		System.out.println(list.toString());
		System.out.println(set.toString());
		System.out.println(list.stream().filter(x -> x.isPronounType(PronounType.INDEFINITE)).collect(Collectors.toList()));
	}
}
