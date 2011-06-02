package org.zkoss.fiddler.executor.classloader;

import java.util.List;

import org.zkoss.fiddler.executor.server.Configs;

public class ByteArrayClassConverter extends ClassLoader {

	public ByteArrayClassConverter(ClassLoader cl) {
		super(cl);
	}
	public void processByteClasses(List<ByteClass> list) {
		for (ByteClass bc : list) {
			String name = bc.getName();
			if (Configs.isLogMode())
				System.out.println("compiling:" + name);
			byte[] bts = bc.getBytes();
			bc.setClz(super.defineClass(name, bts, 0, bts.length));
		}
	}

}