package org.zkoss.fiddler.executor.utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public class PortUtil {

	public static int findAAvaiablePort(int start, int end, int retry) {

		int range = end - start + 1;
		int port = -1;

		for (int i = 0; i < retry || retry == -1; ++i) {
			port = start + (int) (Math.random() * range);
			if (available(port))
				return port;
		}

		throw new IllegalStateException("no available port");
	}

	public static boolean available(int port) {
		if (port <= 0) {
			throw new IllegalArgumentException("Invalid start port: " + port);
		}

		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
				}
			}
		}

		return false;
	}

}
