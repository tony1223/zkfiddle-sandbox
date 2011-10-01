package org.zkoss.fiddler.executor.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;

public class SandboxServer {

	private Server server;

	private Map<String, SandboxWebappContext> contextMap;

	public SandboxServer(int port) {
		server = new Server();
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(port);

		server.addConnector(connector);
		contextMap = new HashMap<String,SandboxWebappContext>();

	}

	/**
	 * 
	 * @param contextPath
	 * @param webappfolder
	 *            if null , we will create a temp folder for it.
	 * @throws IOException
	 */
	public void addContext(String contextPath, String webappfolder, List<String> classpath, String remoteResourceHost)
			throws IOException {
		synchronized (contextMap) {
			if (contextMap.containsKey(contextPath)) {
				throw new IllegalStateException("Context already exists in the web application.");
			}

			SandboxWebappContext web = new SandboxWebappContext(contextPath, webappfolder, classpath,
					remoteResourceHost);
			server.addHandler(web);

			contextMap.put(contextPath, web);
		}
	}


	public void restart() throws Exception {
		for (SandboxWebappContext swc : contextMap.values()) {
			swc.restart();
		}
	}

	public void start() throws Exception {
		server.start();
	}

	public void join() throws InterruptedException {
		server.join();
	}

}
