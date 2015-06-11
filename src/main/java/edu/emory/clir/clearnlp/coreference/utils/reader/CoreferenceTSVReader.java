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
package edu.emory.clir.clearnlp.coreference.utils.reader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import edu.emory.clir.clearnlp.collection.pair.IntIntPair;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.collection.pair.Pair;
import edu.emory.clir.clearnlp.coreference.config.MentionConfiguration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.mention.detector.AbstractMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.detector.EnglishMentionDetector;
import edu.emory.clir.clearnlp.coreference.utils.structures.DisjointSet;
import edu.emory.clir.clearnlp.coreference.utils.structures.Tuple;
import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.reader.TSVReader;
import edu.emory.clir.clearnlp.util.CharTokenizer;
import edu.emory.clir.clearnlp.util.constant.CharConst;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @author 	Yu-Hsin(Henry) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Jun 9, 2015
 */
public class CoreferenceTSVReader extends TSVReader{
	private int i_corefLink;
	private AbstractMentionDetector m_detector;
	public CharTokenizer T_PIPE  = new CharTokenizer('|');
	
	public CoreferenceTSVReader(int iID, int iForm, int iLemma, int iPOSTag, int iNERTag, int iFeats, int iHeadID, int iDeprel, int iXHeads, int iSHeads, int iCorefLink) {
		super(iID, iForm, iLemma, iPOSTag, iNERTag, iFeats, iHeadID, iDeprel, iXHeads, iSHeads);
		i_corefLink = iCorefLink;
		m_detector = new EnglishMentionDetector(new MentionConfiguration(true, true, true));
	}

	public Tuple<List<DEPTree>, List<AbstractMention>, DisjointSet> getCoNLLDocument(){
		
		int i;
		DEPTree tree;
		List<String[]> lines;
		Map<Integer, List<IntIntPair>> mentionRanges;
		Map<Integer, List<ObjectIntPair<IntIntPair>>> clusters = new HashMap<>();
		
		List<DEPTree> trees = new ArrayList<>();
		try {
			while( (lines = readLines()) != null){
				// Tree construction
				tree = getDEPTree(lines);
				
				// Mention identification ( clusterId -> treeId(beginIndex, endIndex) )
				mentionRanges = getCoNLLCorefMentionRange(lines);
				for(Entry<Integer, List<IntIntPair>> e : mentionRanges.entrySet()){
					clusters.computeIfAbsent(e.getKey(), ArrayList::new)
							.addAll(e.getValue().stream().map(pair -> new ObjectIntPair<>(pair, trees.size())).collect(Collectors.toList()));
				}
				
				trees.add(tree);
			}
			
		} catch (Exception e) { e.printStackTrace(); }
		
		// Mention creation
		List<AbstractMention> mentions = m_detector.getMentionList(trees);
		
		// Coreferant relation construction
		List<Integer> m_indices;
		DisjointSet links = new DisjointSet(mentions.size(), false);
		
		// ~~Strict mention matching
		AbstractMention mention;
		int size = mentions.size();
		Map<DEPNode, Integer> m_singleMentionId = new HashMap<>();
		Map<Set<DEPNode>, Integer> m_multiMetnionId = new HashMap<>(); 
		for(i = 0; i < size; i++){
			mention = mentions.get(i);
			
			if(mention.isMultipleMention())	m_multiMetnionId.put(mention.getSubMentions().stream().map(m -> m.getNode()).collect(Collectors.toSet()), i);
			else							m_singleMentionId.put(mention.getNode(), i);
		}
				
		for(List<ObjectIntPair<IntIntPair>> cluster : clusters.values()){
			m_indices = getMentionIndices(trees, m_singleMentionId, m_multiMetnionId, cluster);			
			for(i = m_indices.size() - 1; i > 0; i--)
				links.union(m_indices.get(i-1), m_indices.get(i));
		}
		
		/* Debug console printer
		 * // Print clusters
		 * for(Entry<Integer, List<ObjectIntPair<IntIntPair>>> e : clusters.entrySet())
		 * System.out.println(e.getKey() + "\t->\t" + Joiner.join(e.getValue().stream().map(pair -> "["+pair.i+"."+pair.o.i1+"-"+pair.i+"."+pair.o.i2+"]").collect(Collectors.toList()), ", "));
		 * // Print mentions
		 * for(AbstractMention m : mentions)	System.out.println(m);
		 * // Print links
		 * System.out.println(links);
		 */
		
		return new Tuple<>(trees, mentions, links);
	}
	
