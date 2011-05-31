package org.zkoss.fiddler.executor.classloader;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

@SuppressWarnings("restriction")
public class MemoryJavaFileObject extends SimpleJavaFileObject {

	public ByteArrayOutputStream out = new ByteArrayOutputStream();

	public MemoryJavaFileObject(String uri, JavaFileObject.Kind kind) {
		super(java.net.URI.create(uri), kind);
	}

	@Override
	public OutputStream openOutputStream() {
		return out;
	}

	public byte[] getBytes() {
		return out.toByteArray();
	}
}