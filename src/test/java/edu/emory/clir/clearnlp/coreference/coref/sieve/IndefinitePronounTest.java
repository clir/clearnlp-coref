package edu.emory.clir.clearnlp.coreference.coref.sieve;

import java.util.List;

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
 * @author alexlutz ({@code ajlutz@emory.edu})
 * @version 1.0
 * @since 6/5/15 at 2:04 AM
 */
public class IndefinitePronounTest
{
    @Test
    public void IndefinitePronounTest()
    {
        SieveSystemCongiuration config = new SieveSystemCongiuration(TLanguage.ENGLISH);
        config.loadDefaultMentionDetectors();
        config.mountSieves(new IndefinitePronounMatch());
        AbstractCoreferenceResolution coref = new SieveSystemCoreferenceResolution(config);

        List<String> l_filePaths = FileUtils.getFileList(PathData.ENG_COREF_MICROSOFT_PARSED_DIR, ".cnlp", false);
        List<DEPTree> trees;
        Pair<List<AbstractMention>, CoreferantSet> resolution;
        for(String filePath : l_filePaths) {
            trees = CoreferenceTestUtil.getTestDocuments(filePath, 9);
            resolution = coref.getEntities(trees);

            CoreferenceTestUtil.printSentences(trees);
			CoreferenceTestUtil.printResolutionResult(resolution);
            CoreferenceTestUtil.printCorefCluster(resolution);

//			break;
        }
    }
}
