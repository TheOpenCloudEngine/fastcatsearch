package org.fastcatsearch.http.action.management.collections;

import java.util.ArrayList;
import java.util.List;

import org.fastcatsearch.http.ActionAuthority;
import org.fastcatsearch.http.ActionAuthorityLevel;
import org.fastcatsearch.http.ActionMapping;
import org.fastcatsearch.http.action.ActionRequest;
import org.fastcatsearch.http.action.ActionResponse;
import org.fastcatsearch.http.action.AuthAction;
import org.fastcatsearch.ir.IRService;
import org.fastcatsearch.ir.config.CollectionConfig;
import org.fastcatsearch.ir.config.DataPlanConfig;
import org.fastcatsearch.ir.search.CollectionHandler;
import org.fastcatsearch.service.ServiceManager;
import org.fastcatsearch.util.CollectionContextUtil;
import org.fastcatsearch.util.FilePaths;
import org.fastcatsearch.util.ResponseWriter;
/**
 * 관리도구 컬렉션 생성 위자드 step1 에서 사용될 액션.   
 * */
@ActionMapping(value = "/management/collections/create-update", authority = ActionAuthority.Collections, authorityLevel = ActionAuthorityLevel.WRITABLE)
public class CreateCollectionAction extends AuthAction {

	@Override
	public void doAuthAction(ActionRequest request, ActionResponse response) throws Exception {

		String collectionId = request.getParameter("collectionId");
		String collectionName = request.getParameter("name");
		String indexNode = request.getParameter("indexNode");
		String searchNodeListString = request.getParameter("searchNodeList");
		String dataNodeListString = request.getParameter("dataNodeList");
		
		List<String> searchNodeList = new ArrayList<String>();
		if (searchNodeListString != null) {
			for (String nodeStr : searchNodeListString.split(",")) {
				nodeStr = nodeStr.trim();
				if (nodeStr.length() > 0) {
					searchNodeList.add(nodeStr);
				}
			}
		}

		List<String> dataNodeList = new ArrayList<String>();
		if (dataNodeListString != null) {
			for (String nodeStr : dataNodeListString.split(",")) {
				nodeStr = nodeStr.trim();
				if (nodeStr.length() > 0) {
					dataNodeList.add(nodeStr);
				}
			}
		}

		boolean isSuccess = false;
		String errorMessage = null;

		try {
			IRService irService = ServiceManager.getInstance().getService(IRService.class);
			
			CollectionHandler collectionHandler = irService.collectionHandler(collectionId);
			if(collectionHandler == null) {
				CollectionConfig collectionConfig = new CollectionConfig(collectionName, indexNode, searchNodeList, dataNodeList, DataPlanConfig.DefaultDataPlanConfig);
	
				collectionHandler = irService.createCollection(collectionId, collectionConfig);
				
				isSuccess = (collectionHandler != null);
			}else{
				CollectionConfig collectionConfig = collectionHandler.collectionContext().collectionConfig();
				collectionConfig.setName(collectionName);
				collectionConfig.setIndexNode(indexNode);
				collectionConfig.setSearchNodeList(searchNodeList);
				collectionConfig.setDataNodeList(dataNodeList);
				FilePaths collectionFilePaths = collectionHandler.collectionContext().collectionFilePaths();
				isSuccess = CollectionContextUtil.updateConfig(collectionConfig, collectionFilePaths);
			}
			
		} catch (Exception e) {
			isSuccess = false;
			errorMessage = e.getMessage();
		} finally {
			ResponseWriter responseWriter = getDefaultResponseWriter(response.getWriter());
			responseWriter.object();
			responseWriter.key("success").value(isSuccess);
			if (errorMessage != null) {
				responseWriter.key("errorMessage").value(errorMessage);
			}
			responseWriter.endObject();
			responseWriter.done();
		}

	}

}
