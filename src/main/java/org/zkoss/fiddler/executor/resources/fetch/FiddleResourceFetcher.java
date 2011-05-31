package org.zkoss.fiddler.executor.resources.fetch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.mortbay.util.ajax.JSON;
import org.zkoss.fiddler.executor.classloader.ByteClass;
import org.zkoss.fiddler.executor.classloader.FiddleClass;
import org.zkoss.fiddler.executor.classloader.FiddleClassUtil;
import org.zkoss.fiddler.executor.server.Configs;

public class FiddleResourceFetcher {

	// public static void main(String[] args) throws MalformedURLException {
	// FiddleResourceFetcher frt = new
	// FiddleResourceFetcher("http://localhost:8088");
	// List<FetchResource> resources = frt.fetch(new FetchedToken("34f8ilk",
	// ""));
	//
	// }

	Map<FetchedToken, List<FetchResource>> cacheResult = new HashMap<FetchedToken, List<FetchResource>>();

	private String host;

	private File base;

	public FiddleResourceFetcher(String phost, File base) {
		host = phost;
		this.base = base;
	}

	public boolean isFetched(FetchedToken ft) {
		return cacheResult.containsKey(ft);
	}


	// 3b10fdm
	public List<FetchResource> fetch(FetchedToken ft) throws MalformedURLException {

		// TODO review this
		if (cacheResult.containsKey(ft)) {
			return cacheResult.get(ft);
		}

		URL u = new URL(host + "/data/" + ft.getToken() + ("".equals(ft.getVersion()) ? "" : "/" + ft.getVersion()));

		if (Configs.isLogMode())
			System.out.println("requesting:" + u);

		StringBuffer content = new StringBuffer();

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(u.openConnection().getInputStream()));

			String input = br.readLine();
			while (input != null) {
				content.append(input);
				input = br.readLine();
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		if ("".equals(content.toString())) {
			cacheResult.put(ft, null);
			return null;
		}

		Map map = (Map) JSON.parse(content.toString());

		Object[] resources = (Object[]) map.get("resources");

		List<FetchResource> list = new ArrayList<FetchResource>();

		List<FiddleClass> fiddleClass = new ArrayList<FiddleClass>();

		for (Object obj : resources) {
			Map resource = (Map) obj;
			if (Configs.isLogMode())
				System.out.println("loading:" + resource.get("name"));
			FetchResource fr = new FetchResource();
			fr.setType(((Long) resource.get("type")).intValue());
			fr.setFileName((String) resource.get("name"));
			fr.setContent((String) resource.get("content"));
			fr.setStorePath(base.getAbsolutePath() + "/" + ft.getToken() +"/"+ft.getVersion()+"/"+ fr.getFileName());

			fr.saveContent();
			// TODO: notify user if parse error
			if (fr.getType() == 1) {
				fiddleClass.add(new FiddleClass(fr.getFileName(), fr.getContent()));
			}

			list.add(fr);
		}
		if (fiddleClass.size() != 0) {
			// TODO review this
			List<ByteClass> classlist = FiddleClassUtil.compile(fiddleClass);

			for (FetchResource rc : list) {

				if (rc.getType() == 1) {
					for (ByteClass bc : classlist) {
						if (ObjectUtils.equals(rc.getFileName(), bc.getName() + ".java")) {
							rc.setClz(bc.getCls());
						}
					}
				}

			}
		}
		cacheResult.put(ft, list);

		return list;
	}

}
