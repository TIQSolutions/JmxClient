package de.tiq.jmx

import javax.management.ObjectName

class JmxMBeanData {
	
	ObjectName associatedObjectName;
	List<String> attributes;
	
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false
		if(obj instanceof JmxMBeanData){
			isEqual = obj.associatedObjectName == associatedObjectName && 
					  obj.attributes == attributes
		}
		return isEqual
	}

	@Override
	public String toString() {
		return "JmxMBeanData [associatedObjectName=" + associatedObjectName + ", attributes=" + attributes + "]";
	}
	
}
