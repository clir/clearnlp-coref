package edu.emory.clir.clearnlp.coreference.coref.sieve;

import java.util.List;

import org.junit.Test;

import edu.emory.clir.clearnlp.NLPDecoder;
import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.coreference.AbstractCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.SieveSystemCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.config.SieveSystemCongiuration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.path.PathData;
import edu.emory.clir.clearnlp.coreference.sieve.RuleBasedPleonasticIt;
import edu.emory.clir.clearnlp.coreference.utils.CoreferenceTestUtil;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
import edu.emory.clir.clearnlp.coreference.utils.util.CorpusConverter;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author alexlutz
 * @version 1.0
 * @since 6/11/2015
 */
public class RuleBasedPleonasticItTest
{
	private final NLPDecoder decode = new NLPDecoder(TLanguage.ENGLISH);
	private RuleBasedPleonasticIt test = new RuleBasedPleonasticIt();
	private Pair<List<AbstractMention>, CoreferantSet> resolution;
	private List<DEPTree> trees;
	
	@Test
	public void test()
	{
		SieveSystemCongiuration config = new SieveSystemCongiuration(TLanguage.ENGLISH);
		config.loadDefaultMentionDectors();
		config.loadDefaultSieves(false);
		AbstractCoreferenceResolution coref = new SieveSystemCoreferenceResolution(config);

		List<String> l_filePaths = FileUtils.getFileList(PathData.ENG_COREF_MICROSOFT_PARSED_DIR, ".cnlp", false);
		for(String filePath : l_filePaths) { 	
			trees = CoreferenceTestUtil.getTestDocuments(filePath, 9);
			resolution = coref.getEntities(trees);
			test.removePleonasticIt(resolution.o1);

		}
	        testSentence(coref, "It seems that he has arrived.");
	        testSentence(coref, "It seems unlikely that he will finish.");
	        testSentence(coref, "It seemed it was the case.");
	        testSentence(coref, "It is 3 pm.");
	        testSentence(coref, "It is time to go home");
	        testSentence(coref, "It is quite late");
	        testSentence(coref, "It is rainy.");
	        testSentence(coref, "It is raining");
	        testSentence(coref, "It is autumn.");
	        testSentence(coref, "It is funny that he left so early.");
	        testSentence(coref, "It is odd to walk that way.");	
	        testSentence(coref, "We made it certain that we would succeed."); //latest version works but is not what the pattern is
	        testSentence(coref, "It was thought that we would finish on time today.");
	        testSentence(coref, "It was obvious I could do it.");
	        testSentence(coref, "It is the plan that we will wake up at dawn.");	
	        testSentence(coref, "It is on this conclusion that I base my work.");	
	        testSentence(coref, "It is very likely she will come");
	        testSentence(coref, "It is necessary that you check the levels before starting");
	        testSentence(coref, "It was agreed that we would eat at noon.");	//need to double check cases like this
	        testSentence(coref, "It was agreed that the transaction would be kept secret.");
	        testSentence(coref, "John found it difficult to speak");
	        
	}
	
	private void testSentence(AbstractCoreferenceResolution coref, String line)
	{
		 trees = decode.toDEPTrees(line);
		 resolution = coref.getEntities(trees);
		 test.removePleonasticIt(resolution.o1);
	}
}