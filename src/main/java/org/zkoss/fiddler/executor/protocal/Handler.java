package org.zkoss.fiddler.executor.protocal;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler {
    private final ClassLoader classLoader;

    public Handler(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    protected URLConnection openConnection(URL u) throws IOException {
        final URL resourceUrl = classLoader.getResource(u.getPath());
        return resourceUrl.openConnection();
    }
}