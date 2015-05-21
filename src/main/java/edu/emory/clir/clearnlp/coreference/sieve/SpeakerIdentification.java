package edu.emory.clir.clearnlp.coreference.sieve;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.emory.clir.clearnlp.coreference.dictionary.PathSieve;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.type.AttributeType;
import edu.emory.clir.clearnlp.coreference.type.EntityType;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSetWithConfidence;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author alexlutz
 * @version 1.0
 */
public class SpeakerIdentification extends AbstractSieve
{
	private final Set<String> reportingVerbs = init(PathSieve.REPORT_VERBS);
	private final Set<String> firstPersonSingularPronouns = new HashSet<>(Arrays.asList("I", "me", "my", "mine"));
	private final Set<String> firstPersonPluralPronouns	= new HashSet<>(Arrays.asList("we", "our", "ours", "us"));
	private final Set<String> secondPersonPronouns = new HashSet<>(Arrays.asList("you", "your", "yours"));	//will need later
	private final Set<String> thirdPersonSingularPronouns = new HashSet<>(Arrays.asList("he", "him", "his", "she", "her", "hers", "it", "its"));
	private final Set<String> thirdPersonPluralPronouns = new HashSet<>(Arrays.asList("they", "them", "their", "theirs"));
	
	@Override
	public void resolute(List<DEPTree> trees, List<AbstractMention> mentions, DisjointSetWithConfidence mentionLinks)
	{
		AbstractMention curr, prev;
		int i, j ,size = mentions.size();
		
		for (i = 1;i < size; i++) {
			curr = mentions.get(i);
			
			for (j = i-1; j >= 0; j--){
				prev = mentions.get(j);
				if (match(prev, curr)) {
					mentionLinks.union(i, j, 0); break;
				}
			}
		}
	}
	
	public Set<String> init(String filepath) 
	{
		BufferedReader reader = IOUtils.createBufferedReader(filepath);
		Set<String> reportingVerbs = new HashSet<>();
		String line;
		
		try {
			while((line = reader.readLine()) != null)
				reportingVerbs.add(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return reportingVerbs;
	}
	
	private boolean match(AbstractMention prev, AbstractMention curr)
	{
		if (bothInQuote(prev, curr)) {
			if (firstPersonSingularPronouns.contains(prev.getWordFrom()) && firstPersonPluralPronouns.contains(curr.getWordFrom()) 
					|| thirdPersonSingularPronouns.contains(prev.getWordFrom()) && thirdPersonPluralPronouns.contains(curr.getWordFrom())) {
				return true;
			}
		}
		else if (oneInQuote(prev, curr)) {
			if (firstPersonSingularPronouns.contains(prev.getWordFrom()) && (firstPersonSingularPronouns.contains(curr.getWordFrom()) || curr.isEntityType(EntityType.PERSON) && reportingVerbs.contains(curr.getHeadNodeWordForm()))) {
				return true;
			}
		}
		return false;
	}
	
	public boolean bothInQuote(AbstractMention prev, AbstractMention curr)
	{
		return prev.hasFeature(AttributeType.QUOTE) && curr.hasFeature(AttributeType.QUOTE);
	}
	
	public boolean oneInQuote(AbstractMention prev, AbstractMention curr)
	{
		return prev.hasFeature(AttributeType.QUOTE) && !curr.hasFeature(AttributeType.QUOTE) || !prev.hasFeature(AttributeType.QUOTE) && curr.hasFeature(AttributeType.QUOTE);
	}
}