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
package edu.emory.clir.clearnlp.relation.parameter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.emory.clir.clearnlp.util.IOUtils;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jul 13, 2015
 */
public class MultiThreadParameterSearcher {
	public static int THREAD_COUNT = 40;

	public static void main(String[] args){
		final double CUTOFF = 0.45d, GAP = 0.05d;
		final double PARA_INIT = 0d, PARA_CAP = 1.01d, PARA_INCRE = 0.05d;
	
		int threadNum = 1;
		double frequencyStart = PARA_INIT, frequencyEnd = PARA_INIT;
		double entityStart = 0d, entityEnd = entityStart + PARA_INCRE;
		
		ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
		for(; entityEnd <= PARA_CAP; entityStart = entityEnd, entityEnd += PARA_INCRE){
			executor.submit(new MainEntityExtractorParamenterSearch(CUTOFF, GAP, IOUtils.createBufferedPrintStream("/home/henryyhc/output/thread" + threadNum++ + ".output"), 0.70d, 0.70d, entityStart, entityEnd, 0d, 0.5d, PARA_INCRE));
			executor.submit(new MainEntityExtractorParamenterSearch(CUTOFF, GAP, IOUtils.createBufferedPrintStream("/home/henryyhc/output/thread" + threadNum++ + ".output"), 0.70d, 0.70d, entityStart, entityEnd, 0.5d, 1d, PARA_INCRE));
		}
		executor.shutdown();
	}
}