	public Pair<List<DEPTree>, Map<Integer, List<ObjectIntPair<IntIntPair>>>> getCoNLLDocumentMentionSpan(){

		DEPTree tree;
		List<String[]> lines;
		Map<Integer, List<IntIntPair>> mentionRanges;
		Map<Integer, List<ObjectIntPair<IntIntPair>>> clusters = new HashMap<>();
		
		List<DEPTree> trees = new ArrayList<>();
		try {
			while( (lines = readLines()) != null){
				// Tree construction
				tree = getDEPTree(lines);
				
				// Mention identification (clusterId -> treeId(beginIndex, endIndex) )
				mentionRanges = getCoNLLCorefMentionRange(lines);
				for(Entry<Integer, List<IntIntPair>> e : mentionRanges.entrySet()){
					clusters.computeIfAbsent(e.getKey(), ArrayList::new)
							.addAll(e.getValue().stream().map(pair -> new ObjectIntPair<>(pair, trees.size())).collect(Collectors.toList()));
				}
				
				trees.add(tree);
			}
			
		} catch (Exception e) { e.printStackTrace(); }
		
		return new Pair<>(trees, clusters);
	}
	
	private Map<Integer, List<IntIntPair>> getCoNLLCorefMentionRange(List<String[]> lines){
		Map<Integer, List<IntIntPair>> map = new HashMap<>();
		
		String corefAnnotation;
		String[] clusterAnnotations;
		int i, c_index, size = lines.size();
		
		for(i = 0; i < size; i++){
			corefAnnotation = lines.get(i)[i_corefLink];
			
			if(!corefAnnotation.equals(StringConst.HYPHEN)){
				clusterAnnotations = T_PIPE.tokenize(corefAnnotation);
				
				for(String clusterAnnotation : clusterAnnotations){
					if(clusterAnnotation.indexOf(CharConst.LRB) >= 0){
						if(clusterAnnotation.indexOf(CharConst.RRB) >= 0){
							c_index = Integer.parseInt(clusterAnnotation.substring(1, clusterAnnotation.length()-1));
							map.computeIfAbsent(c_index, ArrayList::new).add(new IntIntPair(i+1, i+2));
						}
						else{
							c_index = Integer.parseInt(clusterAnnotation.substring(1));
							map.computeIfAbsent(c_index, ArrayList::new).add(new IntIntPair(i+1, -1));
						}
					}
					else if(clusterAnnotation.indexOf(CharConst.RRB) >= 0){
						c_index = Integer.parseInt(clusterAnnotation.substring(0, clusterAnnotation.length()-1));
						for(IntIntPair pair : map.get(c_index))	if(pair.i2 < 0) pair.i2 = i+2;
					}
				}
			}
		}
		
		return map;
	}
	
	private List<Integer> getMentionIndices(List<DEPTree> trees, Map<DEPNode, Integer> singleMetnionId, Map<Set<DEPNode>, Integer> multiMentionId, List<ObjectIntPair<IntIntPair>> cluster){
		Set<Integer> indices = new HashSet<>();
		
		// Strict mention matching
		int i;
		Integer index;
		Set<Integer> corferentId;
		DEPTree tree; DEPNode node;
		Set<DEPNode> mentionNodes;
		for(ObjectIntPair<IntIntPair> coreferent : cluster){
			tree = trees.get(coreferent.i);
			corferentId = new HashSet<>();
			mentionNodes = new HashSet<>();
			
			for(i = coreferent.o.i1; i < coreferent.o.i2; i++){
				node = tree.get(i);
				if( (index = singleMetnionId.get(node)) != null ){
					corferentId.add(index); mentionNodes.add(node);
				}
			}
			
			if( (index = multiMentionId.get(mentionNodes)) != null)	indices.add(index);
			else 													indices.addAll(corferentId);
		}
		
		List<Integer> list = new ArrayList<>(indices);
		Collections.sort(list);
		return list;
	}
}
