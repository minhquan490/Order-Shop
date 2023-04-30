package com.bachlinh.order.web.dto.resp;

import java.util.Arrays;
import java.util.Objects;

public record ProductMediaResp(String contentType, byte[] data, boolean isComplete) {

    @Override
    public String toString() {
        return "ProductMediaResp{" +
                "contentType='" + contentType + '\'' +
                ", data=" + Arrays.toString(data) +
                ", isComplete=" + isComplete +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductMediaResp that)) return false;
        return isComplete() == that.isComplete() && Objects.equals(contentType, that.contentType) && Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(contentType, isComplete());
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
