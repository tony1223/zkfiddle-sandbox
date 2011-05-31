package org.zkoss.fiddler.executor.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.mortbay.resource.Resource;
import org.zkoss.fiddler.executor.classloader.ProjectClassLoader;

public class FiddleWebappResource extends FiddleResourceBase {

	private File baseFile;
	private ProjectClassLoader classesLoader = null;
	
	public FiddleWebappResource(ProjectClassLoader projectLoader) {
		
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
		if (baseFile != null){
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
		System.out.println("fetching:" + path);
		if ("WEB-INF/".equals(path)) {
			return new FiddleWEBINFResourceHandler();
		} else if ("/".equals(path)) {
			return this;

		}
		return new EmptyResource();
	}
}
