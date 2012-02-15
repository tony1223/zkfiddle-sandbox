package org.zkoss.fiddler.executor.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import javax.servlet.GenericServlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.webapp.WebAppContext;
import org.zkoss.fiddler.executor.classloader.ByteClass;
import org.zkoss.fiddler.executor.classloader.FiddleClass;
import org.zkoss.fiddler.executor.classloader.FiddleClassUtil;
import org.zkoss.fiddler.executor.classloader.ProjectClassLoader;
import org.zkoss.fiddler.executor.resources.FiddleWebappResource;
import org.zkoss.fiddler.executor.resources.fetch.FiddleResourceFetcher;
import org.zkoss.fiddler.executor.resources.fetch.ImageFilter;

public class SandboxWebappContext extends WebAppContext {
	private static final String JETTY_USE_FILE_MAPPED_BUFFER = "org.mortbay.jetty.servlet.Default.useFileMappedBuffer";

	private List<String> classpath;

	public SandboxWebappContext(String contextPath, String webappfolder,
			List<String> classpath, String remoteResourceHost, String theme)
			throws IOException {
		this.classpath = classpath;
		this.setParentLoaderPriority(true);
		this.setContextPath(contextPath);
		this.setInitParams(Collections.singletonMap(
				JETTY_USE_FILE_MAPPED_BUFFER, "true"));

		ProjectClassLoader pcl = new ProjectClassLoader(this, classpath);
		this.setClassLoader(pcl);

		addThemeWebinit(pcl, theme);
		this.addServlet(PongServlet.class, "/pong");
		this.addFilter(ImageFilter.class, "/*",Handler.DEFAULT);
		// ZK web.xml configuration
		File webapp = null;
		if (webappfolder == null) {
			webapp = tempResourceFolder();
		} else {
			webapp = new File(webappfolder);

			if (!webapp.exists()) {
				if (Configs.isLogMode()) {
					System.out
							.println("warning:webapp folder not found , use temp folder instead.");
				}
				webapp = tempResourceFolder();
			}
		}

		FiddleResourceFetcher frf = new FiddleResourceFetcher(
				remoteResourceHost, webapp);
		this.setBaseResource(new FiddleWebappResource(this, frf, webapp));

	}

	public static class PongServlet extends GenericServlet {
		private static final long serialVersionUID = 1037662625656780778L;

		public void service(ServletRequest req, ServletResponse res) {
			try {
				PrintWriter pw = res.getWriter();
				pw.println("{\"work\":true}");
			} catch (Exception ex) {
			}
		}
	}

	private void addThemeWebinit(ProjectClassLoader loader, String theme) {

		// org.zkoss.fiddle.sandbox.ThemeWebInit

		StringBuffer themeClassString = new StringBuffer();

		themeClassString.append("package org.zkoss.fiddle.sandbox;\n");
		themeClassString.append("import org.zkoss.lang.Library;\n");
		themeClassString.append("import org.zkoss.zk.ui.WebApp;\n");
		themeClassString.append("import org.zkoss.zk.ui.util.WebAppInit;\n");
		themeClassString
				.append("public class ThemeWebInit implements WebAppInit {\n");
		themeClassString
				.append(" public void init(WebApp wapp) throws Exception{\n");
		themeClassString
				.append(" Library.setProperty(\"org.zkoss.theme.preferred\", \""
						+ theme + "\");");
		themeClassString.append(" }\n");
		themeClassString.append(" }\n");

		FiddleClass themeClass = new FiddleClass("ThemeWebInit.java",
				themeClassString.toString());
		List<ByteClass> clslist = FiddleClassUtil.compile(themeClass, loader);

		for (ByteClass bc : clslist) {
			loader.addResourceClass(bc.getCls());
		}

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

	public void restart() throws Exception {
		stop();
		this.destroy();
		if (Configs.isLogMode())
			System.err
					.println("Restarting web context - Step 1 :Stoping webapp ...");

		Thread.sleep(10000L);
		if (classpath != null) {
			ProjectClassLoader loader = new ProjectClassLoader(this, classpath,
					false);
			setClassLoader(loader);
		}
		if (Configs.isLogMode())
			System.err
					.println("Restarting web context - Step 2 :Starting webapp ...");
		start();
	}

	public void setClasspath(List<String> classpath) {
		this.classpath = classpath;
	}

	public ProjectClassLoader getClassLoader() {
		return (ProjectClassLoader) super.getClassLoader();
	}
}
