package org.zkoss.fiddler.executor.server.configs;

import org.w3c.dom.Element;
import org.zkoss.fiddler.executor.utils.XMLParserUtil;

/*
 * 	<sandbox>
 <version>5.0.9</version>
 <name>Breeze 5.0.9</name>
 <!--
 <autoport>true</autoport>
 -->
 <port>10000</port>
 <debugmsg>false</debugmsg>
 <!-- 
 <libpaths>/var/zkfiddlelib/util;/var/zkfiddlelib/5.0.9</libpaths>
 -->
 <libpaths>C:/workspace/executor/jar_src/libs;C:/workspace/executor/jar_src/zkcore/5.0.9</libpaths>
 </sandbox>		
 */
public class SandboxConfig {

	private String version;

	private String name;

	private boolean autoport;

	private boolean debug;

	private String libpaths;
	
	private String context ;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAutoport() {
		return autoport;
	}

	public void setAutoport(boolean autoport) {
		this.autoport = autoport;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getLibpaths() {
		return libpaths;
	}

	public void setLibpaths(String libpaths) {
		this.libpaths = libpaths;
	}

	public static SandboxConfig parse(Element sandbox) {

		SandboxConfig conf = new SandboxConfig();

		conf.setVersion(XMLParserUtil.getFirstElementText(sandbox, "version"));
		conf.setName(XMLParserUtil.getFirstElementText(sandbox, "name"));
		conf.setContext(XMLParserUtil.getFirstElementText(sandbox, "context"));

		conf.setLibpaths(XMLParserUtil.getFirstElementText(sandbox, "libpaths"));

		return conf;
	}

	
	public String getContext() {
		return context;
	}

	
	public void setContext(String context) {
		this.context = context;
	}
	
	public String getSandboxBasePath(){

		String context = getContext();
		if(!context.endsWith("/")){
			context += "/";
		}
		
		return context;
	}

}
