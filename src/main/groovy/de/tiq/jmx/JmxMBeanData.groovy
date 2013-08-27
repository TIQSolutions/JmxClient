package de.tiq.jmx

import javax.management.ObjectName

class JmxMBeanData {
	
	String className;
	ObjectName associatedObjectName;
	List<String> attributes;
	
	JmxMBeanData(String className, ObjectName associatedObjectName, List<String> applyableMethodNames) {
		this.className = className
		this.associatedObjectName = associatedObjectName
		this.attributes = applyableMethodNames
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false
		if(obj instanceof JmxMBeanData){
			isEqual = obj.className == className && 
					  obj.associatedObjectName == associatedObjectName && 
					  obj.attributes == attributes
		}
		return isEqual
	}

	@Override
	public String toString() {
		return "JmxMBeanData [className=" + className + ", associatedObjectName=" + associatedObjectName + ", applyableMethodNames=" + attributes.join(" ") + "]";
	}
	
}
