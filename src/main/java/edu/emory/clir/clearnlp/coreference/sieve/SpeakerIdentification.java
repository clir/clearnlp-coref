package edu.emory.clir.clearnlp.coreference.sieve;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.emory.clir.clearnlp.coreference.dictionary.PathSieve;
import edu.emory.clir.clearnlp.coreference.mention.SingleMention;
import edu.emory.clir.clearnlp.coreference.type.AttributeType;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSetWithConfidence;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author alexlutz
 * need to fix matches methods
 */
public class SpeakerIdentification extends AbstractSieve
{
	Set<String> reportingVerbs = init(PathSieve.REPORT_VERBS);
	Set<String> firstPersonSingularPronouns = new HashSet<>(Arrays.asList("I", "me", "my", "mine"));
	Set<String> firstPersonPluralPronouns	= new HashSet<>(Arrays.asList("we", "our", "ours", "us"));
	
	@Override
	public void resolute(List<DEPTree> trees, List<SingleMention> mentions,
			DisjointSetWithConfidence mentionLinks)
	{
		SingleMention curr, prev;
		int i, j ,size = mentions.size();
		
		for (i = 1;i < size; i++) {
			curr = mentions.get(i);
			
			for (j = i-1; j >= 0; j--){
				prev = mentions.get(j);
				if (true) {
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

	public boolean bothInQuote(SingleMention prev, SingleMention curr)
	{
		return prev.hasFeature(AttributeType.QUOTE) && curr.hasFeature(AttributeType.QUOTE);
	}
	
	public boolean oneInQuote(SingleMention prev, SingleMention curr)
	{
		return prev.hasFeature(AttributeType.QUOTE) && !curr.hasFeature(AttributeType.QUOTE) || !prev.hasFeature(AttributeType.QUOTE) && curr.hasFeature(AttributeType.QUOTE);
	}
}
