package models;

import java.util.LinkedHashMap;

public class MethodDetail {
	private String name;
	/**
	 *   name then type  
	 */
	private LinkedHashMap<String,String> parameters;
	private String protectionLevel;
	private String returnType;
	
	public MethodDetail() {
		parameters = new LinkedHashMap<String,String>();
	}
	public String getName() {
		return name;
	}
	public LinkedHashMap<String, String> getParameters() {
		return parameters;
	}
	public String getProtectionLevel() {
		return protectionLevel;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setParameters(LinkedHashMap<String, String> parameters) {
		this.parameters = parameters;
	}
	public void setProtectionLevel(String protectionLevel) {
		this.protectionLevel = protectionLevel;
	}
	
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
}
