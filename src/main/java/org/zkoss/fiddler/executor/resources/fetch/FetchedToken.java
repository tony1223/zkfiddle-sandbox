package org.zkoss.fiddler.executor.resources.fetch;

import org.apache.commons.lang.ObjectUtils;

public class FetchedToken {

	private String token;

	private String version;

	public FetchedToken(String token, String version) {
		this.token = token;
		this.version = version;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getVersion() {
		if(version == null || "".equals(version))
			return "1";
		else
			return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean equals(Object obj) {
		if (obj instanceof FetchedToken) {
			FetchedToken token = (FetchedToken) obj;
			boolean tokens = ObjectUtils.equals(token.getToken(), this.token);
			if (!tokens)
				return false;
			try {
				int localVersion = 1;
				int objVersion = 1;
				if (version != null && !"".equals(version.trim()))
					localVersion = Integer.parseInt(version);
				if (token.getVersion() != null && !"".equals(token.getVersion().trim()))
					objVersion = Integer.parseInt(token.getVersion());

				return localVersion == objVersion;
			} catch (Exception e) {
				return false;
			}
		}
		return super.equals(obj);
	}
	public int hashCode() {
		return (getToken()+getVersion()).hashCode();
	}
}
