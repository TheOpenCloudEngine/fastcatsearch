/*
 * Copyright 2013 Websquared, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fastcatsearch.ir.search;

import java.util.List;

import org.apache.lucene.util.BytesRef;
import org.fastcatsearch.ir.query.RowExplanation;

/**
 * Hit리스트를 구성하는 문서번호와 정렬정보 데이터  
 * @author sangwook.song
 *
 */
public class HitElement extends AbstractHitElement<HitElement> {
	
	//bundleKey는 타 노드로 전송할 필요없음. 즉, 한 컬렉션 내에서만 묶고, 컬렉션끼리의 병합시에는 묶지 않음. 
	private BytesRef bundleKey;
	
	private DocIdList bundleDocIdList;
	
	public HitElement(int docNo, int score, List<RowExplanation> list){
		this(-1, docNo, score, null, list);
	}
	public HitElement(int docNo, int score, BytesRef[] dataList, List<RowExplanation> list){
		this(-1, docNo, score, dataList, list);
	}
	//bundle
	public HitElement(int docNo, int score, BytesRef[] dataList, List<RowExplanation> list, BytesRef bundleKey){
		this(-1, docNo, score, dataList, list, bundleKey);
	}
	public HitElement(int segmentSequence, int docNo, int score, BytesRef[] dataList, List<RowExplanation> list){
		super(segmentSequence, docNo, score, dataList, list);
	}
	public HitElement(int segmentSequence, int docNo, int score, BytesRef[] dataList, List<RowExplanation> list, DocIdList bundleDocIdList){
		super(segmentSequence, docNo, score, dataList, list);
		this.bundleDocIdList = bundleDocIdList;
	}
	//bundle
	public HitElement(int segmentSequence, int docNo, int score, BytesRef[] dataList, List<RowExplanation> list, BytesRef bundleKey){
		super(segmentSequence, docNo, score, dataList, list);
		this.bundleKey = bundleKey;
	}
	
	@Override
	public String toString(){
		if(bundleKey != null) {
			return super.toString() + ":" + bundleKey;
		} else {
			return super.toString();
		}
	}
	
	@Override
	public int compareTo(HitElement other) {
		
		//최신세그먼트 우선.
		if(segmentSequence != other.segmentSequence){
			return other.segmentSequence - segmentSequence;
		}
		
		//정렬 데이터가 모두 같다면 문서번호가 최신인걸 보여준다. 
		return other.docNo - docNo;
	}
	
	public void setBundleKey(BytesRef bundleKey) {
		this.bundleKey = bundleKey;
	}

	public BytesRef getBundleKey() {
		return bundleKey;
	}
	
	public void setBundleDocIdList(DocIdList bundleDocIdList) {
		this.bundleDocIdList = bundleDocIdList;
	}
	
	public DocIdList getBundleDocIdList() {
		return bundleDocIdList;
	}
}
