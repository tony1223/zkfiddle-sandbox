package org.zkoss.fiddler.executor.classloader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.resource.Resource;
import org.zkoss.fiddler.executor.server.Configs;

/**
 * Uses the provided class path ONLY, rather than also supporting the adding of
 * the jars in the WEB-INF/lib directory, and the adding of the classes in the
 * WEB-INF/classes directory.
 * 
 * @author jsynge
 */
public class ProjectClassLoader extends WebAppClassLoader {

	private Map<String, Class> classPool = new HashMap<String, Class>();

	private StringBuffer classpathString = new StringBuffer();
	
	public ProjectClassLoader(WebAppContext context, List<String> projectClassPath) throws IOException {
		this(context, projectClassPath, true);
	}

	public void addResourceClass(Class clz) {
		if (Configs.isLogMode())
			System.out.println("adding class:" + clz.getName());
		classPool.put(clz.getName(), clz);
	}

	public void addAllResourceClasses(List<Class> clzes) {
		for (Class c : clzes) {
			addResourceClass(c);
		}
	}

	public String getClasspathString() {
		return classpathString.toString();
	}

	public ProjectClassLoader(WebAppContext context, List<String> projectClassPaths, boolean logger) throws IOException {
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
		if (projectClassPaths != null && projectClassPaths.size() >0 && !"".equals(projectClassPaths.get(0))) {
			for (String projectClasspath : projectClassPaths) {
				File f = new File(projectClasspath);
				if(Configs.isLogMode()){
					System.out.println("loading classpathpath:"+projectClasspath);
				}
				String[] tokens = f.list();
				// projectClassPath.split(String.valueOf(File.pathSeparatorChar));
				for (String entry : tokens) {
					if (logger)
						System.err.println("ProjectClassLoader: entry=" + f.getAbsolutePath() + File.separator + entry);
					classpathString.append(f.getAbsolutePath() + File.separator + entry + System.getProperty("path.separator"));
					super.addClassPath(f.getAbsolutePath() + File.separator + entry);
				}
			}
		}

	}

	/**
	 * code fix for a strange case with Beanshell suuport , see Issue #53 for
	 * more detail http://code.google.com/p/run-jetty-run/issues/detail?id=53
	 */
	public Class loadClass(String name) throws ClassNotFoundException {
		try {
			if (classPool.containsKey(name))
				return classPool.get(name);
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
