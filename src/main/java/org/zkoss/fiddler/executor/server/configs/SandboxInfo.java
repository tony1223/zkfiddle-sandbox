package org.zkoss.fiddler.executor.server.configs;

public class SandboxInfo {

	private String path;

	private String version;

	private String name;

	public String getPath() {
		return path;
	}

	public SandboxInfo(String path, String version, String name) {
		super();
		this.path = path;
		this.version = version;
		this.name = name;
	}

	public void setPath(String path) {
		this.path = path;
	}

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
}