package org.zkoss.fiddler.executor.classloader;

import java.util.HashMap;
import java.util.List;

public class ByteArrayClassConverter extends ClassLoader {

		HashMap<String, ByteClass> map = new HashMap<String, ByteClass>();

		HashMap<String, Class> classpool = new HashMap<String, Class>();

		public void processByteClasses(List<ByteClass> list) {
			for (ByteClass bc : list) {
				String name = bc.getName();
				byte[] bts = bc.getBytes();
				bc.setClz(super.defineClass(name, bts, 0, bts.length));
			}
		}

	}