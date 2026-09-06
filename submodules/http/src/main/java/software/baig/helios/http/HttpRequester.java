package software.baig.helios.http;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.stream.Collectors;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.baig.helios.utilities.EnvironmentUtil;


public abstract class HttpRequester {

    private static final Logger LOG = LoggerFactory.getLogger(HttpRequester.class);


    private HttpClient jdkClient;


    public HttpRequester() throws Exception {
        initializeHttpClient();
    }

    public <T> HttpResponse<T> dispatch(HttpRequest httpRequest, HttpResponse.BodyHandler<T> bodyHandler) throws IOException, InterruptedException {
        if (jdkClient == null) {
            throw new IllegalStateException("HttpClient not initialized, call 'initilize()' first.");
        }

        return jdkClient.send(httpRequest, bodyHandler);
    }

    public HttpResponse<String> dispatchS(HttpRequest httpRequest) throws IOException, InterruptedException {
        if (jdkClient == null) {
            throw new IllegalStateException("HttpClient not initialized, call 'initilize()' first.");
        }

        return jdkClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<Void> dispatchV(HttpRequest httpRequest) throws IOException, InterruptedException {
        if (jdkClient == null) {
            throw new IllegalStateException("HttpClient not initialized, call 'initilize()' first.");
        }

        return jdkClient.send(httpRequest, HttpResponse.BodyHandlers.discarding());
    }

    // ----------
    // Utility Methods
    // ----------

    protected String buildFormURLEncodedBody(Map<String, String> data) {
        return data.entrySet()
                .stream()
                .map(entry -> urlEncode(entry.getKey()) + "=" + urlEncode(entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    protected static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }


    // ----------
    // HTTP Client Configuration
    // ----------

    protected void initializeHttpClient() throws NoSuchAlgorithmException, KeyManagementException {
        if (jdkClient != null) {
            LOG.trace("HttpClient already initialized, attempting re-initialization");
            jdkClient.close();
        }

        // (re-)init
        jdkClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NEVER)
                .proxy(buildProxySelector())
                .sslContext(buildSSLContext())
                .build();
    }

    ProxySelector buildProxySelector() {
        final String proxyHost = EnvironmentUtil.getSystemProperty("http.proxyHost", null); // "proxy.some.host"
        final int proxyPort = EnvironmentUtil.getSystemProperty("http.proxyPort", -1, Integer::parseInt); // 8080

        if (proxyHost == null || proxyPort == -1) {
            return ProxySelector.getDefault();
        }

        final String[] nonProxyHosts = EnvironmentUtil.getSystemProperty("http.nonProxyHosts", "").split(";");

        return new FixedProxySelector(proxyHost, proxyPort, nonProxyHosts);
    }

    SSLContext buildSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new SecureRandom());

        return sslContext;
    }

}
