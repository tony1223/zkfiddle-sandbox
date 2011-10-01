package org.zkoss.fiddler.executor.server;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.mortbay.jetty.webapp.WebAppContext;
import org.zkoss.fiddler.executor.classloader.ProjectClassLoader;
import org.zkoss.fiddler.executor.resources.FiddleWebappResource;
import org.zkoss.fiddler.executor.resources.fetch.FiddleResourceFetcher;

public class SandboxWebappContext extends WebAppContext{
	private static final String JETTY_USE_FILE_MAPPED_BUFFER = "org.mortbay.jetty.servlet.Default.useFileMappedBuffer";
	
	private List<String> classpath;
	public SandboxWebappContext(String contextPath,String webappfolder,List<String> classpath,String remoteResourceHost) throws IOException{
		
		this.classpath = classpath;
		this.setParentLoaderPriority(true);
		this.setContextPath(contextPath);
		this.setInitParams(Collections.singletonMap(JETTY_USE_FILE_MAPPED_BUFFER, "true"));

		this.setClassLoader(new ProjectClassLoader(this, classpath));

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

		FiddleResourceFetcher frf = new FiddleResourceFetcher(remoteResourceHost, webapp);
		this.setBaseResource(new FiddleWebappResource(this, frf, webapp));

	}

	private static File tempResourceFolder() throws IOException {
		File webapp = File.createTempFile("resource", "tmp");
		webapp.delete();
		webapp.mkdir();
		webapp.deleteOnExit();
		return webapp;
	}
	
	public List<String> getClasspath() {
		return classpath;
	}

	public void restart() throws Exception{
		stop();
		this.destroy();
		if(Configs.isLogMode())
			System.err.println("Restarting web context - Step 1 :Stoping webapp ...");
		
		Thread.sleep(10000L);
		if (classpath != null) {
			ProjectClassLoader loader = new ProjectClassLoader(this,
					classpath, false);
			setClassLoader(loader);
		}
		if(Configs.isLogMode())
			System.err.println("Restarting web context - Step 2 :Starting webapp ...");
		start();
	}
	
	public void setClasspath(List<String> classpath) {
		this.classpath = classpath;
	}
	

	public ProjectClassLoader getClassLoader() {
		return (ProjectClassLoader) super.getClassLoader();
	}
}

