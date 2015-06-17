package edu.emory.clir.clearnlp.coreference.coref.sieve;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.junit.Test;

import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.coreference.AbstractCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.SieveSystemCoreferenceResolution;
import edu.emory.clir.clearnlp.coreference.config.SieveSystemCongiuration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.path.PathData;
import edu.emory.clir.clearnlp.coreference.sieve.IndefinitePronounMatch;
import edu.emory.clir.clearnlp.coreference.sieve.RuleBasedPleonasticIt;
import edu.emory.clir.clearnlp.coreference.utils.CoreferenceTestUtil;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
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
	        config.loadDefaultMentionDectors();
	        config.mountSieves(new IndefinitePronounMatch());
	        AbstractCoreferenceResolution coref = new SieveSystemCoreferenceResolution(config);

	        List<String> l_filePaths = FileUtils.getFileList(PathData.ENG_COREF_MICROSOFT_PARSED_DIR, ".cnlp", false);
	        RuleBasedPleonasticIt test = new RuleBasedPleonasticIt();
	        List<DEPTree> trees;
	        Pair<List<AbstractMention>, CoreferantSet> resolution;
	        for(String filePath : l_filePaths) {
	            trees = CoreferenceTestUtil.getTestDocuments(filePath, 9);
	            resolution = coref.getEntities(trees);
	            long start = System.currentTimeMillis();
	            test.removePleonasticIt(resolution.o1);
	            long end = System.currentTimeMillis();
	            
	            System.out.println(end - start);
		}
	}
}