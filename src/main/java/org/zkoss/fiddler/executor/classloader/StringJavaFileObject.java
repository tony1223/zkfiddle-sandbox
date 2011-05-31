package org.zkoss.fiddler.executor.classloader;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

@SuppressWarnings("restriction")
public class StringJavaFileObject extends SimpleJavaFileObject {

		protected String str;

		public StringJavaFileObject(String uriName, String str) {
			super(java.net.URI.create("file:///" + uriName + ".java"), JavaFileObject.Kind.SOURCE);
			this.str = str;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncErrors) {
			return str;
		}
	}