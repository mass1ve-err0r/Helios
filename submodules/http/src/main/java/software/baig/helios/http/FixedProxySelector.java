package software.baig.helios.http;

import java.io.IOException;
import java.net.*;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class FixedProxySelector extends ProxySelector {

    private static final Logger LOG = LoggerFactory.getLogger(FixedProxySelector.class);


    private final Proxy proxy;
    private final String[] nonProxyHosts;


    public FixedProxySelector(String proxyHost, int proxyPort, String[] nonProxyHosts) {
        this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        this.nonProxyHosts = nonProxyHosts;
    }

    @Override
    public List<Proxy> select(URI uri) {
        String host = uri.getHost();

        if (host == null || isNonProxyHost(host)) {
            return List.of(Proxy.NO_PROXY);
        }

        return List.of(proxy);
    }

    @Override
    public void connectFailed(URI uri, SocketAddress socketAddress, IOException exception) {
        LOG.error("[FixedProxySelector] Connect failed!", exception);
    }

    private boolean isNonProxyHost(String host) {
        for (String pattern: nonProxyHosts) {
            if (matches(host, pattern)) {
                return true;
            }
        }

        return false;
    }

    private boolean matches(String host, String pattern) {
        if (pattern.startsWith("*.")) {
            String suffix = pattern.substring(1);
            return host.endsWith(suffix);
        }

        return host.equalsIgnoreCase(pattern);
    }

}
