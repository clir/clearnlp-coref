package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import edu.emory.clir.clearnlp.coreference.dictionary.PathSieve;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.type.AttributeType;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTagEn;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.pos.POSLibEn;
import edu.emory.clir.clearnlp.pos.POSTagEn;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.StringUtils;

/**
 * @author alexlutz
 * @version 1.0
 * @since	6/10/15
 * want to provide more support with questions
 * want to possibly ignore prepositional phrases in addition to adverbs
 * maybe add idioms
 */
public class RuleBasedPleonasticIt
{
	private DEPTree tree;
	private final Set<String> seasons;
	private final Set<String> cognitiveVerbs;
	private final Set<String> cognitiveNouns;
	private final Set<String> weatherTerms;		
	private final Set<String> timeWords;
	private final Predicate<String> isAdverb	= x -> POSLibEn.isAdverb(x);
	private final Predicate<String> isAdjective	= x -> POSLibEn.isAdjective(x);
	private final Predicate<String> isVerb		= x -> POSLibEn.isVerb(x);
	private final Predicate<String> isNoun		= x -> POSLibEn.isNoun(x);
	private final Predicate<String> isPronoun	= x -> POSLibEn.isPronoun(x);
	
	
	public RuleBasedPleonasticIt()
	{
		seasons			= DSUtils.toHashSet("summer", "autumn", "fall", "winter", "spring");
		cognitiveVerbs	= DSUtils.createStringHashSet(IOUtils.createFileInputStream(PathSieve.COGNITIVE_VERBS));//only used in one thing might want to remove
		cognitiveNouns	= DSUtils.createStringHashSet(IOUtils.createFileInputStream(PathSieve.COGNITIVE_NOUNS));
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
		tree = mention.getTree();
		int id = mention.getNode().getID();
		return findItBe(id) || specialConstruct(id) || heuristicSeem(id) || cognitiveNounPattern(id) || nonBePattern(id) || auxIs(id); 
	}

	private boolean itBeAdj(int i)
	{
		return hasWord(i, "that") || hasPOS(i, isPronoun) || forTo(i) || hasPOSByTag(i, POSTagEn.POS_TO);
	}
	
	private boolean specialConstruct(int id)
	{
		int i = findPOS(1, isNoun);
		i = findPOS(++i, isVerb);
		i = findWord(++i, "it");
		i = findPOS(++i, isAdjective);
//		return forTo(i+1) || hasPOSByTag(i+1, POSTagEn.POS_TO);	//this does not match with the patterns that he gives but matches with his examples
		return (i < tree.size()) ? true : false;
	}
	
	private boolean findItBe(int id)	
	{
		int i = 3, size = tree.size();
		DEPNode node = tree.get(id);
		if (node.getHead().getLemma().equals("be")) {	
			for (; i < size; i++) {
				node = tree.get(i);
				if 		(isAdverb.test(node.getPOSTag())) continue;
				else if (weatherTerms.contains(node.getLemma())) return true;
				else if (timeWords.contains(node.getLemma())) return true;
				else if (seasons.contains(node.getLemma())) return true;	
				else if (node.isLabel(DEPTagEn.DEP_NUMMOD)) return true;
				else if (isAdjective.test(node.getPOSTag())) return itBeAdj(++i);
				return false;
				}
			}
		return false;
	}
	
	private boolean forTo(int i)	//need to make the for optional
	{
		i = findWordWithPOSTag(i, "for", POSTagEn.POS_IN);
		return (i < tree.size()) ?  hasPOSByTag(i, POSTagEn.POS_TO) : false;
	}
	
	private boolean hasPOS(int i, Predicate<String> f1)
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
	
	private int findPOS(int i, Predicate<String> f1)
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
	
	private boolean hasPOSByTag(int i, String pos)
	{
		int size = tree.size();
		for (; i < size; i++) {
			if		(isAdverb.test(tree.get(i).getPOSTag())) continue;
			else if (tree.get(i).isPOSTag(pos)) return true;
			return false;
		}
		return false;
	}
	
	private int findPOSByTag(int i, String pos)
	{
		int size = tree.size();
		for (; i < size; i++) {
			if		(isAdverb.test(tree.get(i).getPOSTag())) continue;
			else if (tree.get(i).isPOSTag(pos)) return i;
			return size;
		}
		return size;
	}
	
	private boolean hasWord(int i, String word)
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
	
	private int findWord(int i, String word)
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
	
//	private boolean hasWordWithPOSTag(int i, String word, String pos)
//	{
//		int size = tree.size();
//		for (; i < size; i++) {
//			DEPNode node = tree.get(i);
//			if (isAdverb.test(node.getPOSTag())) continue;
//			else if (node.getLemma().equals(word) && node.isPOSTag(pos)) return true;
//		}
//		return false;
//	}
	
	private int findWordWithPOSTag(int i, String word, String pos)
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
	
	private int findContainsWord(int i, Set<String> container)
	{
		int size = tree.size();
		for (; i < size; i++){
			DEPNode node = tree.get(i);
			if (isAdverb.test(node.getPOSTag())) continue;
			else if (container.contains(node.getWordForm())) return i;
			return size;
		}
		return size;
	}
	
	private boolean heuristicSeem(int id)	
	{
		return tree.get(id).getHead().getLemma().equals("seem") && !hasPOSByTag(tree.getFirstRoot().getID(), POSTagEn.POS_TO);
	}
	
	private boolean cognitiveNounPattern(int id)	
	{
		DEPNode node = tree.get(id);
		return node.getHead().getWordForm().equals("is") && cogSplit(node.getHead().getID());
	}
	
	private boolean cogSplit(int i)	
	{
		int answer = findWord(i+1, "on");
		answer = findPOSByTag(++answer, POSTagEn.POS_DT);
		if (answer < tree.size()) return cogEnd(answer);
		else {
			answer = findPOSByTag(i+1, POSTagEn.POS_DT);
			return cogEnd(answer);
		}
	}
	
	private boolean cogEnd(int i)
	{
		i = findContainsWord(++i, cognitiveNouns);
		return hasWord(++i, "that");
	}
	
	private boolean nonBePattern(int id)
	{
		DEPNode node = tree.get(id).getHead();	
		if(node.isPOSTag("VBD") || node.isPOSTag("VBN") && cognitiveVerbs.contains(node.getLemma())) return hasWord(node.getID()+1, "that");
		return false;
	}
	
	private boolean auxIs(int id)
	{
		int size = tree.size();
		int i = findWord(id+1, "be");
		for (; i < size; i++) {
			DEPNode node = tree.get(++i);
			if (isAdverb.test(node.getPOSTag())) continue;
			else if (weatherTerms.contains(StringUtils.toLowerCase(node.getWordForm()))) return true;
			return false;
		}
		return false;
	}
}