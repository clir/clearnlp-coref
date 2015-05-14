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
package edu.emory.clir.clearnlp.coreference.utils.structures;

import edu.emory.clir.clearnlp.coreference.utils.util.CoreferenceStringUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Apr 19, 2015
 */
public class ParameterPair extends StringStringPair{

	public ParameterPair(String n, String v){	super(n, v); }
	
	public String getName(){	return s1; }
	public String getValue(){	return s2; }
	
	public void setName(String n){	s1 = n; }
	public void setValue(String v){	s2 = v; }
	
	@Override
	public String toString(){	return CoreferenceStringUtils.connectStrings(s1, "=", s2); }
}
