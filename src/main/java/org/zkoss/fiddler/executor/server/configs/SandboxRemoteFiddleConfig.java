package org.zkoss.fiddler.executor.server.configs;

import org.w3c.dom.Element;
import org.zkoss.fiddler.executor.utils.XMLParserUtil;

/*
 <remote>
 <hostname>http://localhost:9999/datahandler/</hostname>
 <pingInterval>10000</pingInterval>
 </remote>	
 */
public class SandboxRemoteFiddleConfig {

	private String hostname;

	private int pingInterval = 10000;

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPingInterval() {
		return pingInterval;
	}

	public void setPingInterval(int pingInterval) {
		this.pingInterval = pingInterval;
	}

	public static SandboxRemoteFiddleConfig parse(Element ele) {

		if (ele == null)
			return null;

		SandboxRemoteFiddleConfig srfc = new SandboxRemoteFiddleConfig();
		srfc.setHostname(XMLParserUtil.getFirstElementText(ele, "hostname"));

		String ping = XMLParserUtil.getFirstElementText(ele, "pingIterval");

		if (ping != null) {
			try {
				srfc.setPingInterval(Integer.parseInt(ping));
			} catch (NumberFormatException ex) {
				System.err.println("ping interval is not valid number , ignored it.");
			}
		}
		return srfc;

	}

}
