package org.zkoss.fiddler.executor.server;


/**
 * A configuration object to handle the complicated parse job , and make thing
 * easier.
 */

public class Configs {

	private static boolean _logMode = Boolean.getBoolean("debugmsg");

	public static boolean isLogMode() {
		return _logMode;
	}

}