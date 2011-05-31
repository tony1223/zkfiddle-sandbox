package org.zkoss.fiddler.executor.classloader;

import java.io.File;
import java.io.IOException;

import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.resource.Resource;

/**
 * Uses the provided class path ONLY, rather than also supporting the adding of
 * the jars in the WEB-INF/lib directory, and the adding of the classes in the
 * WEB-INF/classes directory.
 * 
 * @author jsynge
 */
public class ProjectClassLoader extends WebAppClassLoader {

	public ProjectClassLoader(WebAppContext context, String projectClassPath) throws IOException {
		this(context, projectClassPath, true);
	}

	public ProjectClassLoader(WebAppContext context, String projectClassPath, boolean logger) throws IOException {
		super(context);

		/*
		 * As reported in these bugs:
		 * 
		 * http://code.google.com/p/run-jetty-run/issues/detail?id=25
		 * http://code.google.com/p/run-jetty-run/issues/detail?id=26
		 * 
		 * the path separator defined by Java (java.io.File.pathSeparator) (and
		 * used by the run-jetty-run plug-in) may not match the one used by
		 * Jetty (which is expects it to be either a comma or a semi-colon).
		 * Rather than move away from the standard path separator, I'm choosing
		 * to split the projectClassPath, and hand each entry to the super
		 * class, one at a time.
		 */
		File f = new File(projectClassPath);
		String[] tokens = f.list();
		// String[] tokens =
		// projectClassPath.split(String.valueOf(File.pathSeparatorChar));
		for (String entry : tokens) {
			if (logger)
				System.err.println("ProjectClassLoader: entry=" + f.getAbsolutePath() + File.separator + entry);
			super.addClassPath(f.getAbsolutePath() + File.separator + entry);
		}

	}

	/**
	 * code fix for a strange case with Beanshell suuport , see Issue #53 for
	 * more detail http://code.google.com/p/run-jetty-run/issues/detail?id=53
	 */
	public Class loadClass(String name) throws ClassNotFoundException {
		try {
			return loadClass(name, false);
		} catch (NoClassDefFoundError e) {
			throw new ClassNotFoundException(name);
		}
	}

	public void addClassPath(String classPath) throws IOException {
		super.addClassPath(classPath);
	}

	public void addJars(Resource lib) {
		super.addJars(lib);
	}
}
