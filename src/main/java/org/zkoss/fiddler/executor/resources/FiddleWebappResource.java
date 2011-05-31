package org.zkoss.fiddler.executor.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mortbay.resource.FileResource;
import org.mortbay.resource.Resource;
import org.zkoss.fiddler.executor.classloader.ProjectClassLoader;
import org.zkoss.fiddler.executor.resources.fetch.FetchResource;
import org.zkoss.fiddler.executor.resources.fetch.FetchedToken;
import org.zkoss.fiddler.executor.resources.fetch.FiddleResourceFetcher;
import org.zkoss.fiddler.executor.server.Configs;

public class FiddleWebappResource extends FiddleResourceBase {

	private File baseFile;

	private ProjectClassLoader classesLoader;

	private FiddleResourceFetcher fetcher;

	private Pattern parser = Pattern.compile("/([0-9a-zA-Z]+)(/([0-9]+))?.*");

	private HashMap<String, Resource> resourcePool = new HashMap<String, Resource>();

	public FiddleWebappResource(ProjectClassLoader projectLoader, FiddleResourceFetcher fetcher) {
		this.fetcher = fetcher;

		classesLoader = projectLoader;
		try {
			baseFile = File.createTempFile("jtr", "hello");
		} catch (IOException e) {

		}
	}

	public boolean isDirectory() {
		return true;
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	public URL getURL() {
		if (baseFile != null) {
			try {
				return baseFile.toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	public File getFile() throws IOException {
		return baseFile;
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	public String getName() {
		throw new UnsupportedOperationException("Unsupported");
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	public InputStream getInputStream() throws IOException {
		throw new UnsupportedOperationException("Unsupported");
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	public OutputStream getOutputStream() throws IOException, SecurityException {
		throw new UnsupportedOperationException("Unsupported");
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	public boolean delete() throws SecurityException {
		throw new UnsupportedOperationException("Unsupported");
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	public boolean renameTo(Resource dest) throws SecurityException {
		throw new UnsupportedOperationException("Unsupported");
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	public String[] list() {
		throw new UnsupportedOperationException("Unsupported");
	}

	public Resource addPath(String path) throws IOException, MalformedURLException {
		if (Configs.isLogMode())
			System.out.println("fetching:" + path);

		if (path == null) {
			return new EmptyResource();
		} else if (path.endsWith(".gz") || path.indexOf("favicon.ico") != -1) {
			return new EmptyResource();
		} else if ("/WEB-INF/zk.xml".equals(path)) {
			return new EmptyResource();
		} else if ("WEB-INF/".equals(path)) {
			return new FiddleWEBINFResourceHandler();
		} else if ("/".equals(path)) {
			return this;
		} else if (path.matches("/([0-9a-zA-Z]+)(/[0-9]+)?.*")) {

			Matcher match = parser.matcher(path);
			if (match.find()) {
				FetchedToken ft = new FetchedToken(match.group(1), match.groupCount() > 1 ? match.group(2).replaceFirst("/","") : "");
				// FIXME change host
				List<FetchResource> resources = fetcher.fetch(ft);
				if (resources != null) {
					for (FetchResource fr : resources) {
						if (Configs.isLogMode())
							System.out.println("adding to pool:" + (path + "/" + fr.getFileName()));

						try {
							resourcePool.put(path + "/" + fr.getFileName(), new FileResource(fr.getStoreURL()));
						} catch (URISyntaxException e) {
							if (Configs.isLogMode())
								e.printStackTrace();
						}
						if (fr.getType() == 1) {
							classesLoader.addClass(fr.getClz());
						}
					}
				}
			}

			// fetcher.
		}
		if (resourcePool.containsKey(path)) {
			return resourcePool.get(path);
		}
		return new EmptyResource();
	}
}
