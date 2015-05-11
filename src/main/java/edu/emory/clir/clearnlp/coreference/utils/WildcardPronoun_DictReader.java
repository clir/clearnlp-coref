package edu.emory.clir.clearnlp.coreference.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.emory.clir.clearnlp.coreference.type.PronounType;

public class WildcardPronoun_DictReader{
	private Map<String, PronounType> pronounMap;
	
	public WildcardPronoun_DictReader(String FILEPATH) throws IOException{
		init(new FileInputStream(new File(FILEPATH)));
	}
	
	private void init(FileInputStream input) throws IOException{
		pronounMap = new HashMap<>();
		int  i = 0; String line = "";
		
		while((i = input.read()) != -1){
			if((char)i != '\n') line += (char)i;
			else{
				int index = line.indexOf('\t');
				String name = line.substring(0, index);
				String type = line.substring(index+1);
				pronounMap.put(name, PronounType.valueOf(type));
				line = "";
			}
		}
	}
	
	public Set<String> getStringSet(){
		return pronounMap.keySet();
	}
	public Map<String, PronounType> getPronounMap(){
		return pronounMap;
	}
}
