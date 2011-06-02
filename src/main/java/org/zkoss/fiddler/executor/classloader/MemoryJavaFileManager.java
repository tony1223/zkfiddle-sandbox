package org.zkoss.fiddler.executor.classloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

import org.zkoss.fiddler.executor.server.Configs;

@SuppressWarnings("restriction")
public class MemoryJavaFileManager implements JavaFileManager {

	protected JavaFileManager parent;

	public HashMap<String, MemoryJavaFileObject> items = new HashMap<String, MemoryJavaFileObject>();

	public MemoryJavaFileManager(JavaFileManager parent) {
		this.parent = parent;
	}

	public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className,
			JavaFileObject.Kind kind, FileObject sibling) throws IOException {

		MemoryJavaFileObject mjfo = new MemoryJavaFileObject("file:///" + className + ".class", kind);
		items.put(className, mjfo);
		return mjfo;

	}

	public List<ByteClass> getByteClasses() {
		List<ByteClass> list = new ArrayList<ByteClass>();

		for (Map.Entry<String, MemoryJavaFileObject> e : items.entrySet()) {
			ByteClass bc = new ByteClass(e.getKey(), e.getValue().getBytes());
			list.add(bc);
		}

		return list;
	}

	// public Iterator<Map.Entry<String, MemoryJavaFileObject>> entrys() {
	// return items.entrySet().iterator();
	// }

	public void close() throws IOException {
		parent.close();
	}

	public void flush() throws IOException {
		parent.flush();
	}

	public ClassLoader getClassLoader(JavaFileManager.Location location) {
		if (Configs.isLogMode())
			System.out.println("loading Class loader from MemoryJavaFileManager");
		return parent.getClassLoader(location);
	}

	public FileObject getFileForInput(JavaFileManager.Location location, String packageName, String relName)
			throws IOException {
		if (Configs.isLogMode())
		System.out.println("input:"+packageName+":"+relName+":"+location);
		return parent.getFileForInput(location, packageName, relName);
	}

	public FileObject getFileForOutput(JavaFileManager.Location location, String packageName, String relName,
			FileObject sibling) throws IOException {
		return parent.getFileForOutput(location, packageName, relName, sibling);
	}

	public JavaFileObject getJavaFileForInput(JavaFileManager.Location location, String className,
			JavaFileObject.Kind kind) throws IOException {
		
		if (Configs.isLogMode())
			System.out.println("inputJavaFile:"+className+":"+kind+":"+location);

		return parent.getJavaFileForInput(location, className, kind);
	}

	public boolean handleOption(String current, Iterator<String> remaining) {
		return parent.handleOption(current, remaining);
	}

	public boolean hasLocation(JavaFileManager.Location location) {
		return parent.hasLocation(location);
	}

	public String inferBinaryName(JavaFileManager.Location location, JavaFileObject file) {
		return parent.inferBinaryName(location, file);
	}

	public boolean isSameFile(FileObject a, FileObject b) {
		return parent.isSameFile(a, b);
	}

	public Iterable<JavaFileObject> list(JavaFileManager.Location location, String packageName,
			Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
		return parent.list(location, packageName, kinds, recurse);
	}

	public int isSupportedOption(String option) {
		return parent.isSupportedOption(option);
	}
}