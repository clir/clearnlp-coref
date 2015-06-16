package edu.emory.clir.clearnlp.coreference.coref.sieve;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.coreference.AbstractCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.SieveSystemCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.config.SieveSystemCongiuration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.path.PathData;
import edu.emory.clir.clearnlp.coreference.sieve.RuleBasedPleonasticIt;
import edu.emory.clir.clearnlp.coreference.utils.CoreferenceTestUtil;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSet;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.FileUtils;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author alexlutz
 * @version 1.0
 * @since 6/11/2015
 */
public class RuleBasedPleonasticItTest
{
	@Test
	public void test()
	{
		SieveSystemCongiuration config = new SieveSystemCongiuration(TLanguage.ENGLISH);
		config.loadMentionDectors(true, false, true);
		config.loadDefaultSieves(false);
		AbstractCoreferenceResolution coref = new SieveSystemCoreferenceResolution((SieveSystemCongiuration)config);
		List<String> l_filePaths = FileUtils.getFileList(PathData.ENG_COREF_MICROSOFT_PARSED_DIR, ".cnlp", false);
		List<DEPTree> trees;
		Pair<List<AbstractMention>, DisjointSet> resolution = null;
		RuleBasedPleonasticIt test = new RuleBasedPleonasticIt();
		for(String filePath : l_filePaths){
			trees = CoreferenceTestUtil.getTestDocuments(filePath, 9);
			resolution = coref.getEntities(trees);
//			CoreferenceTestUtil.printSentences(trees);
			long start = System.currentTimeMillis();
			test.removePleonasticIt(resolution.o1);
			long end = System.currentTimeMillis();
			System.out.println(end - start);
		}
	}
}