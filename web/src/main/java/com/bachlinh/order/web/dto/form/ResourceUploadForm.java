package com.bachlinh.order.web.dto.form;

import java.util.Arrays;
import java.util.Objects;

public record ResourceUploadForm(byte[] data, String contentType) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceUploadForm that)) return false;
        return Arrays.equals(data, that.data) && Objects.equals(contentType, that.contentType);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(contentType);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
