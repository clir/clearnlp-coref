package edu.emory.clir.clearnlp.coreference.sieve;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import edu.emory.clir.clearnlp.coreference.dictionary.PathSieve;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
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
 * need to trace both success and failure 
 * also create version that can be implemented by the parser
 * need to re-look at cognitive noun patterns
 * need to test cases in paper to make sure those are working
 * need to re-evaluate since the mention that I am passing is "it" so I might want to pass in the id of the mention in the tree n_id THIS IS KEY RIGHT NOW
 * might want to make rule it followed by weather verb
 * NEED TO FIX WITH HEAD WORDS SINCE it is really raining DOES NOT HAVE IS AS THE ROOT
 */
public class RuleBasedPleonasticIt
{
	private final Set<String> seasons;
	private final Set<String> cognitiveVerbs;
	private final Set<String> modalAdjectives;
	private final Set<String> cognitiveNouns;
	private final Set<String> weatherTerms;		//need to make sure that I have all of the present-progressive terms
	private final Predicate<String> isAdverb	= x -> POSLibEn.isAdverb(x);
	private final Predicate<String> isAdjective	= x -> POSLibEn.isAdjective(x);
	private final Predicate<String> isVerb		= x -> POSLibEn.isVerb(x);
	private final Predicate<String> isNoun		= x -> POSLibEn.isNoun(x);
	private final Predicate<String> isPronoun	= x -> POSLibEn.isPronoun(x);
	
	
	public RuleBasedPleonasticIt()
	{
		seasons			= DSUtils.toHashSet("summer", "autumn", "fall", "winter", "spring");
		cognitiveVerbs	= DSUtils.toHashSet("recommend", "think", "believe", "know", "anticipate", "assume", "expect");
		modalAdjectives	= DSUtils.toHashSet("necessary", "possible", "certain", "likely", "important", "economical", "easy", "good", "useful", "advisable", 
				"convenient", "sufficient", "desirable", "difficult", "legal","elementary", "painless", "plain", "painful", "simple", "problematic", "hard", 
				"tought", "trying", "preferable", "better", "worse", "criminal", "illegal", "worthless", "worthy");
		cognitiveNouns	= DSUtils.createStringHashSet(IOUtils.createFileInputStream(PathSieve.COGNITIVE_NOUNS));
		weatherTerms	= DSUtils.toHashSet("cloudy", "rainy", "snowy", "sleet", "rain", "snow", "thunder", "cold", "hot", "humid", "freezing", "sunny", 
				"overcast", "hazy", "foggy", "hail", "windy", "sleet", "gusty", "drizzle", "drizzly", "damp", "stormy", "showering", "gust", "storm", 
				"arid", "muggy", "raining", "snowing", "sleeting", "hailing", "drizzling", "storming", "gusting", "thundering");
	}
	
	public void removePleonasticIt(List<AbstractMention> mentions)
	{
		for (AbstractMention mention : mentions) {
			if (mention.getWordFrom().equals("it")) {
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
		return findItBe(tree, id) || specialConstruct(tree, id) || heuristicSeem(tree, id) || cognitiveNounPattern(tree, id); 
	}

	private boolean itBeAdj(DEPTree tree, int i)
	{
		return hasWord(tree, i, "that") || hasPOS(tree, i, isPronoun) || forTo(tree, i);
	}
	
	private boolean specialConstruct(DEPTree tree, int id)
	{
		int i = findPOS(tree, 0, isNoun);
		i = findPOS(tree, ++i, isVerb);
		i = findWord(tree, ++i, "it");
		i = specialConstruct2(tree, ++i);
		return forTo(tree, ++i);
	}
	
	private int specialConstruct2(DEPTree tree, int i)
	{
		int answer;
		if ((answer = findPOS(tree, i, isAdjective)) > 0) return answer;
		else return answer = findContainsWord(tree, i, modalAdjectives);
	}
	
	private boolean findItBe(DEPTree tree, int id)	
	{
		int i = 3, size = tree.size();
		DEPNode node = tree.get(id);
		if (node.getHead().equals("be")) {	
			for (; i < size; i++) {
				node = tree.get(i);
				if 		(isAdverb.test(node.getPOSTag())) continue;
				else if (isAdjective.test(node.getPOSTag())) return itBeAdj(tree, ++i);
				else if (node.isPOSTag("VBD") || node.isPOSTag("VBN")) return hasWord(tree, ++i, "that");
				else if (hasContainsWord(tree, i, seasons)) return true;	
				else if (node.getLemma().equals("time") || node.getLemma().equals("early") || node.getLemma().equals("late")) return true;
				else if (node.isLabel(DEPTagEn.DEP_NUMMOD)) return true;
				return false;
				}
			}
		return false;
	}
	
	private boolean forTo(DEPTree tree, int i)
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
		}
		return false;
	}
	
//	private boolean hasContainsWord(DEPTree tree, int i, Set<String> container)
//	{
//		int a = findContainsWord(tree, i, container);
//		return (a < size) ? true : false;
//	}
	
	private int findContainsWord(DEPTree tree, int i, Set<String> container)
	{
		int size = tree.size();
		for (; i < size; i++){
			DEPNode node = tree.get(i);
			if (isAdverb.test(node.getPOSTag())) continue;
			else if (container.contains(node.getWordForm())) return i;
		}
		return size;
	}
	
	private boolean heuristicSeem(DEPTree tree, int id)	//missing one
	{
		return tree.get(id).getHead().getLemma().equals("seem");
	}
	
	private boolean cognitiveNounPattern(DEPTree tree, int id)	
	{
		DEPNode node = tree.get(id);
		return node.getHead().getWordForm().equals("be") && cogSplit(tree);
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
}