package org.zkoss.fiddler.executor.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

/**
 * A configuration object to handle the complicated parse job , and make thing
 * easier.
 */

public class Configs {

	private static boolean _logMode = Boolean.getBoolean("debugmsg");

	private String context;

	private String webAppDir;

	private Integer port;

//	private Integer sslport;

//	private String keystore;

//	private String password;

	private String[] webAppClasslibPaths;

//	private String keyPassword;

//	private Integer scanIntervalSeconds;

	private Boolean parentLoaderPriority;

//	private Boolean enablessl;

//	private Boolean needClientAuth;

//	private Boolean enableJNDI;

//	private String configurationClasses;

	private int pingRemoteInterval ;

	private String remoteResourceHost;
	
	private String localHostName;
	
	private String zkversion;
	
	private String instanceName;

	public Configs() {
		
		/**
		 * FIXME user a prefix as possible for system properties , 
		 * since the name is too easy to conflict with others....
		 */
		context = "/";
		webAppDir = System.getProperty("webapp");
		if (webAppDir != null) {
			if (webAppDir.matches("^\".*?\"$")) {
				webAppDir = webAppDir.substring(1, webAppDir.length() - 1);
			}
		}
		Integer mport = Integer.getInteger("port", -1);
		boolean autoport = Boolean.getBoolean("autoport");

		if (mport == -1) {
			if (autoport) {
				port = findAAvaiablePort(10000, 20000, 100);
			} else {
				port = 10158;
			}
		} else {
			port = mport;
		}
//		sslport = -1;
		webAppClasslibPaths = System.getProperty("libpaths", "").split(";"); // resovleWebappClasspath();

		parentLoaderPriority = true;
		if (System.getProperty("parentloaderpriority") != null)
			parentLoaderPriority = Boolean.getBoolean("parentloaderpriority");

		remoteResourceHost = System.getProperty("remoteResourceHost", "");
		
		pingRemoteInterval = Integer.getInteger("pingRemoteInterval",1000 * 60 * 30 );
		
		localHostName = System.getProperty("localHostName", "");
		
		zkversion = System.getProperty("zkversion", "");
		
		instanceName = System.getProperty("instName", ""); 
		
		
//		enablessl = false;
//		needClientAuth = false;
//		enableJNDI = false;
//		configurationClasses = "";

	}

	private int findAAvaiablePort(int start, int end, int retry) {

		int range = end - start + 1;
		int port = -1;

		for (int i = 0; i < retry || retry == -1; ++i) {
			port = start + (int) (Math.random() * range);
			if (available(port))
				return port;
		}

		throw new IllegalStateException("no available port");
	}

	public String getContext() {
		return context;
	}

	public String getWebAppDir() {
		return webAppDir;
	}

	public Integer getPort() {
		return port;
	}

//	public Integer getSslport() {
//		return sslport;
//	}

//	public String getKeystore() {
//		return keystore;
//	}
//
//	public String getPassword() {
//		return password;
//	}

	public String[] getWebAppClasslibPaths() {
		return webAppClasslibPaths;
	}

//	public String getKeyPassword() {
//		return keyPassword;
//	}
//
//	public Integer getScanIntervalSeconds() {
//		return scanIntervalSeconds;
//	}

	public Boolean getParentLoaderPriority() {
		return parentLoaderPriority;
	}

//	public Boolean getEnablessl() {
//		return enablessl;
//	}
//
//	public Boolean getNeedClientAuth() {
//		return needClientAuth;
//	}
//
//	public Boolean getEnableJNDI() {
//		return enableJNDI;
//	}
//
//	public String getConfigurationClasses() {
//		return configurationClasses;
//	}

	public void validation() {
		if (getContext() == null) {
			throw new IllegalStateException("you need to provide argument -Dcontext");
		}
		// if (getWebAppDir() == null) {
		// throw new IllegalStateException(
		// "you need to provide argument -Drjrwebapp");
		// }
		if (getPort() == null ) {
			throw new IllegalStateException("you need to provide argument -Dport");
		}
		
//		if (getSslport() == null) {
//			throw new IllegalStateException("you need to provide argument -Dport and/or -Dsslport");
//		}

		if (!available(port)) {
			throw new IllegalStateException("port :" + port + " already in use!");
		}

//		if (getEnablessl() && getSslport() != null) {
//			if (!available(sslport)) {
//				throw new IllegalStateException("SSL port :" + sslport + " already in use!");
//			}
//		}
		
		if(remoteResourceHost == null || "".equals(remoteResourceHost.trim())  ){
			throw new IllegalStateException("remote host path is empty!");
		}
		
		if(localHostName == null ||"".equals(localHostName.trim())){
			throw new IllegalStateException("local host name is empty!");
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

	public static boolean isLogMode() {
		return _logMode;
	}

	public int getPingRemoteInterval() {
		return pingRemoteInterval;
	}

	public void setPingRemoteInterval(int pingRemoteInterval) {
		this.pingRemoteInterval = pingRemoteInterval;
	}
	
	
	public String getFullLocalInstancePath(){
		String portString = (getPort() != 80 ? ":" + getPort() : "");
		return getLocalHostName() + portString + "/";
	}

	public String getRemoteResourceHost() {
		return remoteResourceHost;
	}

	public void setRemoteResourceHost(String remoteResourceHost) {
		this.remoteResourceHost = remoteResourceHost;
	}

	
	/**
	 * you have to tell remote resource host where you are ,
	 * so they can access you from remote , you have to tell them at this case.
	 * 
	 * The base rule is hostname must be accessible for the end user.
	 * 
	 * @return
	 */
	public String getLocalHostName() {
		return localHostName;
	}
	
	public void setLocalHostName(String localHostName) {
		this.localHostName = localHostName;
	}

	
	public String getZkversion() {
		return zkversion;
	}

	public void setZkversion(String zkversion) {
		this.zkversion = zkversion;
	}

	
	public String getInstanceName() {
		return instanceName;
	}	
}