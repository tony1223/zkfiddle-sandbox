package org.zkoss.fiddler.executor.classloader;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.zkoss.fiddler.executor.server.Configs;

@SuppressWarnings("restriction")
public class FiddleClassUtil {

	// public static void main(String[] args) throws ClassNotFoundException,
	// IllegalArgumentException,
	// IllegalAccessException, InvocationTargetException, SecurityException,
	// NoSuchMethodException {
	//
	// List<FiddleClass> list = FiddleClass.list();
	// list.add(new FiddleClass(
	// "Test.java",
	// "package test;\npublic class Test {\n"+
	// " public static int i = 0; public static void test(){"+
	// " System.out.println(\"Hello world:\"+i); i++;}} \n class TestB{} "));
	//
	// List<ByteClass> clzlist = FiddleClassUtil.compile(list);
	// List<ByteClass> clzlist2 = FiddleClassUtil.compile(list);
	// System.out.println(clzlist.get(0).getName());
	// System.out.println(clzlist2.get(0).getName());
	//
	// Class clazz = clzlist.get(0).getCls();
	// Method method = clazz.getMethod("test");
	// method.invoke(null);
	// method.invoke(null);
	// method.invoke(null);
	// method.invoke(null);
	// method.invoke(null);
	// method.invoke(null);
	// method.invoke(null);
	// method.invoke(null);
	//
	// clazz = clzlist2.get(0).getCls();
	// method = clazz.getMethod("test");
	// method.invoke(null);
	// method.invoke(null);
	// method.invoke(null);
	// method.invoke(null);
	// method.invoke(null);
	// method.invoke(null);
	// method.invoke(null);
	// method.invoke(null);
	// }

	public static List<ByteClass> compile(List<FiddleClass> waitingForCompile, Writer pr,
			DiagnosticCollector diagnostics, ProjectClassLoader pcl) {
		return compile(waitingForCompile, pr, diagnostics, pcl.getClasspathString(), pcl);
	}

	public static List<ByteClass> compile(List<FiddleClass> waitingForCompile) {
		return compile(waitingForCompile, null, null, null);
	}

	public static List<ByteClass> compile(List<FiddleClass> waitingForCompile, String classPath) {
		return compile(waitingForCompile, null, null, classPath, null);
	}

	public static List<ByteClass> compile(List<FiddleClass> waitingForCompile, Writer pr,
			DiagnosticCollector diagnostics, String classPath, ClassLoader test) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		List<JavaFileObject> tasks = new ArrayList<JavaFileObject>();

		for (FiddleClass fcls : waitingForCompile) {
			JavaFileObject fileObj = new StringJavaFileObject(fcls.getClsName(), fcls.getContent());
			tasks.add(fileObj);
		}

		JavaFileManager defFileMgr = compiler.getStandardFileManager(null, null, null);
		MemoryJavaFileManager fileMgr = new MemoryJavaFileManager(defFileMgr);

		List arg = null;
		if (classPath != null) {
			arg = new ArrayList<String>();
			arg.add("-classpath");
			arg.add(classPath);
			if (Configs.isLogMode()) {
				System.out.println("compiling with classpath:" + classPath);
			}
		}
		compiler.getTask(pr, fileMgr, diagnostics, arg, null, tasks).call();

		List<ByteClass> list = fileMgr.getByteClasses();
		// ByteArrayClassConverter clsLoader = new
		// ByteArrayClassConverter(test.getClasspathString());
		ByteArrayClassConverter clsLoader = new ByteArrayClassConverter(test);
		clsLoader.processByteClasses(list);
		return list;
	}

}
