package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import edu.emory.clir.clearnlp.coreference.dictionary.PathDictionary;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.type.AttributeType;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.pos.POSLibEn;
import edu.emory.clir.clearnlp.pos.POSTagEn;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author alexlutz
 * @version 1.0
 * @since	6/10/15
 * need to re-look at cognitive noun patterns
 * NEED TO FIX WITH HEAD WORDS SINCE it is really raining DOES NOT HAVE IS AS THE ROOT
 * WANT TO MOVE COG NOUN INTO ITBE PATTTERN
 * MAKE TREE GLOBAL VARIABLE
 */
public class RuleBasedPleonasticIt
{
	private DEPTree tree;
	private final Set<String> seasons;
	private final Set<String> cognitiveVerbs;
	private final Set<String> cognitiveNouns;
	private final Set<String> weatherTerms;		//need to make sure that I have all of the present-progressive terms
	private final Set<String> timeWords;
	private final Predicate<String> isAdverb	= x -> POSLibEn.isAdverb(x);
	private final Predicate<String> isAdjective	= x -> POSLibEn.isAdjective(x);
	private final Predicate<String> isVerb		= x -> POSLibEn.isVerb(x);
	private final Predicate<String> isNoun		= x -> POSLibEn.isNoun(x);
	private final Predicate<String> isPronoun	= x -> POSLibEn.isPronoun(x);
	
	
	public RuleBasedPleonasticIt()
	{
		seasons			= DSUtils.toHashSet("summer", "autumn", "fall", "winter", "spring");
		cognitiveVerbs	= DSUtils.createStringHashSet(IOUtils.createFileInputStream(PathDictionary.COGNITIVE_VERBS));//only used in one thing might want to remove
		cognitiveNouns	= DSUtils.createStringHashSet(IOUtils.createFileInputStream(PathDictionary.COGNITIVE_NOUNS));
		weatherTerms	= DSUtils.toHashSet("cloudy", "rainy", "snowy", "sleet", "rain", "snow", "thunder", "cold", "hot", "humid", "freezing", "sunny", 
				"overcast", "hazy", "foggy", "hail", "windy", "sleet", "gusty", "drizzle", "drizzly", "damp", "stormy", "showering", "gust", "storm", 
				"arid", "muggy", "raining", "snowing", "sleeting", "hailing", "drizzling", "storming", "gusting", "thundering");
		timeWords = DSUtils.toHashSet("early", "late", "time", "afternoon", "bedtime", "morning", "evening", "daytime", "bedtime"
				+ "twilight", "tonight", "dinnertime", "sometime", "midnight", "midafternoon");
	}
	
	public void removePleonasticIt(List<AbstractMention> mentions)
	{
		for (AbstractMention mention : mentions) {
			if (!mention.hasAttribute(AttributeType.CONJUNCTION) && mention.getLemma().equals("it")) {
				if(isPleonastic(mention)) {
					System.out.println(mention.getTree().toString() +"\n");
//					mentions.remove(mention);
				}
			}
		}
	}
	
	private boolean isPleonastic(AbstractMention mention)
	{
		DEPTree tree = mention.getTree();
		int id = mention.getNode().getID();
		return findItBe(tree, id) || specialConstruct(tree, id) || heuristicSeem(tree, id) || cognitiveNounPattern(tree, id) || nonBePattern(tree, id); 
	}

	private boolean itBeAdj(DEPTree tree, int i)
	{
		return hasWord(tree, i, "that") || hasPOS(tree, i, isPronoun) || forTo(tree, i) || hasPOSByTag(tree, i, POSTagEn.POS_TO);
	}
	
	private boolean specialConstruct(DEPTree tree, int id)
	{
		int i = findPOS(tree, 1, isNoun);
		i = findPOS(tree, ++i, isVerb);
		i = findWord(tree, ++i, "it");
		i = findPOS(tree, ++i, isAdjective);
//		return forTo(tree, ++i) || hasPOSByTag(tree, ++i, POSTagEn.POS_TO);	//this does not match with the patterns that he gives but matches with his examples
		return (i < tree.size()) ? true : false;
	}
	
	private boolean findItBe(DEPTree tree, int id)	
	{
		int i = 3, size = tree.size();
		DEPNode node = tree.get(id);
		if (node.getHead().getLemma().equals("be")) {	
			for (; i < size; i++) {
				node = tree.get(i);
				if 		(isAdverb.test(node.getPOSTag())) continue;
				else if (hasContainsWord(tree, i, weatherTerms)) return true;
				else if (hasContainsWord(tree, i, timeWords)) return true;
				else if (hasContainsWord(tree, i, seasons)) return true;	
				else if (node.isLabel(DEPTagEn.DEP_NUMMOD)) return true;
				else if (isAdjective.test(node.getPOSTag())) return itBeAdj(tree, ++i);
				return false;
				}
			}
		return false;
	}
	
	private boolean forTo(DEPTree tree, int i)	//need to make the for optional
	{
		i = findWordWithPOSTag(tree, i, "for", POSTagEn.POS_IN);
		return (i < tree.size()) ?  hasPOSByTag(tree, i, POSTagEn.POS_TO) : false;
	}
	
