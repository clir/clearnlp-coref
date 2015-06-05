/**
 * Copyright 2015, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.clir.clearnlp.dialogue.util.parser;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.emory.clir.clearnlp.dialogue.structure.Season;
import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 5, 2015
 */
public class JSONDialogueReader {
	private Gson gson;
	private Type type;
	
	public JSONDialogueReader(){
		gson = new Gson();
		type = new TypeToken<Map<Integer, Season>>(){}.getType();
	}
	
	public Map<Integer, Season> read(String filePath) throws IOException{
		return gson.fromJson(IOUtils.createBufferedReader(filePath), type);
	}
	
	public Map<Integer, Season> read(InputStream in){
		return gson.fromJson(IOUtils.createBufferedReader(in), type);
	}
}
