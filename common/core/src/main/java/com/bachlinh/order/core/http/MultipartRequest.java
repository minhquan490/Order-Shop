package com.bachlinh.order.core.http;

public class MultipartRequest {
    private String fileName;
    private String contentType;
    private long contentLength;
    private boolean isChunked;
    private byte[] bodyContent;

    public MultipartRequest() {
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getContentType() {
        return this.contentType;
    }

    public long getContentLength() {
        return this.contentLength;
    }

    public boolean isChunked() {
        return this.isChunked;
    }

    public byte[] getBodyContent() {
        return this.bodyContent;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public void setChunked(boolean isChunked) {
        this.isChunked = isChunked;
    }

    public void setBodyContent(byte[] bodyContent) {
        this.bodyContent = bodyContent;
    }
}