	private boolean hasPOS(DEPTree tree, int i, Predicate<String> f1)
	{
		int size = tree.size();
		for (; i < size; i++) {
			String pos = tree.get(i).getPOSTag();
			if (isAdverb.test(pos)) continue;
			else if (f1.test(pos)) return true;
			return false;
		}
		return false;
	}
	
//	private boolean hasPOS(DEPTree tree, int i, Predicate<String> f1)
//	{
//		int a = findPOS(tree, i, f1);
//		return (a < tree.size()) ?  true : false;
//	}
	
	private int findPOS(DEPTree tree, int i, Predicate<String> f1)
	{
		int size = tree.size();
		for (; i < size; i++) {
			String pos = tree.get(i).getPOSTag();
			if (isAdverb.test(pos)) continue;
			else if (f1.test(pos)) return i;
			return size;
		}
		return size;
	}
	
	private boolean hasPOSByTag(DEPTree tree, int i, String pos)
	{
		int size = tree.size();
		for (; i < size; i++) {
			if		(isAdverb.test(pos))		continue;
			else if (tree.get(i).isPOSTag(pos)) return true;
			return false;
		}
		return false;
	}
	
	private boolean hasWord(DEPTree tree, int i, String word)
	{
		int size = tree.size();
		for (; i < size; i++) {
			DEPNode node = tree.get(i);
			if (isAdverb.test(node.getPOSTag())) continue;
			else if (node.getLemma().equals(word)) return true;
			return false;
		}
		return false;
	}
	
//	private boolean hasWord(DEPTree tree, int i, String word)
//	{
//		int a = findWord(tree, i, word);
//		return (a < size) ? true : false;
//	}	
	
	private int findWord(DEPTree tree, int i, String word)
	{
		int size = tree.size();
		for (; i < size; i++) {
			DEPNode node = tree.get(i);
			if (isAdverb.test(node.getPOSTag())) continue;
			if (node.getLemma().equals(word)) return i;
			return size;
		}
		return size;
	}
	
//	private boolean hasWordWithPOSTag(DEPTree tree, int i, String word, String pos)
//	{
//		int size = tree.size();
//		for (; i < size; i++) {
//			DEPNode node = tree.get(i);
//			if (isAdverb.test(node.getPOSTag())) continue;
//			else if (node.getLemma().equals(word) && node.isPOSTag(pos)) return true;
//		}
//		return false;
//	}
	
	private int findWordWithPOSTag(DEPTree tree, int i, String word, String pos)
	{
		int size = tree.size();
		for (; i < size; i++) {
			DEPNode node = tree.get(i);
			if (isAdverb.test(node.getPOSTag())) continue;
			else if (node.getLemma().equals(word) && node.isPOSTag(pos)) return i;
			return size;
		}
		return size;
	}
	
	private boolean hasContainsWord(DEPTree tree, int i, Set<String> container)
	{
		int size = tree.size();
		for (; i < size; i++) {
			DEPNode node = tree.get(i);
			if (isAdverb.test(node.getPOSTag())) continue;
			else if (container.contains(node.getWordForm())) return true;
			return false;
		}
		return false;
	}
	
//	private boolean hasContainsWord(DEPTree tree, int i, Set<String> container)
//	{
//		int a = findContainsWord(tree, i, container);
//		return (a < size) ? true : false;
//	}
//	
//	private int findContainsWord(DEPTree tree, int i, Set<String> container)
//	{
//		int size = tree.size();
//		for (; i < size; i++){
//			DEPNode node = tree.get(i);
//			if (isAdverb.test(node.getPOSTag())) continue;
//			else if (container.contains(node.getWordForm())) return i;
//			return size;
//		}
//		return size;
//	}
	
	private boolean heuristicSeem(DEPTree tree, int id)	//missing one
	{
		return tree.get(id).getHead().getLemma().equals("seem") && !hasPOSByTag(tree, tree.getFirstRoot().getID(), POSTagEn.POS_TO);
	}
	
	private boolean cognitiveNounPattern(DEPTree tree, int id)	
	{
		DEPNode node = tree.get(id);
		return node.getHead().getLemma().equals("be") && cogSplit(tree);
	}
	
	private boolean cogSplit(DEPTree tree)	//need better
	{
		if (tree.get(3).isLabel(DEPTagEn.DEP_DET)) return cogEnd(tree);
		else if (tree.get(3).getLemma().equals("on") && tree.get(4).isLabel(DEPTagEn.DEP_DET)) return cogEnd(tree);
		return false;
	}
	
	private boolean cogEnd(DEPTree tree)
	{
		return cognitiveNouns.contains(tree.get(4).getWordForm()) && tree.get(5).getLemma().equals("lemma");
	}
	
	private boolean nonBePattern(DEPTree tree, int id)
	{
		DEPNode node = tree.getFirstRoot();	
		if(node.isPOSTag("VBD") || node.isPOSTag("VBN") && cognitiveVerbs.contains(node.getLemma())) return hasWord(tree, node.getID()+1, "that");
		return false;
	}
}