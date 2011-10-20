package org.zkoss.fiddler.executor.server.configs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zkoss.fiddler.executor.utils.XMLParserUtil;

/*
 * <sandboxs>
 <remote>
 <hostname>http://localhost:9999/datahandler/</hostname>
 <pingInterval>10000</pingInterval>
 </remote>
 <hostname>http://sandbox.local</hostname>
 <debugmsg>false</debugmsg>

 <sandbox>
 </sandbox>
 */
public class SandboxServerConfig {

	private String version;

	private String name;

	private SandboxRemoteFiddleConfig remote;

	private List<SandboxConfig> sandboxs;

	private boolean autoport;

	private int port;

	private boolean debugmsg;

	private String libpaths;

	private String hostname;

	public SandboxServerConfig() {
		sandboxs = new ArrayList<SandboxConfig>();
	}

	private static String loadConf(String file) {
		try {
			StringBuffer sb = new StringBuffer();
			File f = new File(file);

			BufferedReader reader = new BufferedReader(new FileReader(f));
			String inputStr = reader.readLine();

			while (inputStr != null) {
				sb.append(inputStr);
				inputStr = reader.readLine();
			}

			reader.close();
			return sb.toString();
		} catch (Exception ex) {
			return null;
		}
	}

	public static SandboxServerConfig parse(String file) {

		try {
			String conf = loadConf(file);
			if (conf == null) {
				throw new IllegalArgumentException("Can't load configuration file.");
			}

			SandboxServerConfig serverconfig = new SandboxServerConfig();

			Document doc = XMLParserUtil.buildDocument(conf);

			{
				SandboxRemoteFiddleConfig remote = SandboxRemoteFiddleConfig.parse(XMLParserUtil.getFirstElement(doc,
						"remote"));

				if (remote == null) {
					throw new IllegalArgumentException("no remote informations");
				}

				serverconfig.setRemote(remote);
			}

			{
				Element root = XMLParserUtil.getFirstElement(doc, "local");
				serverconfig.setHostname(XMLParserUtil.getFirstElementText(root, "hostname"));
			}

			{
				String autoPort = XMLParserUtil.getFirstElementText(doc, "autoport");
				serverconfig.setAutoport(Boolean.parseBoolean(autoPort));
			}

			{
				try {
					String portStr = XMLParserUtil.getFirstElementText(doc, "port");
					serverconfig.setPort(Integer.parseInt(portStr));
				} catch (NumberFormatException ex) {
					throw new IllegalArgumentException("Port of sandbox server is not a valid number.");
				}
			}

			{

				Iterator<Element> sandbox = XMLParserUtil.getElementsByTag(doc, "sandbox");

				List<SandboxConfig> confs = new ArrayList<SandboxConfig>();

				while (sandbox.hasNext()) {
					confs.add(SandboxConfig.parse(sandbox.next()));
				}

				serverconfig.setSandboxs(confs);
			}

			return serverconfig;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAutoport() {
		return autoport;
	}

	public void setAutoport(boolean autoport) {
		this.autoport = autoport;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getLibpaths() {
		return libpaths;
	}

	public void setLibpaths(String libpaths) {
		this.libpaths = libpaths;
	}

	public SandboxRemoteFiddleConfig getRemote() {
		return remote;
	}

	public void setRemote(SandboxRemoteFiddleConfig remote) {
		this.remote = remote;
	}

	public List<SandboxConfig> getSandboxs() {
		return sandboxs;
	}

	public void setSandboxs(List<SandboxConfig> sandboxs) {
		this.sandboxs = sandboxs;
	}

	public boolean isDebugmsg() {
		return debugmsg;
	}

	public void setDebugmsg(boolean debugmsg) {
		this.debugmsg = debugmsg;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getPath() {

		String portString = (getPort() != 80 ? ":" + getPort() : "");

		return hostname + portString;
	}
}
