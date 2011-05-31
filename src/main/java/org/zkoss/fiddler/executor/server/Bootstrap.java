package org.zkoss.fiddler.executor.server;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.zkoss.fiddler.executor.classloader.ProjectClassLoader;
import org.zkoss.fiddler.executor.resources.FiddleWebappResource;
import org.zkoss.fiddler.executor.resources.fetch.FiddleResourceFetcher;

/**
 * Started up by the plugin's runner. Starts Jetty.
 * 
 * @author hillenius, jsynge, jumperchen
 */
public class Bootstrap {

	private static Server server;

	private static WebAppContext web;

	/**
	 * Main function, starts the jetty server.
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		logArgus(false);
		Configs configs = new Configs();

		configs.validation();

		server = new Server();

		initConnnector(server, configs);

		initWebappContext(server, configs);

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(100);
		}
		return;
	}

	private static void initWebappContext(Server server, Configs configs) throws IOException, URISyntaxException {
		web = new WebAppContext();

		if (configs.getParentLoaderPriority()) {
			System.err.println("ParentLoaderPriority enabled");
			web.setParentLoaderPriority(true);
		}

		web.setContextPath("/");
		// web.setWar(configs.getWebAppDir());

		web.setInitParams(Collections.singletonMap("org.mortbay.jetty.servlet.Default.useFileMappedBuffer", "true"));

		ProjectClassLoader loader = new ProjectClassLoader(web, configs.getWebAppClassPath());
		web.setClassLoader(loader);

		// ZK web.xml configuration
		File f = File.createTempFile("resource", "tmp");
		f.delete();
		f.mkdir();
		web.setBaseResource(new FiddleWebappResource(loader, 
				new FiddleResourceFetcher(configs.getRemoteResourceHost(),	f)));

		server.addHandler(web);
	}

	private static void initConnnector(Server server, Configs configObj) {
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(configObj.getPort());

		if (configObj.getEnablessl() && configObj.getSslport() != null)
			connector.setConfidentialPort(configObj.getSslport());

		server.addConnector(connector);

		if (configObj.getEnablessl() && configObj.getSslport() != null)
			initSSL(server, configObj.getSslport(), configObj.getKeystore(), configObj.getPassword(),
					configObj.getKeyPassword(), configObj.getNeedClientAuth());

	}

	private static void logArgus(boolean loggerparam) {

		if (loggerparam) {
			String[] propkeys = new String[] { "rjrcontext", "rjrwebapp", "rjrport", "rjrsslport", "rjrkeystore",
					"rjrpassword", "rjrclasspath", "rjrkeypassword", "rjrscanintervalseconds", "rjrenablescanner",
					"rjrenablessl", "rjrenbaleJNDI" };
			for (String key : propkeys) {
				System.err.println("-D" + key + "=" + System.getProperty(key));
			}
		}
	}

	private static void initSSL(Server server, int sslport, String keystore, String password, String keyPassword,
			boolean needClientAuth) {

		if (keystore == null) {
			throw new IllegalStateException("you need to provide argument -Drjrkeystore with -Drjrsslport");
		}
		if (password == null) {
			throw new IllegalStateException("you need to provide argument -Drjrpassword with -Drjrsslport");
		}
		if (keyPassword == null) {
			throw new IllegalStateException("you need to provide argument -Drjrkeypassword with -Drjrsslport");
		}

		SslSocketConnector sslConnector = new SslSocketConnector();
		sslConnector.setKeystore(keystore);
		sslConnector.setPassword(password);
		sslConnector.setKeyPassword(keyPassword);

		if (needClientAuth) {
			System.err.println("Enable NeedClientAuth.");
			sslConnector.setNeedClientAuth(needClientAuth);
		}
		sslConnector.setMaxIdleTime(30000);
		sslConnector.setPort(sslport);

		server.addConnector(sslConnector);
	}

}
