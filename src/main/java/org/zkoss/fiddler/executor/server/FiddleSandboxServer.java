package org.zkoss.fiddler.executor.server;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;

import org.zkoss.fiddler.executor.utils.URLUtil;

/**
 * Started up by the plugin's runner. Starts Jetty. Reference from Run-Jetty-Run
 * project 's Bootstrap.
 * 
 * @author tonyq
 */
public class FiddleSandboxServer {

	private static SandboxServer server;

	/**
	 * Main function, starts the jetty server.
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		final Configs configs = new Configs();
		configs.validation();
		server = new SandboxServer(configs.getPort());
		
		//Check if we could redirect to another link first
		server.addContext(configs.getContext(), configs.getWebAppDir(), configs.getWebAppClasslibPaths(), configs.getRemoteResourceHost());

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
						} catch (ConnectException e) {
							if (Configs.isLogMode()) {
								System.out.println("lost remote connection:" + configs.getRemoteResourceHost());
							}
						} catch (Exception e) {
							if (Configs.isLogMode()) {
								e.printStackTrace();
							}
						}
						try{
							Thread.sleep(configs.getPingRemoteInterval());
						}catch(Exception e){
							
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
		
		//new spec should be 
		//1.path=root path
		//2.contexts = [[context,ver,name],[context,ver,name],[context,ver,name]]
		// Don't forget for url encoding and decoding.
		
		String content = URLUtil.fetchContent(new URL(remotehost + "/sandbox/?path=" + path + "&ver=" + version
				+ "&name=" + name));
		return (Boolean.parseBoolean(content));
	}


}
