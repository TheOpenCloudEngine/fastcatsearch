package org.fastcatsearch.servlet;

import java.io.IOException;

import org.fastcatsearch.util.ResponseWriter;
import org.fastcatsearch.util.ResultWriterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 검색결과를 ResultWriter에 기록하는 클래스.
 * */
public abstract class AbstractSearchResultWriter {
	protected static Logger logger = LoggerFactory.getLogger(AbstractSearchResultWriter.class);
	protected ResponseWriter resultWriter;
	
	public AbstractSearchResultWriter(ResponseWriter resultStringer){
		this.resultWriter = resultStringer;
	}
	public abstract void writeResult(Object obj, long searchTime, boolean isSuccess) throws ResultWriterException, IOException;
}
