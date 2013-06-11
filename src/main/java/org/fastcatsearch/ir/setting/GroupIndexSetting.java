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

package org.fastcatsearch.ir.setting;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

public class GroupIndexSetting {
	
	private String id;
	private String name;
	private FieldSetting field;
	
	public GroupIndexSetting() {}
	
	public GroupIndexSetting(String id, String name, FieldSetting field){
		this.id = id;
		this.name = name;
		this.field = field;
	}
	
	public String toString(){
		return "[group="+id+":"+name+":"+field+"]";
	}

	@XmlAttribute(name="id", required=true)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	public FieldSetting getField() {
		return field;
	}

	public void setField(FieldSetting field) {
		this.field = field;
	}
}