package edu.emory.clir.clearnlp.coreference.sieve;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.emory.clir.clearnlp.coreference.dictionary.PathSieve;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.type.AttributeType;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSetWithConfidence;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author alexlutz
 * need to be able to check if pronoun is first, second, or third person
 * need to add quotations to the test files 
 */
public class SpeakerIdentification extends AbstractSieve
{
	Set<String> reportingVerbs = init(PathSieve.REPORT_VERBS);
	
	@Override
	public void resolute(List<DEPTree> trees, List<AbstractMention> mentions, DisjointSetWithConfidence mentionLinks)
	{
		AbstractMention curr, prev;
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

	public boolean bothInQuote(AbstractMention prev, AbstractMention curr)
	{
		return prev.hasFeature(AttributeType.QUOTE) && curr.hasFeature(AttributeType.QUOTE);
	}
	
	public boolean oneInQuote(AbstractMention prev, AbstractMention curr)
	{
		return (prev.hasFeature(AttributeType.QUOTE) && !curr.hasFeature(AttributeType.QUOTE)) 
			|| (!prev.hasFeature(AttributeType.QUOTE) && curr.hasFeature(AttributeType.QUOTE));
	}
}
