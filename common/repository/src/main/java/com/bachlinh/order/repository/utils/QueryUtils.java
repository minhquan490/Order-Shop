package com.bachlinh.order.repository.utils;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.repository.query.QueryBinding;
import com.bachlinh.order.repository.query.Select;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QueryUtils {

    public static Map<String, Object> parse(Collection<QueryBinding> queryBindings) {
        Map<String, Object> results = new HashMap<>();
        if (!queryBindings.isEmpty()) {
            for (var binding : queryBindings) {
                results.put(binding.attribute(), binding.value());
            }
        }
        return results;
    }

    public static Select resolveSelectAlias(Select select, TableMetadataHolder targetMetadata) {
        if (!StringUtils.hasText(select.getAlias())) {
            select = Select.builder()
                    .column(select.getColumn())
                    .alias(targetMetadata.getTableName()
                            .concat(".")
                            .concat(targetMetadata.getColumn(select.getColumn())))
                    .build();
        }
        return select;
    }

    public static long calculateOffset(long pageNum, long pageSize) {
        if (pageNum < 0 || pageSize < 0) {
            return 0;
        }
        return (pageNum - 1) * pageSize;
    }

    public static String parseQueryToEntitySimpleName(String query) {
        if (!query.contains("FROM")) {
            return "";
        }
        String[] parts = query.split("FROM")[1].split(" ")[1].toLowerCase().split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            result.append(org.apache.commons.lang3.StringUtils.capitalize(part));
        }
        return result.toString();
    }

    public static String bindAttributes(String query, Map<String, Object> attributes) {
        for (var entry : attributes.entrySet()) {
            query = query.replace(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }
        return query;
    }
}
