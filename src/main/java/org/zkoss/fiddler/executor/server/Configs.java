package org.zkoss.fiddler.executor.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

/**
 * A configuration object to handle the complicated parse job , and make thing
 * easier.
 */

public class Configs {

	private String context;

	private String webAppDir;

	private Integer port;

	private Integer sslport;

	private String keystore;

	private String password;

	private String webAppClassPath;

	private String keyPassword;

	private Integer scanIntervalSeconds;

	private Boolean parentLoaderPriority;

	private Boolean enablessl;

	private Boolean needClientAuth;

	private Boolean enableJNDI;

	private String configurationClasses;

	// Fiddler only
	private String remoteResourceHost = "http://localhost:8088";

	public String getRemoteResourceHost() {
		return remoteResourceHost;
	}

	public void setRemoteResourceHost(String remoteResourceHost) {
		this.remoteResourceHost = remoteResourceHost;
	}

	public Configs() {
		context = "/";
		webAppDir = System.getProperty("rjrwebapp");
		if (webAppDir != null) {
			if (webAppDir.matches("^\".*?\"$")) {
				webAppDir = webAppDir.substring(1, webAppDir.length() - 1);
			}
		}
		port = 10158;
		sslport = -1;
		// FIXME remove this after production
		webAppClassPath = "C:/workspace/executor/target/dependency/"; // resovleWebappClasspath();
		parentLoaderPriority = true; // getBoolean("rjrparentloaderpriority",
										// true);

		enablessl = false;
		needClientAuth = false;
		enableJNDI = false;
		configurationClasses = "";

	}

	public String getContext() {
		return context;
	}

	// public String getWebAppDir() {
	// return webAppDir;
	// }

	public Integer getPort() {
		return port;
	}

	public Integer getSslport() {
		return sslport;
	}

	public String getKeystore() {
		return keystore;
	}

	public String getPassword() {
		return password;
	}

	public String getWebAppClassPath() {
		return webAppClassPath;
	}

	public String getKeyPassword() {
		return keyPassword;
	}

	public Integer getScanIntervalSeconds() {
		return scanIntervalSeconds;
	}

	public Boolean getParentLoaderPriority() {
		return parentLoaderPriority;
	}

	public Boolean getEnablessl() {
		return enablessl;
	}

	public Boolean getNeedClientAuth() {
		return needClientAuth;
	}

	public Boolean getEnableJNDI() {
		return enableJNDI;
	}

	public String getConfigurationClasses() {
		return configurationClasses;
	}

	public void validation() {
		if (getContext() == null) {
			throw new IllegalStateException("you need to provide argument -Drjrcontext");
		}
		// if (getWebAppDir() == null) {
		// throw new IllegalStateException(
		// "you need to provide argument -Drjrwebapp");
		// }
		if (getPort() == null && getSslport() == null) {
			throw new IllegalStateException("you need to provide argument -Drjrport and/or -Drjrsslport");
		}

		if (!available(port)) {
			throw new IllegalStateException("port :" + port + " already in use!");
		}

		if (getEnablessl() && getSslport() != null) {
			if (!available(sslport)) {
				throw new IllegalStateException("SSL port :" + sslport + " already in use!");
			}
		}
	}

	private static boolean available(int port) {
		if (port <= 0) {
			throw new IllegalArgumentException("Invalid start port: " + port);
		}

		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
				}
			}
		}

		return false;
	}

	public static boolean isLogMode(){
		return true;
	}
}