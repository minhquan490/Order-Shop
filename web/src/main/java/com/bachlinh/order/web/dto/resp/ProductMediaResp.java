package com.bachlinh.order.web.dto.resp;

public class ProductMediaResp {
    private String contentType;
    private byte[] data;
    private long totalSize;
    private boolean isComplete;

    public ProductMediaResp() {
    }

    public String getContentType() {
        return this.contentType;
    }

    public byte[] getData() {
        return this.data;
    }

    public long getTotalSize() {
        return this.totalSize;
    }

    public boolean isComplete() {
        return this.isComplete;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }
}
