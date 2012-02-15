package org.zkoss.fiddler.executor.resources.fetch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import org.zkoss.fiddler.executor.server.Configs;

public class FetchResource {
	public static final int TYPE_ZUL = 0;

	public static final int TYPE_JAVA = 1;  

	public static final int TYPE_JS = 2;

	public static final int TYPE_HTML = 3;

	public static final int TYPE_CSS = 4;
	
	public static final int TYPE_IMAGE = 5;
	
	private int type;

	private String fileName;

	private String content;

	private String storePath;

	public URL getStoreURL() {
		File f = new File(getStorePath());
		try {
			return f.toURI().toURL();
		} catch (MalformedURLException e) {
			if (Configs.isLogMode())
				e.printStackTrace();
			return null;
		}
	}

	public String getStorePath() {
		return storePath + getFileName();
	}

	public void setStoreBasePath(String storePath) {
		this.storePath = storePath;
	}

	@SuppressWarnings("rawtypes")
	private Class clz;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@SuppressWarnings("rawtypes")
	public Class getClz() {
		return clz;
	}

	@SuppressWarnings("rawtypes")
	public void setClz(Class clz) {
		this.clz = clz;
	}

	public boolean saveContent() {
		String newFileName = this.getStorePath() ;
		File f = new File(newFileName);
		if (Configs.isLogMode())
			System.out.println("save to :" + newFileName);
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			try {
				Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFileName), "UTF-8"));
				try {
					if(this.getContent() == null){
						throw new IllegalStateException("file content is null");
					}
					
					if(type == TYPE_IMAGE){
						out.write("img:::"+this.getContent());
					}else{
						out.write(this.getContent());
					}
				} catch (IllegalStateException e){
					if (Configs.isLogMode())
						e.printStackTrace();
				} catch (IOException e) {
					if (Configs.isLogMode())
						e.printStackTrace();
				} finally {
					out.close();
				}
				return true;
			} catch (IOException e) {
				if (Configs.isLogMode())
					e.printStackTrace();
				return false;
			}
		}
		return true;

	}

}
