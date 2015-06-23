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
import java.util.Collection;
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
import edu.emory.clir.clearnlp.collection.triple.Triple;
import edu.emory.clir.clearnlp.coreference.config.MentionConfiguration;
import edu.emory.clir.clearnlp.coreference.mention.AbstractMention;
import edu.emory.clir.clearnlp.coreference.mention.detector.AbstractMentionDetector;
import edu.emory.clir.clearnlp.coreference.mention.detector.EnglishMentionDetector;
import edu.emory.clir.clearnlp.coreference.utils.structures.CoreferantSet;
import edu.emory.clir.clearnlp.coreference.utils.util.CoreferenceDSUtils;
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
	private boolean toDisjointClusters, filterCorpus;
	private AbstractMentionDetector m_detector;
	public CharTokenizer T_PIPE  = new CharTokenizer('|');
	
	public CoreferenceTSVReader(boolean filterCorpus, boolean toDisjointClusters, int iID, int iForm, int iLemma, int iPOSTag, int iNERTag, int iFeats, int iHeadID, int iDeprel, int iXHeads, int iSHeads, int iCorefLink){
		super(iID, iForm, iLemma, iPOSTag, iNERTag, iFeats, iHeadID, iDeprel, iXHeads, iSHeads);
		i_corefLink = iCorefLink;
		this.filterCorpus = filterCorpus;
		this.toDisjointClusters = toDisjointClusters;
		setMentionDetector(new MentionConfiguration(true, true, true));
	}
	
	public CoreferenceTSVReader(MentionConfiguration m_config, boolean filterCorpus, boolean toDisjointClusters, int iID, int iForm, int iLemma, int iPOSTag, int iNERTag, int iFeats, int iHeadID, int iDeprel, int iXHeads, int iSHeads, int iCorefLink){
		super(iID, iForm, iLemma, iPOSTag, iNERTag, iFeats, iHeadID, iDeprel, iXHeads, iSHeads);
		i_corefLink = iCorefLink;
		this.filterCorpus = filterCorpus;
		this.toDisjointClusters = toDisjointClusters;
		setMentionDetector(m_config);
	}
	
	public void setMentionDetector(MentionConfiguration config){
		m_detector = new EnglishMentionDetector(config);
	}
	
	public Pair<List<DEPTree>, List<AbstractMention>> getCoNLLDocument(){
		DEPTree tree;
		List<AbstractMention> mentions;
		List<DEPTree> trees = new ArrayList<>();
		
		while( (tree = next()) != null)	trees.add(tree);
		mentions = m_detector.getMentionList(trees);
		
		return new Pair<>(trees, mentions);
	}

	public Triple<List<DEPTree>, List<AbstractMention>, CoreferantSet> getGoldCoNLLDocument(){
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
		CoreferantSet links = new CoreferantSet(mentions.size(), false, false);
		List<List<Integer>> l_mentionIndices = getMentionIndices(trees, mentions, clusters.values());
		
		// Corpus filtration
		if(filterCorpus){
			CoreferenceDSUtils.sortListBySublistSizeThenHead(l_mentionIndices, false);
			filterOverlappingMentions(mentions, l_mentionIndices);
			l_mentionIndices = l_mentionIndices.stream().filter(cluster -> cluster.size() > 0).collect(Collectors.toList());
		}
		
		links.addClusters(l_mentionIndices, toDisjointClusters);
		
		return new Triple<>(trees, mentions, links);
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
	
	private List<List<Integer>> getMentionIndices(List<DEPTree> trees, List<AbstractMention> mentions, Collection<List<ObjectIntPair<IntIntPair>>> clusters){
		List<List<Integer>> clusterIndices = new ArrayList<>();
		
		// ~~Strict mention matching (mention span DEPNode map constructions)
		AbstractMention mention;
		int i, j, size = mentions.size();
		Map<DEPNode, Integer> m_singleMentionId = new HashMap<>();
		Map<Set<DEPNode>, Integer> m_multiMetnionId = new HashMap<>(); 
		for(i = 0; i < size; i++){
			mention = mentions.get(i);
			if(mention.isMultipleMention())	m_multiMetnionId.put(mention.getSubMentions().stream().map(m -> m.getNode()).collect(Collectors.toSet()), i);
			else							m_singleMentionId.put(mention.getNode(), i);
		}
		
		Integer index;
		List<Integer> m_indices;
		Set<Integer> corferentId;
		Set<DEPNode> mentionNodes;
		DEPTree tree; DEPNode node;
		
		for(List<ObjectIntPair<IntIntPair>> cluster : clusters){
			m_indices = new ArrayList<>();
			
			for(ObjectIntPair<IntIntPair> coreferent : cluster){
				tree = trees.get(coreferent.i);
				corferentId = new HashSet<>();
				mentionNodes = new HashSet<>();
				
				for(i = coreferent.o.i1; i < coreferent.o.i2; i++){
					node = tree.get(i);
					if( (index = m_singleMentionId.get(node)) != null ){
						corferentId.add(index); mentionNodes.add(node);
					}
				}
				
				if( (index = m_multiMetnionId.get(mentionNodes)) != null)	m_indices.add(index);
				else 														m_indices.addAll(corferentId);
			}
			
			if(!m_indices.isEmpty()){
				Collections.sort(m_indices);
				
				// Filter out childMentions
				int prevId, currId;
				size = m_indices.size();
				for(i = size - 1; i > 0 ; i--){
					currId = m_indices.get(i);
					for(j = i - 1; j >= 0; j--){
						prevId = m_indices.get(j);
						if(mentions.get(prevId).isParentMention(mentions.get(currId))){
							m_indices.remove(i);
							break;
						}
					}
				}
				clusterIndices.add(m_indices);
			}
		}
		
		return clusterIndices;
	}
	
	private void filterOverlappingMentions(List<AbstractMention> mentions, List<List<Integer>> clusters){
		List<Integer> cluster1;
		int i, j, size = clusters.size();
		
		for(i = 0; i < size - 1; i++){
			cluster1 = clusters.get(i);
			for(j = i + 1; j < size; j++)
				filterOverlappingMentions_Aux(mentions, cluster1, clusters.get(j));
		}
	}
	
	private void filterOverlappingMentions_Aux(List<AbstractMention> mentions, List<Integer> cluster1, List<Integer> cluster2){
		int i, j, index1, size1 = cluster1.size(), size2;
		
		for(i = 0; i < size1; i++){
			index1 = cluster1.get(i);
			size2 = cluster2.size();
			for(j = size2 - 1; j >= 0; j--){
				if(index1 == cluster2.get(j))
					cluster2.remove(j);
			}
		}
	}
}
