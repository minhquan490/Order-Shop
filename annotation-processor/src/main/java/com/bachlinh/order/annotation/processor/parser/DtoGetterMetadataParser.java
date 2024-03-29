package com.bachlinh.order.annotation.processor.parser;

import com.bachlinh.order.annotation.processor.meta.GetterMetadata;
import com.bachlinh.order.core.annotation.MappedDtoField;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import java.util.List;
import java.util.regex.Pattern;


class DtoGetterMetadataParser implements GetterMetadataParser {
    private Pattern p;

    @Override
    public List<GetterMetadata> parse(String pattern, Element parent) {
        if (p == null || !p.pattern().equals(pattern)) {
            this.p = Pattern.compile(pattern);
        }
        return parent.getEnclosedElements()
                .stream()
                .filter(element -> element.getKind().equals(ElementKind.METHOD))
                .filter(element -> p.matcher(element.getSimpleName()).matches())
                .filter(element -> {
                    var executable = (ExecutableElement) element;
                    var methodName = executable.getSimpleName().toString();
                    var fieldName = parseFieldName(methodName);
                    return findField(fieldName, parent) != null;
                })
                .map(element -> {
                    var executable = (ExecutableElement) element;
                    var methodName = executable.getSimpleName().toString();
                    var fieldName = parseFieldName(methodName);
                    var field = findField(fieldName, parent);
                    MappedDtoField mappedDtoField = field.getAnnotation(MappedDtoField.class);
                    if (mappedDtoField == null) {
                        throw new IllegalStateException(String.format("MappedDtoField missing on field %s of element %s", field.getSimpleName().toString(), parent.getSimpleName().toString()));
                    }
                    return new GetterMetadata(methodName, executable.getReturnType(), mappedDtoField);
                })
                .toList();
    }

    private Element findField(String fieldName, Element parent) {
        var elements = parent.getEnclosedElements().stream().filter(element -> element.getKind().equals(ElementKind.FIELD)).toList();
        for (var element : elements) {
            if (element.getSimpleName().toString().equals(fieldName) && element.getAnnotation(MappedDtoField.class) != null) {
                return element;
            }
        }
        return null;
    }

    private String parseFieldName(String methodName) {
        var fieldName = "";
        if (methodName.startsWith("get")) {
            fieldName = fieldName.concat(String.valueOf(methodName.charAt(3)).toLowerCase()).concat(methodName.substring("get".length() + 1));
        }
        if (methodName.startsWith("is")) {
            fieldName = fieldName.concat(String.valueOf(methodName.charAt(2)).toLowerCase()).concat(methodName.substring("is".length() + 1));
        }
        return fieldName;
    }
}
