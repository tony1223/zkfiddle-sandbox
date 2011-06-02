package org.zkoss.fiddler.executor.server;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.zkoss.fiddler.executor.classloader.ProjectClassLoader;
import org.zkoss.fiddler.executor.resources.FiddleWebappResource;
import org.zkoss.fiddler.executor.resources.fetch.FiddleResourceFetcher;
import org.zkoss.fiddler.executor.utils.URLUtil;

/**
 * Started up by the plugin's runner. Starts Jetty. Reference from Run-Jetty-Run
 * project 's Bootstrap.
 * 
 * @author tonyq
 */
public class FiddleInstanceServer {

	private static Server server;

	private static WebAppContext web;

	/**
	 * Main function, starts the jetty server.
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		final Configs configs = new Configs();
		configs.validation();
		server = new Server();

		initConnnector(server, configs);
		initWebappContext(server, configs);

		// TonyQ:2011/6/2
		// if we want , here we could add on a level for server context level ,
		// but I think the sandbox is not strong enough for this.

		try {
			server.start();

			Thread thread = new Thread() {

				public void run() {
					while (true) {
						try {
							boolean connect = pingRemote(
									configs.getRemoteResourceHost(),
									configs.getFullLocalInstancePath(), 
									configs.getZkversion(),
									configs.getInstanceName());
							
							if (Configs.isLogMode()) {
								if(connect)
									System.out.println("connect with [" + configs.getRemoteResourceHost()+"]");
								else
									System.out.println("lost remote connection with [" + configs.getRemoteResourceHost()+"]");
							}

							if (configs.getPingRemoteInterval() == -1) {
								break;
							}
							Thread.sleep(configs.getPingRemoteInterval());
						} catch (ConnectException e) {
							if (Configs.isLogMode()) {
								System.out.println("lost remote connection:" + configs.getRemoteResourceHost());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			};
			thread.start();

			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(100);
		}
		return;
	}

	private static boolean pingRemote(String remotehost, String path, String version, String name)
			throws MalformedURLException, ConnectException {
		String content = URLUtil.fetchContent(new URL(remotehost + "/instance/?path=" + path + "&ver=" + version
				+ "&name=" + name));
		return (Boolean.parseBoolean(content));
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

		ProjectClassLoader loader = new ProjectClassLoader(web, configs.getWebAppClasslibPaths());
		web.setClassLoader(loader);

		// ZK web.xml configuration
		File webapp = null;

		if (configs.getWebAppDir() == null) {

			webapp = File.createTempFile("resource", "tmp");
			webapp.delete();
			webapp.mkdir();
			webapp.deleteOnExit();

		} else {
			webapp = new File(configs.getWebAppDir());
		}

		FiddleResourceFetcher frf = new FiddleResourceFetcher(configs.getRemoteResourceHost(), webapp, loader);
		web.setBaseResource(new FiddleWebappResource(loader, frf, webapp));

		server.addHandler(web);
	}

	private static void initConnnector(Server server, Configs configObj) {
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(configObj.getPort());

		// if (configObj.getEnablessl() && configObj.getSslport() != null)
		// connector.setConfidentialPort(configObj.getSslport());

		server.addConnector(connector);

		// if (configObj.getEnablessl() && configObj.getSslport() != null)
		// initSSL(server, configObj.getSslport(), configObj.getKeystore(),
		// configObj.getPassword(),
		// configObj.getKeyPassword(), configObj.getNeedClientAuth());

	}

	// private static void initSSL(Server server, int sslport, String keystore,
	// String password, String keyPassword,
	// boolean needClientAuth) {
	//
	// if (keystore == null) {
	// throw new
	// IllegalStateException("you need to provide argument -Drjrkeystore with -Drjrsslport");
	// }
	// if (password == null) {
	// throw new
	// IllegalStateException("you need to provide argument -Drjrpassword with -Drjrsslport");
	// }
	// if (keyPassword == null) {
	// throw new
	// IllegalStateException("you need to provide argument -Drjrkeypassword with -Drjrsslport");
	// }
	//
	// SslSocketConnector sslConnector = new SslSocketConnector();
	// sslConnector.setKeystore(keystore);
	// sslConnector.setPassword(password);
	// sslConnector.setKeyPassword(keyPassword);
	//
	// if (needClientAuth) {
	// System.err.println("Enable NeedClientAuth.");
	// sslConnector.setNeedClientAuth(needClientAuth);
	// }
	// sslConnector.setMaxIdleTime(30000);
	// sslConnector.setPort(sslport);
	//
	// server.addConnector(sslConnector);
	// }

}
