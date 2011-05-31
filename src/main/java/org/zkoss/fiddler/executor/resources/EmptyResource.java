package org.zkoss.fiddler.executor.resources;

import java.io.IOException;
import java.net.MalformedURLException;

import org.mortbay.resource.Resource;

public class EmptyResource extends FiddleResourceBase {


	public EmptyResource() {
		super();
	}

	public Resource addPath(String path) throws IOException,
			MalformedURLException {
		return new EmptyResource();
	}
	
	public boolean exists() {
		return false;
	}
	
}
