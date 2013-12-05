/*
 * Copyright 2013
 * Dan Häberlein
 * TIQ Solutions GmbH
 * Weißenfelser Str. 84
 * 04229 Leipzig, Germany
 *
 * info@tiq-solutions.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package de.tiq.jmx

import javax.management.ObjectName


class Attribute {
	
	String name
	List<String> compositeKeys = []

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((compositeKeys == null) ? 0 : compositeKeys.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false
		if(obj instanceof Attribute){
			isEqual = obj?.name == name && obj?.compositeKeys == compositeKeys 
		}
		return isEqual
	}

	@Override
	public String toString() {
		return "Attribute [name=" + name + ", compositeKeys=" + compositeKeys + "]";
	}	
	
}

class JmxMBeanData {
	
	ObjectName associatedObjectName;
	List<Attribute> attributes;
	
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false
		if(obj instanceof JmxMBeanData){
			isEqual = obj?.associatedObjectName == associatedObjectName && 
					  obj?.attributes == attributes
		}
		return isEqual
	}

	@Override
	public String toString() {
		return "JmxMBeanData [associatedObjectName=" + associatedObjectName + ", attributes=" + attributes + "]";
	}
	
}
