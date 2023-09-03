package com.bachlinh.order.core.http.handler;

import java.io.Serializable;
import java.util.Arrays;

public record ExceptionReturn(int status, String[] messages) implements Serializable {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExceptionReturn that = (ExceptionReturn) o;

        if (status != that.status) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(messages, that.messages);
    }

    @Override
    public int hashCode() {
        int result = status;
        result = 31 * result + Arrays.hashCode(messages);
        return result;
    }

    @Override
    public String toString() {
        return "ExceptionReturn{" +
                "status=" + status +
                ", messages=" + Arrays.toString(messages) +
                '}';
    }
}
