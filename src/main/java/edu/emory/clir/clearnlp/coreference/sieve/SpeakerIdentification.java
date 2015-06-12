package edu.emory.clir.clearnlp.coreference.sieve;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import edu.emory.clir.clearnlp.coreference.dictionary.PathSieve;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.type.AttributeType;
import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.StringUtils;

/**
 * @author alexlutz
 * @version 1.0
 */
public class SpeakerIdentification extends AbstractSieve{
	
	private final Set<String> reportingVerbs;
	private final Set<String> firstPersonSingularPronouns = DSUtils.toHashSet("i", "me", "my", "mine");
	private final Set<String> firstPersonPluralPronouns	= DSUtils.toHashSet("we", "our", "ours", "us");
	private final Set<String> secondPersonPronouns = DSUtils.toHashSet("you", "your", "yours");
	private final Set<String> thirdPersonSingularPronouns = DSUtils.toHashSet("he", "him", "his", "she", "her", "hers", "it", "its");
	private final Set<String> thirdPersonPluralPronouns = DSUtils.toHashSet("they", "them", "their", "theirs");
	
	public SpeakerIdentification(){
		reportingVerbs = initReportingVerbs();
	}
	
	private Set<String> initReportingVerbs() {
		InputStream in = IOUtils.createFileInputStream(PathSieve.REPORT_VERBS);
		return DSUtils.createStringHashSet(in);
	}
	
	@Override
	public void resolute(List<DEPTree> trees, List<AbstractMention> mentions, CoreferantSet mentionLinks){
		AbstractMention curr, prev;
		int i, j, size = mentions.size();
		
		for(i = size - 1; i > 0; i--){
			curr = mentions.get(i);
			
			for(j = i - 1; j >= 0; j--){
				prev = mentions.get(j);
				
				if(match(prev, curr)){
					if(!mentionLinks.isSameSet(i, j))
						if(curr.isEntityType(EntityType.PERSON) || !curr.hasAttribute(AttributeType.QUOTE))
							mentionLinks.union(i, j);
						else 
							mentionLinks.union(j, i);
							
					break;
				}
			}
		}
	}
	
	@Override
	protected boolean match(AbstractMention prev, AbstractMention curr){
		String prevWord = StringUtils.toLowerCase(prev.getWordFrom()), currWord = StringUtils.toLowerCase(curr.getWordFrom());

		if(curr.matchNumberType(prev) && curr.matchGenderType(prev)){
			
			if(bothInQuote(prev, curr)) {
				if(bothInSameQuote(prev, curr) && (isBothFirstPerson(prevWord, currWord) || isBothThirdPerson(prevWord, currWord)))
					return true;
			}
			else if(oneInQuote(prev, curr)) {
				
				if( (isFirstPerson(prevWord) && reportingVerbs.contains(curr.getHeadNode().getLemma())) ||	// && prev.isEntityType(EntityType.PERSON) ?
					(isFirstPerson(currWord) && reportingVerbs.contains(prev.getHeadNode().getLemma())) )	// && prev.isEntityType(EntityType.PERSON) ?
					return true;
			}
		}
	
		return false;
	}
	
	private boolean bothInQuote(AbstractMention prev, AbstractMention curr){
		return prev.hasAttribute(AttributeType.QUOTE) && curr.hasAttribute(AttributeType.QUOTE);
	}
	
	private boolean bothInSameQuote(AbstractMention prev, AbstractMention curr){
		return prev.getAttribute(AttributeType.QUOTE) == curr.getAttribute(AttributeType.QUOTE);
	}
	
	private boolean oneInQuote(AbstractMention prev, AbstractMention curr){
		return 	(prev.hasAttribute(AttributeType.QUOTE) && !curr.hasAttribute(AttributeType.QUOTE)) || 
				(!prev.hasAttribute(AttributeType.QUOTE) && curr.hasAttribute(AttributeType.QUOTE));
	}
	
	private boolean isFirstPerson(String s){
		return firstPersonSingularPronouns.contains(s) || firstPersonPluralPronouns.contains(s);
	}
	
	private boolean isBothFirstPerson(String prev, String curr){
		return 	(firstPersonSingularPronouns.contains(prev) && firstPersonSingularPronouns.contains(curr)) ||
				(firstPersonPluralPronouns.contains(prev) && firstPersonPluralPronouns.contains(curr));
	}
	
	private boolean isSecondPerson(String s){
		return secondPersonPronouns.contains(s);
	}
	
	private boolean isBothSecondPerson(String prev, String curr){
		return secondPersonPronouns.contains(prev) && secondPersonPronouns.contains(curr);
	}
	
	private boolean isThirdPerson(String s){
		return thirdPersonSingularPronouns.contains(s) || thirdPersonPluralPronouns.contains(s);
	}
	
	private boolean isBothThirdPerson(String prev, String curr){
		return 	(thirdPersonSingularPronouns.contains(prev) && thirdPersonSingularPronouns.contains(curr)) ||
				(thirdPersonPluralPronouns.contains(prev) && thirdPersonPluralPronouns.contains(curr));
	}
	
}