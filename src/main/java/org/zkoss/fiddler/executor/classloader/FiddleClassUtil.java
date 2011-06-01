package org.zkoss.fiddler.executor.classloader;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

@SuppressWarnings("restriction")
public class FiddleClassUtil {

//	public static void main(String[] args) throws ClassNotFoundException, IllegalArgumentException,
//			IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
//
//		List<FiddleClass> list = FiddleClass.list();
//		list.add(new FiddleClass(
//				"Test.java",
//				"package test;\npublic class Test {\n"+
//				" public static int i = 0; public static void test(){"+
//				" System.out.println(\"Hello world:\"+i); i++;}} \n class TestB{} "));
//
//		List<ByteClass> clzlist = FiddleClassUtil.compile(list);
//		List<ByteClass> clzlist2 = FiddleClassUtil.compile(list);
//		System.out.println(clzlist.get(0).getName());
//		System.out.println(clzlist2.get(0).getName());
//
//		Class clazz = clzlist.get(0).getCls();
//		Method method = clazz.getMethod("test");
//		method.invoke(null);
//		method.invoke(null);
//		method.invoke(null);
//		method.invoke(null);
//		method.invoke(null);
//		method.invoke(null);
//		method.invoke(null);
//		method.invoke(null);
//
//		clazz = clzlist2.get(0).getCls();
//		method = clazz.getMethod("test");
//		method.invoke(null);
//		method.invoke(null);
//		method.invoke(null);
//		method.invoke(null);
//		method.invoke(null);
//		method.invoke(null);
//		method.invoke(null);
//		method.invoke(null);
//	}

	public static List<ByteClass> compile(List<FiddleClass> waitingForCompile) {
		return compile(waitingForCompile, new PrintWriter(System.err));
	}

	public static List<ByteClass> compile(List<FiddleClass> waitingForCompile, Writer pr) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		List<JavaFileObject> tasks = new ArrayList<JavaFileObject>();

		for (FiddleClass fcls : waitingForCompile) {
			JavaFileObject fileObj = new StringJavaFileObject(fcls.getClsName(), fcls.getContent());
			tasks.add(fileObj);
		}

		JavaFileManager defFileMgr = compiler.getStandardFileManager(null, null, null);
		MemoryJavaFileManager fileMgr = new MemoryJavaFileManager(defFileMgr);
		compiler.getTask(pr, fileMgr, null, null, null, tasks).call();

		List<ByteClass> list = fileMgr.getByteClasses();
		ByteArrayClassConverter clsLoader = new ByteArrayClassConverter();
		clsLoader.processByteClasses(list);
		return list;
	}

}
