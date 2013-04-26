package org.fastcatsearch.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fastcatsearch.util.JSONPResultStringer;
import org.fastcatsearch.util.JSONResultStringer;
import org.fastcatsearch.util.ResultStringer;
import org.fastcatsearch.util.XMLResultStringer;

public class AbstractSearchServlet extends JobHttpServlet {

	private static final long serialVersionUID = 4165287616751500253L;
	
	String requestCharset;
	String responseCharset;
	String isAdmin;
	int timeout;
	
	String collectionName;
	String fields;
	String searchCondition;
	String groupFields;
	String groupCondition;
	String groupFilter;
	String sortFields;
	String filterFields;
	String startNumber;
	String resultLength;
	String highlightTags;
	String searchOption;
	String userData;
	String jsonCallback;
	
	public AbstractSearchServlet(int resultType) {
		super(resultType);
	}

	public void writeResult(HttpServletResponse response, String charset, ResultStringer stringer) {
		try {
			if(stringer instanceof JSONResultStringer) {
	    		response.setContentType("application/json; charset="+charset);
			} else if(stringer instanceof JSONPResultStringer) {
	    		response.setContentType("application/json; charset="+charset);
			} else if(stringer instanceof XMLResultStringer) {
	    		response.setContentType("text/xml; charset="+charset);
			}
			response.getWriter().write(stringer.toString());
		} catch (IOException e) {
		} finally {
		}
	}
	
	public void prepare(HttpServletRequest request) {
    	requestCharset = request.getParameter("requestCharset");
    	responseCharset = request.getParameter("responseCharset");
    	
    	if(requestCharset == null) {
    		requestCharset = "UTF-8";
    	}
    	
    	if(responseCharset == null) {
    		responseCharset = "UTF-8";
    	}
    	
    	String timeoutStr = request.getParameter("timeout");
    	timeout = 5;
    	if(timeoutStr != null) {
    		timeout = Integer.parseInt(timeoutStr);
    	}
    	
    	isAdmin = request.getParameter("admin");
    	
    	collectionName = request.getParameter("cn");
    	fields = request.getParameter("fl");
    	searchCondition = request.getParameter("se");
    	groupFields = request.getParameter("gr");
    	groupCondition = request.getParameter("gc");
    	groupFilter = request.getParameter("gf");
    	sortFields = request.getParameter("ra");
    	filterFields = request.getParameter("ft");
    	startNumber = request.getParameter("sn");
    	resultLength = request.getParameter("ln");
    	highlightTags = request.getParameter("ht");
    	searchOption = request.getParameter("so");
    	userData = request.getParameter("ud");
    	
    	jsonCallback = request.getParameter("jsoncallback");
	}
	
	public String queryString() {
    	try {
			return "cn="+collectionName+
				"&fl="+URLDecoder.decode(fields,requestCharset)+
				"&se="+URLDecoder.decode(searchCondition,requestCharset)+
				"&gr="+URLDecoder.decode(groupFields,requestCharset)+
				"&gc="+URLDecoder.decode(groupCondition,requestCharset)+
				"&gf="+URLDecoder.decode(groupFilter,requestCharset)+
				"&ra="+URLDecoder.decode(sortFields,requestCharset)+
				"&ft="+URLDecoder.decode(filterFields,requestCharset)+
				"&sn="+URLDecoder.decode(startNumber,requestCharset)+
				"&ln="+URLDecoder.decode(resultLength,requestCharset)+
				"&ht="+URLDecoder.decode(highlightTags,requestCharset)+
				"&so="+URLDecoder.decode(searchOption,requestCharset)+
				"&ud="+URLDecoder.decode(userData,requestCharset);
		} catch (UnsupportedEncodingException e) {
			logger.error("",e);
		}
    	return "";
	}
	
	public ResultStringer getResultStringer() {
		ResultStringer rStringer = null;
		if(resultType == JSON_TYPE) {
			rStringer = new JSONResultStringer();
		} else if(resultType == JSONP_TYPE) {
			rStringer = new JSONPResultStringer(jsonCallback);
		} else if(resultType == XML_TYPE) {
			rStringer = new XMLResultStringer("fastcat",true);
		}
		return rStringer;
	}
}
