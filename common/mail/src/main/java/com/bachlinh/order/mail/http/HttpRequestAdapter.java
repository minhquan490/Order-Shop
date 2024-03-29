package com.bachlinh.order.mail.http;

import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.function.Decorator;
import com.bachlinh.order.core.exception.system.mail.MailException;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.common.net.HttpHeaders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestAdapter extends LowLevelHttpRequest {
    private final HttpClient httpClient;
    private final HttpRequest.Builder httpBuilder;
    private String url;
    private RequestMethod httpMethod;

    public HttpRequestAdapter(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.httpBuilder = HttpRequest.newBuilder();
    }

    @Override
    public void addHeader(String name, String value) {
        this.httpBuilder.header(name, value);
    }

    @Override
    public LowLevelHttpResponse execute() throws IOException {
        if (url == null) {
            throw new MailException("Unknown host");
        }
        if (httpMethod == null) {
            throw new MailException("Unknown http method");
        }
        List<Decorator<HttpRequest.Builder>> decorators = new ArrayList<>();
        decorators.add(decorateHeader());
        decorators.add(decorateMethod(httpMethod));
        decorators.add(decorateUrl(url));
        try {
            decorators.forEach(httpRequestDecorator -> {
                try {
                    httpRequestDecorator.decorate(this.httpBuilder);
                } catch (IOException e) {
                    throw new MailException("Process email failure", e);
                }
            });
            HttpResponse<byte[]> response = httpClient.send(httpBuilder.build(), HttpResponse.BodyHandlers.ofByteArray());
            return HttpResponseAdapter.wrap(response);
        } catch (InterruptedException e) {
            throw new MailException("Can not send mail", e);
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHttpMethod(RequestMethod method) {
        this.httpMethod = method;
    }

    private Decorator<HttpRequest.Builder> decorateHeader() {
        return requestBuilder -> {
            if (getContentType() != null) {
                requestBuilder.header(HttpHeaders.CONTENT_TYPE, getContentType());
            }
            if (getContentEncoding() != null) {
                requestBuilder.header(HttpHeaders.CONTENT_ENCODING, getContentEncoding());
            }
            if (getContentLength() > 0) {
                requestBuilder.header(HttpHeaders.CONTENT_LENGTH, String.valueOf(getContentLength()));
            }
        };
    }

    private Decorator<HttpRequest.Builder> decorateMethod(RequestMethod httpMethod) {
        return requestBuilder -> {
            switch (httpMethod) {
                case GET -> requestBuilder.GET();
                case POST -> {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    if (getStreamingContent() != null) {
                        getStreamingContent().writeTo(byteArrayOutputStream);
                        requestBuilder.POST(HttpRequest.BodyPublishers.ofByteArray(byteArrayOutputStream.toByteArray()));
                    } else {
                        requestBuilder.POST(HttpRequest.BodyPublishers.noBody());
                    }
                }
                case PATCH -> {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    if (getStreamingContent() != null) {
                        getStreamingContent().writeTo(byteArrayOutputStream);
                        requestBuilder.PUT(HttpRequest.BodyPublishers.ofByteArray(byteArrayOutputStream.toByteArray()));
                    } else {
                        requestBuilder.PUT(HttpRequest.BodyPublishers.noBody());
                    }
                }
                case DELETE -> requestBuilder.DELETE();
            }
        };
    }

    private Decorator<HttpRequest.Builder> decorateUrl(String url) {
        return requestBuilder -> requestBuilder.uri(URI.create(url));
    }
}
