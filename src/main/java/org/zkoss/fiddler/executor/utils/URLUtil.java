package org.zkoss.fiddler.executor.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.zkoss.fiddler.executor.server.Configs;

public class URLUtil {

	public static void main(String[] args) throws MalformedURLException {
		
		System.out.println(URLUtil.fetchContent(new URL(
				"http://localhost:8088/instance/?path=http://localhost:19502/&ver=5.0.7&name=TonyQ")));

	}

	public static String fetchContent(URL u) {

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
		return content.toString();
	}
}
