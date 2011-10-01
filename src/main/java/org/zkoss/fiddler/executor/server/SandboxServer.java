package org.zkoss.fiddler.executor.server;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.zkoss.fiddler.executor.classloader.ProjectClassLoader;
import org.zkoss.fiddler.executor.resources.FiddleWebappResource;
import org.zkoss.fiddler.executor.resources.fetch.FiddleResourceFetcher;

public class SandboxServer {

	// TODO change this for jetty7
	private static final String JETTY_USE_FILE_MAPPED_BUFFER = "org.mortbay.jetty.servlet.Default.useFileMappedBuffer";

	private Server server;

	private Map<String, WebAppContext> contextMap;

	public SandboxServer(int port) {
		server = new Server();
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(port);

		server.addConnector(connector);

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

			WebAppContext web = new WebAppContext();
			web.setParentLoaderPriority(true);
			web.setContextPath(contextPath);
			web.setInitParams(Collections.singletonMap(JETTY_USE_FILE_MAPPED_BUFFER, "true"));

			ProjectClassLoader loader = new ProjectClassLoader(web, classpath);
			web.setClassLoader(loader);

			// ZK web.xml configuration
			File webapp = null;
			if (webappfolder == null) {
				webapp = tempResourceFolder();
			} else {
				webapp = new File(webappfolder);

				if (!webapp.exists()) {
					if (Configs.isLogMode()) {
						System.out.println("warning:webapp folder not found , use temp folder instead.");
					}
					webapp = tempResourceFolder();
				}
			}

			FiddleResourceFetcher frf = new FiddleResourceFetcher(remoteResourceHost, webapp, loader);
			web.setBaseResource(new FiddleWebappResource(loader, frf, webapp));
			server.addHandler(web);

			contextMap.put(contextPath, web);
		}
	}

	private File tempResourceFolder() throws IOException {
		File webapp = File.createTempFile("resource", "tmp");
		webapp.delete();
		webapp.mkdir();
		webapp.deleteOnExit();
		return webapp;
	}
	
	public void start() throws Exception{
		server.start();
	}

	public void join() throws InterruptedException{
		server.join();
	}
	
	public void removeContext(){
		
	}
	
}
