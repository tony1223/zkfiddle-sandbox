package org.zkoss.fiddler.executor.classloader;

import java.util.ArrayList;
import java.util.List;


public class FiddleClass {

	private String token;
	private String clsPkg;
	private String clsName;
	private String content;

	private Class cls;

	private byte[] bytes;
	
	public FiddleClass(){
		
	}
	
	public FiddleClass(String name,String content){
		this.clsName = name ;
		this.content = content;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getClsPkg() {
		return clsPkg;
	}
	
	public void setClsPkg(String clsPkg) {
		this.clsPkg = clsPkg;
	}
	
	public String getClsName() {
		return clsName;
	}
	
	public void setClsName(String clsName) {
		this.clsName = clsName;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public static List<FiddleClass> list(){
		return new ArrayList<FiddleClass>();
	}
	
	public Class getCls() {
		return cls;
	}

	
	public void setCls(Class cls) {
		this.cls = cls;
	}

	
	public byte[] getBytes() {
		return bytes;
	}

	
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
}
