package org.zkoss.fiddler.executor.classloader;
public class ByteClass {

		private Class<?> cls;

		private String name;

		private byte[] bytes;

		public Class<?> getCls() {
			return cls;
		}

		public void setClz(Class<?> cls) {
			this.cls = cls;
		}

		public ByteClass(String name, byte[] bytes) {
			this.name = name;
			this.bytes = bytes;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public byte[] getBytes() {
			return bytes;
		}

		public void setBytes(byte[] bytes) {
			this.bytes = bytes;
		}

	}