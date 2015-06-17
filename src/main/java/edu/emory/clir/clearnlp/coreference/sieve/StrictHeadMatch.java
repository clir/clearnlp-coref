package edu.emory.clir.clearnlp.coreference.sieve;

import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.coreference.dictionary.PathSieve;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.StringUtils;

/**
 * @author alexlutz
 * @version 1.0
 * need to change this 
 */
public class StrictHeadMatch extends AbstractSieve {
	
	private Set<String> s_stopwords;
	
	public StrictHeadMatch() {
		super();
		s_stopwords = initStopWords();
	}
	
	private Set<String> initStopWords(){
		InputStream in = IOUtils.createFileInputStream(PathSieve.ENG_STOPWORDS);
		return DSUtils.createStringHashSet(in);
	}
	
	@Override
	public boolean match(AbstractMention prev, AbstractMention curr){
		if(!prev.isMultipleMention() && !curr.isMultipleMention())
			return 	!prev.isParentMention(curr) &&			// Not i-within i condition 
					(matchWordInclusion(prev, curr));
		return false;
	}
	
	/* This will mathch everything */
//	private boolean matchHeadStrings(AbstractMention prev, AbstractMention curr){
//		return DSUtils.hasIntersection(prev.getAncestorWords(), curr.getAncestorWords());
//	}
	
	private boolean matchWordInclusion(AbstractMention prev, AbstractMention curr){		
		Set<String> prev_words = prev.getSubTreeWordList().stream().map(word -> StringUtils.toLowerCase(word)).filter(word -> !s_stopwords.contains(word)).collect(Collectors.toSet()),
				curr_words = curr.getSubTreeWordList().stream().map(word -> StringUtils.toLowerCase(word)).filter(word -> !s_stopwords.contains(word)).collect(Collectors.toSet());
		return (!prev_words.isEmpty() && !curr_words.isEmpty()) && prev_words.containsAll(curr_words);
	}
	
	/* Additional rules need for specification (ie. The happy cute "couple" is happy with the happy cute "dogs".)  */
//	private boolean mathchModifiers(AbstractMention prev, AbstractMention curr){
//		return prev.getModifiers().containsAll(curr.getModifiers());
//	}
}
