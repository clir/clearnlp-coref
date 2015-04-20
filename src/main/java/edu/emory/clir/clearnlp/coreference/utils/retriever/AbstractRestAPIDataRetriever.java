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
package edu.emory.clir.clearnlp.coreference.utils.retriever;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import edu.emory.clir.clearnlp.coreference.utils.structures.ParameterPair;
import edu.emory.clir.clearnlp.coreference.utils.util.CoreferenceStringUtils;
import edu.emory.clir.clearnlp.util.Joiner;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Apr 19, 2015
 */
abstract public class AbstractRestAPIDataRetriever extends AbstractDataRetriever{
	
	protected String API_URL;
	static protected String charset = "UTF-8";
	
	public AbstractRestAPIDataRetriever(String url){
		API_URL = url;
	}
	
	protected String REST_GetRequest(String queryString, ParameterPair... parameters) throws Exception{
		// Generating queryURL
		String queryURL = CoreferenceStringUtils.connectStrings(API_URL, "/", queryString, "?", getParameterString(parameters));
		// HTTP Connection
		URLConnection connection = new URL(queryURL).openConnection();
		connection.setRequestProperty("Accept-Charset", charset);
		InputStream response = connection.getInputStream();
		return response.toString();
	}
	
	protected String REST_PostRequest(String queryString, ParameterPair... parameters) throws Exception{
		// Generating queryURL
		String queryURL = CoreferenceStringUtils.connectStrings(API_URL, "/", queryString);
		// HTTP Connection
		URLConnection connection = new URL(queryURL).openConnection();
		connection.setDoOutput(true); 	// Triggers POST
		connection.setRequestProperty("Accept-Charset", charset);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
		
		try (OutputStream output = connection.getOutputStream()) {
		    output.write(getParameterString(parameters).getBytes(charset));
		}
		
		InputStream response = connection.getInputStream();
		return response.toString();
	}
	
	private String getParameterString(ParameterPair... parameters) throws Exception{
		for(ParameterPair parameter : parameters)	parameter.setValue(URLEncoder.encode(parameter.getValue(), charset));
		return Joiner.join(parameters, "&");
	}
}
