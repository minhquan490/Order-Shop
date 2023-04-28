package com.bachlinh.order.mail.http.ssl;

import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.mail.http.HttpRequestAdapter;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;

import javax.net.ssl.SSLContext;
import java.net.CookieManager;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executors;

public class SslHttpTransport extends HttpTransport {
    private final HttpClient httpClient;

    public SslHttpTransport(SSLContext sslContext) {
        this.httpClient = HttpClient
                .newBuilder()
                .sslContext(sslContext)
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .cookieHandler(new CookieManager())
                .followRedirects(HttpClient.Redirect.NORMAL)
                .executor(Executors.newFixedThreadPool(2))
                .build();
    }

    @Override
    protected LowLevelHttpRequest buildRequest(String method, String url) {
        HttpRequestAdapter request = new HttpRequestAdapter(httpClient);
        request.setUrl(url);
        request.setHttpMethod(RequestMethod.valueOf(method.toUpperCase()));
        return request;
    }
}
