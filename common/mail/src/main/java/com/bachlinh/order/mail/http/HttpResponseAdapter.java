package com.bachlinh.order.mail.http;

import com.google.api.client.http.LowLevelHttpResponse;
import com.google.common.net.HttpHeaders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class HttpResponseAdapter extends LowLevelHttpResponse {

    private final HttpResponse<byte[]> response;

    private HttpResponseAdapter(HttpResponse<byte[]> response) {
        this.response = response;
    }

    public static HttpResponseAdapter wrap(HttpResponse<byte[]> response) {
        return new HttpResponseAdapter(response);
    }

    @Override
    public InputStream getContent() throws IOException {
        return new ByteArrayInputStream(response.body());
    }

    @Override
    public String getContentEncoding() throws IOException {
        return response.headers().firstValue(HttpHeaders.CONTENT_ENCODING).orElse(null);
    }

    @Override
    public long getContentLength() throws IOException {
        return Long.parseLong(response.headers().firstValue(HttpHeaders.CONTENT_LENGTH).orElse("-1"));
    }

    @Override
    public String getContentType() throws IOException {
        return response.headers().firstValue(HttpHeaders.CONTENT_TYPE).orElse(null);
    }

    @Override
    public String getStatusLine() throws IOException {
        return getReasonPhrase();
    }

    @Override
    public int getStatusCode() throws IOException {
        return response.statusCode();
    }

    @Override
    public String getReasonPhrase() throws IOException {
        int status = getStatusCode();
        if (status >= 100 && status < 200) {
            return "Informational";
        }
        if (status >= 200 && status < 300) {
            return "Successful";
        }
        if (status >= 300 && status < 400) {
            return "Redirection";
        }
        if (status >= 400 && status < 500) {
            return "Client error";
        }
        if (status >= 500 && status < 600) {
            return "Server error";
        }
        return null;
    }

    @Override
    public int getHeaderCount() throws IOException {
        return response.headers().map().size();
    }

    @Override
    public String getHeaderName(int index) throws IOException {
        return new ArrayList<>(response.headers().map().keySet()).get(index);
    }

    @Override
    public String getHeaderValue(int index) throws IOException {
        return response.headers().map().get(getHeaderName(index)).get(0);
    }
}
