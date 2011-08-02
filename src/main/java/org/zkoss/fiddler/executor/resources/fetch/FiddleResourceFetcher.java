package org.zkoss.fiddler.executor.resources.fetch;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;

import org.mortbay.resource.FileResource;
import org.mortbay.resource.Resource;
import org.mortbay.util.ajax.JSON;
import org.zkoss.fiddler.executor.classloader.ByteClass;
import org.zkoss.fiddler.executor.classloader.FiddleClass;
import org.zkoss.fiddler.executor.classloader.FiddleClassUtil;
import org.zkoss.fiddler.executor.classloader.ProjectClassLoader;
import org.zkoss.fiddler.executor.exceptions.JavaSecurityException;
import org.zkoss.fiddler.executor.server.Configs;
import org.zkoss.fiddler.executor.utils.URLUtil;

@SuppressWarnings("restriction")
public class FiddleResourceFetcher {

	Map<FetchedToken, List<FetchResource>> cacheResult = new HashMap<FetchedToken, List<FetchResource>>();

	private String host;

	private File base;
	private ProjectClassLoader projectClassLoader;
	
	private static final int TYPE_JAVA = 1;

	public FiddleResourceFetcher(String phost, File base,ProjectClassLoader pcl) {
		host = phost;
		this.base = base;
		projectClassLoader = pcl;
	}

	public boolean isFetched(FetchedToken ft) {
		return cacheResult.containsKey(ft);
	}

	private URL fileToURL(File f) {
		try {
			return f.toURI().toURL();
		} catch (MalformedURLException e) {
			if (Configs.isLogMode())
				e.printStackTrace();
			return null;
		}
	}

	public Resource getTokenHolder(FetchedToken ft) throws IOException, URISyntaxException {
		return new FileResource(fileToURL(new File(base.getAbsolutePath() + "/" + ft.getToken() + "/" + ft.getVersion()
				+ "/")));
	}

	
	private List<FetchResource> parseResourceList(String content,String storeParent){
		Map map = (Map) JSON.parse(content);
		Object[] resources = (Object[]) map.get("resources");
		List<FetchResource> list = new ArrayList<FetchResource>();
		for (Object obj : resources) {
			Map resource = (Map) obj;
			if (Configs.isLogMode())
				System.out.println("loading:" + resource.get("name"));
			FetchResource fr = new FetchResource();
			fr.setType(((Long) resource.get("type")).intValue());
			fr.setFileName((String) resource.get("name"));
			fr.setContent((String) resource.get("content"));
			fr.setStoreBasePath(base.getAbsolutePath() + storeParent + File.separator);
			list.add(fr);
		}		
		return list;
	}
	
	/**
	 * 
	 * @throws IllegalStateException when Compile Error
	 * @param resources
	 * @return
	 */
	public List<Class> compile(List<FetchResource> resources ) {
		 List<Class> ret = new ArrayList<Class>();
		List<FiddleClass> fiddleClass =new ArrayList<FiddleClass>();
		
		for(FetchResource fr:resources){
			if (fr.getType() == TYPE_JAVA) {
				fiddleClass.add(new FiddleClass(fr.getFileName(), fr.getContent()));
				if(fr.getContent().indexOf("System.exit")!= -1){
					throw new JavaSecurityException("ZK Fiddle Sandbox don't allow System.exit in your java class:\n"+fr.getContent());
				}else if(fr.getContent().indexOf("getRuntime()")!=-1){
					throw new JavaSecurityException("ZK fiddle don't allow you to run system command.");
				}
			}
		}
		
		if (fiddleClass.size() != 0) {
			StringBuffer sw = new StringBuffer();
			
			DiagnosticCollector<Diagnostic> diagnostics = new DiagnosticCollector<Diagnostic>();
			List<ByteClass> classlist = FiddleClassUtil.compile(fiddleClass, null, diagnostics, projectClassLoader);

			boolean error = false;
			for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
				if (Diagnostic.Kind.ERROR == diagnostic.getKind()) {
					error = true;
					sw.append(diagnostic.toString() +"\n\n");
				}
			}
			if (error) {
				throw new IllegalStateException("Compile Error:" + sw.toString());
			}
			
			/* Note that one resource might mapping to multiple resource , so we didn't record resource-class mapping. */
			for (ByteClass bc : classlist) {
				ret.add(bc.getCls());
			}
		}
		return ret;
	}
	
	public List<FetchResource> fetch(FetchedToken ft) throws MalformedURLException, ConnectException {

		URL u = new URL(host + "/data/" + ft.getToken() +"/" +  ft.getVersion());
		String content = URLUtil.fetchContent(u);

		if ("".equals(content)) {
			cacheResult.put(ft, null);
			return null;
		}

		String parent =  File.separator + ft.getToken() + File.separator + ft.getVersion() ;
		List<FetchResource> resources = parseResourceList(content,parent);
		cacheResult.put(ft, resources);
		return resources;
	}
	

}
