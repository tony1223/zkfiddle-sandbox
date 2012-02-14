package org.zkoss.fiddler.executor.server;

import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.zkoss.fiddler.executor.server.configs.SandboxConfig;
import org.zkoss.fiddler.executor.server.configs.SandboxInfo;
import org.zkoss.fiddler.executor.server.configs.SandboxServerConfig;
import org.zkoss.fiddler.executor.utils.PortUtil;
import org.zkoss.fiddler.executor.utils.URLUtil;

/**
 * Started up by the plugin's runner. Starts Jetty. Reference from Run-Jetty-Run
 * project 's Bootstrap.
 * 
 * @author tonyq
 */
public class FiddleSandboxServerAgent {

	private static SandboxServer server;

	/**
	 * Main function, starts the jetty server.
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		if (args.length == 0 || args[0] == null) {
			System.out.println("Usage:java FiddleSandboxServerAgent <Config file>");
			return;
		}

		final SandboxServerConfig conf = SandboxServerConfig.parse(args[0]);

		Integer port = conf.getPort();
		if (port == -1) {
			if (conf.isAutoport()) {
				port = PortUtil.findAAvaiablePort(10000, 20000, 100);
			} else {
				port = 8080;
			}
		}

		server = new SandboxServer(conf.getPort());

		for (SandboxConfig sbConf : conf.getSandboxs()) {
			// Check if we could redirect to another link first
			server.addContext(conf,sbConf);
		}

		try {
			// TonyQ:2011/6/2
			// if we want , here we could add on a level for server context
			// level ,
			// but I think the sandbox is not strong enough for this.
			server.start();

			prepareForRemoteNotification(conf);
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(100);
		}
		return;
	}

	private static void prepareForRemoteNotification(final SandboxServerConfig serverconf) {
		Thread thread = new Thread() {

			public void run() {
				while (true) {
					try {
						boolean connect = pingRemote(serverconf.getRemote().getHostname(), serverconf.getSandboxInfos());

						if (Configs.isLogMode()) {
							if (connect)
								System.out.println("connect with [" + serverconf.getRemote().getHostname() + "]");
							else
								System.out.println("lost remote connection with ["
										+ serverconf.getRemote().getHostname() + "]");
						}

					} catch (ConnectException e) {
						if (Configs.isLogMode()) {
							System.out.println("lost remote connection:" + serverconf.getRemote().getHostname());
						}
					} catch (Exception e) {
						if (Configs.isLogMode()) {
							e.printStackTrace();
						}
					}
					try {
						Thread.sleep(serverconf.getRemote().getPingInterval());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
	}


	private static boolean pingRemote(String remotehost, List<SandboxInfo> info)
			throws MalformedURLException, ConnectException, UnsupportedEncodingException {

		// new spec should be
		// 1.path=root path
		// 2.contexts =
		// [[context,ver,name],[context,ver,name],[context,ver,name]]
		// Don't forget for url encoding and decoding.

		String content = URLUtil.fetchContent(new URL(remotehost + "/sandbox/"+getParam(info)));
		return (Boolean.parseBoolean(content));
	}
	
	private static String getParam(List<SandboxInfo> infos) throws UnsupportedEncodingException{
		StringBuffer sb = new StringBuffer("?size="+infos.size());
		for (int i = 0; i < infos.size(); i++) {
			SandboxInfo info = infos.get(i);
			
			sb.append("&path"+i+"=" + URLEncoder.encode(info.getPath(), "UTF-8"));
			sb.append("&ver"+i+"=" + info.getVersion() );
			sb.append("&name"+i+"=" + URLEncoder.encode(info.getName(), "UTF-8"));
			sb.append("&theme"+i+"=" + URLEncoder.encode(info.getTheme(), "UTF-8"));
		}
		
		return sb.toString();
	}

}
