/*
 * Copyright (c) 2013 Websquared, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     swsong - initial API and implementation
 */

package org.fastcatsearch.datasource.reader;

import java.io.File;
import java.util.Map;

import org.fastcatsearch.datasource.SourceModifier;
import org.fastcatsearch.ir.common.IRException;
import org.fastcatsearch.ir.config.DataSourceConfig;
import org.fastcatsearch.ir.config.SingleSourceConfig;
import org.fastcatsearch.ir.settings.SchemaSetting;
import org.fastcatsearch.util.DynamicClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultDataSourceReaderFactory {
	private static Logger logger = LoggerFactory.getLogger(DefaultDataSourceReaderFactory.class);

	public static AbstractDataSourceReader<Map<String, Object>> createFullIndexingSourceReader(File filePath, SchemaSetting schemaSetting, DataSourceConfig dataSourceConfig) throws IRException {

		AbstractDataSourceReader<Map<String, Object>> dataSourceReader = new DefaultDataSourceReader(schemaSetting);
		logger.debug("dataSourceConfig > {}", dataSourceConfig);
		if (dataSourceConfig != null && dataSourceConfig.getFullIndexingSourceConfig() != null) {
			
			for (SingleSourceConfig singleSourceConfig : dataSourceConfig.getFullIndexingSourceConfig()) {
				if(!singleSourceConfig.isActive()){
					continue;
				}
				SingleSourceReader<Map<String, Object>> sourceReader = createSingleSourceReader(filePath, singleSourceConfig, null);
				dataSourceReader.addSourceReader(sourceReader);
			}
		} else {
			logger.error("설정된 datasource가 없습니다.");
		}
		dataSourceReader.init();
		return dataSourceReader;
	}
	
	public static AbstractDataSourceReader<Map<String, Object>> createAddIndexingSourceReader(File filePath, SchemaSetting schemaSetting, DataSourceConfig dataSourceConfig, String lastIndexTime) throws IRException {

		AbstractDataSourceReader<Map<String, Object>> dataSourceReader = new DefaultDataSourceReader(schemaSetting);
		logger.debug("dataSourceConfig > {}", dataSourceConfig);
		if (dataSourceConfig != null && dataSourceConfig.getAddIndexingSourceConfig() != null) {
			
			for (SingleSourceConfig singleSourceConfig : dataSourceConfig.getAddIndexingSourceConfig()) {
				if(!singleSourceConfig.isActive()){
					continue;
				}
				SingleSourceReader<Map<String, Object>> sourceReader = createSingleSourceReader(filePath, singleSourceConfig, lastIndexTime);
				dataSourceReader.addSourceReader(sourceReader);
			}
		} else {
			logger.error("설정된 datasource가 없습니다.");
		}
		dataSourceReader.init();
		return dataSourceReader;
	}

	private static SingleSourceReader<Map<String, Object>> createSingleSourceReader(File filePath, SingleSourceConfig singleSourceConfig, String lastIndexTime) throws IRException {
		String sourceReaderType = singleSourceConfig.getSourceReader();
		String sourceModifierType = singleSourceConfig.getSourceModifier();
		SourceModifier<Map<String, Object>> sourceModifier = null;
		if (sourceModifierType != null && sourceModifierType.length() > 0) {
			sourceModifier = DynamicClassLoader.loadObject(sourceModifierType, SourceModifier.class);
		}

		SingleSourceReader<Map<String, Object>> sourceReader = DynamicClassLoader.loadObject(sourceReaderType, SingleSourceReader.class, new Class[] { File.class,
			SingleSourceConfig.class, SourceModifier.class, String.class }, new Object[] { filePath, 
				singleSourceConfig, sourceModifier, lastIndexTime });
		
		logger.debug("Loading sourceReader : {} >> {}, modifier:{} / lastIndexTime:{}", sourceReaderType, sourceReader, sourceModifier, lastIndexTime);
		// dataSourceReader가 null일 수 있다.
		if (sourceReader == null) {
			throw new IRException("소스리더를 로드하지 못했습니다. 해당 클래스가 클래스패스에 없거나 생성자 시그너처가 일치하는지 확인이 필요합니다. reader=" + sourceReaderType);
		} else {
			return sourceReader;
		}

	}
}
