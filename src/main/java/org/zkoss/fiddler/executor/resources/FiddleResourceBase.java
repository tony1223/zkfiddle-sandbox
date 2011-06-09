package org.zkoss.fiddler.executor.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.mortbay.resource.Resource;

public class FiddleResourceBase extends Resource {

	/**
	 *
	 */
	private static final long serialVersionUID = 8748988779292501912L;

	public FiddleResourceBase() {

	}

	public void release() {
	}


	public boolean exists() {
		return true;
	}


	public boolean isDirectory() {
		return false;
	}

	public String getListHTML(String base,boolean parent) {
		return "";
	}

	public long lastModified() {
		return new Date().getTime();
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	public long length() {
		throw new UnsupportedOperationException("Unsupported");
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	public URL getURL() {
		throw new UnsupportedOperationException("Unsupported");
	}
	/**
	 * @throws UnsupportedOperationException
	 */
	public File getFile() throws IOException {
		throw new UnsupportedOperationException("Unsupported");
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


	public Resource addPath(String path) throws IOException,
			MalformedURLException {
		return new EmptyResource();
	}
}
