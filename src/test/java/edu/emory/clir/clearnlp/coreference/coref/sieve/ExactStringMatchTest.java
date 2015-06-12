package edu.emory.clir.clearnlp.coreference.coref.sieve;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.coreference.AbstractCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.SieveSystemCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.config.SieveSystemCongiuration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.path.PathData;
import edu.emory.clir.clearnlp.coreference.sieve.ExactStringMatch;
import edu.emory.clir.clearnlp.coreference.utils.CoreferenceTestUtil;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.lang.TLanguage;
//need to fix
public class ExactStringMatchTest
{
	@Test
	public void testExactStringMatch() throws IOException{
		SieveSystemCongiuration config = new SieveSystemCongiuration(TLanguage.ENGLISH);
		config.loadDefaultMentionDectors();
		config.mountSieves(new ExactStringMatch(true));
		
		AbstractCoreferenceResolution coref = new SieveSystemCoreferenceResolution(config); 
		List<DEPTree> trees = CoreferenceTestUtil.getTestDocuments(PathData.ENG_MENTION, 0, 4);
		
		Pair<List<AbstractMention>, CoreferantSet> resolution = coref.getEntities(trees);
		CoreferenceTestUtil.printSentences(trees);
		CoreferenceTestUtil.printResolutionResult(resolution);
		CoreferenceTestUtil.printCorefCluster(resolution);
	}
}
