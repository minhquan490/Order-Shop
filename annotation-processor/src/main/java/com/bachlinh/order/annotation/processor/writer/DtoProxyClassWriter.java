package com.bachlinh.order.annotation.processor.writer;

import com.bachlinh.order.annotation.processor.meta.FieldMeta;
import com.bachlinh.order.annotation.processor.parser.ClassMetadataParser;
import com.bachlinh.order.annotation.processor.parser.GetterMetadataParser;
import com.bachlinh.order.core.annotation.MappedDtoField;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Collection;

class DtoProxyClassWriter implements ClassWriter {
    private static final String PROXY = "Proxy";
    private static final String OVERRIDE = "@Override";
    private static final String JSON_IGNORE = "@JsonIgnore";
    private final JavaFileObject source;
    private final Elements elements;
    private final GetterMetadataParser getterMetadataParser;
    private final TypeElement delegateType;

    DtoProxyClassWriter(JavaFileObject source, Elements elements, TypeElement delegateType) {
        this.source = source;
        this.elements = elements;
        this.getterMetadataParser = GetterMetadataParser.dtoGetterParser();
        this.delegateType = delegateType;
    }

    @Override
    public void write(Element element) throws IOException {
        var parser = ClassMetadataParser.dtoParser(element, elements, delegateType);
        try (var writer = source.openWriter()) {
            writePackage(writer, parser.getPackage());
            writeImport(writer, parser.getImports());
            writeClass(writer, parser.getClassName(), parser.getField());
            writeField(writer, parser.getField());
            writeConstructor(writer, parser.getClassName());
            writeMethod(writer, element);
            writeWrapMethod(writer, parser.getClassName(), parser.getField());
            writeProxyForTypeMethod(writer, parser.getClassName());
            writeFactoryMethod(writer, parser.getClassName());
            writer.write('}');
        }
    }

    private void writePackage(Writer writer, String packageName) throws IOException {
        var template = "package {0};";
        writer.write(MessageFormat.format(template, packageName));
        writeDoubleSeparator(writer);
    }

    private void writeImport(Writer writer, Collection<String> classNames) throws IOException {
        var template = "import {0};";
        for (String className : classNames) {
            writer.write(MessageFormat.format(template, className));
            writer.write(System.lineSeparator());
        }
        writer.write(System.lineSeparator());
    }

    private void writeClass(Writer writer, String className, FieldMeta fieldMeta) throws IOException {
        var template = "public class {0} extends {1} implements Proxy<{2}, {3}> ";
        var name = className.substring(className.lastIndexOf(".") + 1);
        var dtoName = name.replace(PROXY, "");
        writer.write("@ActiveReflection");
        writer.write(System.lineSeparator());
        writer.write("@DtoProxy");
        writer.write(System.lineSeparator());
        writer.write("@JsonInclude(Include.NON_NULL)");
        writer.write(System.lineSeparator());
        writer.write(MessageFormat.format(template, name, dtoName, dtoName, fieldMeta.type()));
        writer.write('{');
        writeDoubleSeparator(writer);
    }

    private void writeField(Writer writer, FieldMeta fieldMeta) throws IOException {
        var fieldWriter = new InternalFieldWriter(writer);
        fieldWriter.write(fieldMeta);
        writeDoubleSeparator(writer);
    }

    private void writeConstructor(Writer writer, String className) throws IOException {
        var constructorName = className.substring(className.lastIndexOf(".") + 1);
        var template = "public {0}() ";
        writeTab(writer);
        writer.write("@ActiveReflection");
        writer.write(System.lineSeparator());
        writeTab(writer);
        writer.write(MessageFormat.format(template, constructorName));
        writer.write("{}");
        writeDoubleSeparator(writer);
    }

    private void writeMethod(Writer writer, Element element) throws IOException {
        var methodWriter = new InternalMethodWriter(writer, getterMetadataParser);
        methodWriter.write(element);
    }

    private void writeWrapMethod(Writer writer, String className, FieldMeta fieldMeta) throws IOException {
        var name = className.substring(className.lastIndexOf(".") + 1);
        var dtoName = name.replace(PROXY, "");
        var template = "public %s wrap(%s source) {";
        writeTab(writer);
        writer.write(OVERRIDE);
        writer.write(System.lineSeparator());
        writeTab(writer);
        writer.write(JSON_IGNORE);
        writer.write(System.lineSeparator());
        writeTab(writer);
        writer.write(String.format(template, dtoName, fieldMeta.type()));
        writer.write(System.lineSeparator());
        writeTab(writer);
        writeTab(writer);
        writer.write("this.delegate = source;");
        writer.write(System.lineSeparator());
        writeTab(writer);
        writeTab(writer);
        writer.write("return this;");
        writer.write(System.lineSeparator());
        writeTab(writer);
        writer.write('}');
        writeDoubleSeparator(writer);
    }

    private void writeProxyForTypeMethod(Writer writer, String className) throws IOException {
        var name = className.substring(className.lastIndexOf(".") + 1);
        var dtoName = name.replace(PROXY, "");
        var template = "public Class<%s> proxyForType() {";
        var returnTemplate = "return %s.class;";
        writeTab(writer);
        writer.write(OVERRIDE);
        writer.write(System.lineSeparator());
        writeTab(writer);
        writer.write(String.format(template, dtoName));
        writer.write(System.lineSeparator());
        writeTab(writer);
        writeTab(writer);
        writer.write(String.format(returnTemplate, dtoName));
        writer.write(System.lineSeparator());
        writeTab(writer);
        writer.write('}');
        writer.write(System.lineSeparator());
    }

    private void writeFactoryMethod(Writer writer, String className) throws IOException {
        var constructorName = className.substring(className.lastIndexOf(".") + 1);
        var template = "return new {0}();";
        writer.write(System.lineSeparator());
        writeTab(writer);
        writer.write(JSON_IGNORE);
        writer.write(System.lineSeparator());
        writeTab(writer);
        writer.write(OVERRIDE);
        writer.write(System.lineSeparator());
        writeTab(writer);
        writer.write("public Proxy<?, ?> getInstance() {");
        writer.write(System.lineSeparator());
        writeTab(writer);
        writeTab(writer);
        writer.write(MessageFormat.format(template, constructorName));
        writer.write(System.lineSeparator());
        writeTab(writer);
        writer.write('}');
        writer.write(System.lineSeparator());
    }

    private void writeDoubleSeparator(Writer writer) throws IOException {
        writer.write(System.lineSeparator());
        writer.write(System.lineSeparator());
    }

    private void writeTab(Writer writer) throws IOException {
        var tab = '\t';
        writer.write(tab);
    }

    private record InternalFieldWriter(Writer writer) implements FieldWriter {

        @Override
        public void write(FieldMeta meta) throws IOException {
            writeTab(writer);
            writer.write(JSON_IGNORE);
            writer.write(System.lineSeparator());
            var template = "private {0} {1};";
            writeTab(writer);
            writer.write(MessageFormat.format(template, meta.type(), meta.name()));
        }

        private void writeTab(Writer writer) throws IOException {
            var tab = '\t';
            writer.write(tab);
        }
    }

    private record InternalMethodWriter(Writer writer, GetterMetadataParser parser) implements MethodWriter {

        @Override
        public void write(Element element) throws IOException {
            var getters = parser.parse("^((get)|(is))+(([A-Z][a-z]+)|([A-Z]))+$", element);
            var jsonTemplate = "@JsonProperty(\"{0}\")";
            var methodTemplate = "public %s %s() {";
            var returnTemplate = "return this.delegate.{0};";
            for (var getter : getters) {
                var dtoField = getter.mappedDtoField();
                writeTab(writer);
                writer.write(OVERRIDE);
                writer.write(System.lineSeparator());
                writeTab(writer);
                writer.write(MessageFormat.format(jsonTemplate, dtoField.outputJsonField()));
                writer.write(System.lineSeparator());
                writeTab(writer);
                writer.write(String.format(methodTemplate, getter.returnType().toString(), getter.methodName()));
                writer.write(System.lineSeparator());
                writeTab(writer);
                writeTab(writer);
                if (getter.returnType().toString().equals("boolean") || getter.returnType().toString().equals(Boolean.class.getName())) {
                    var mappedField = getter.mappedDtoField();
                    var targetMethodName = resolveMethodName("is", mappedField);
                    writer.write(MessageFormat.format(returnTemplate, targetMethodName));
                } else {
                    var mappedField = getter.mappedDtoField();
                    var targetMethodName = resolveMethodName("get", mappedField);
                    writer.write(MessageFormat.format(returnTemplate, targetMethodName));
                }
                writer.write(System.lineSeparator());
                writeTab(writer);
                writer.write('}');
                writer.write(System.lineSeparator());
                writer.write(System.lineSeparator());
            }
        }

        private void writeTab(Writer writer) throws IOException {
            var tab = '\t';
            writer.write(tab);
        }

        private String resolveMethodName(String prefix, MappedDtoField mappedDtoField) {
            var targetMethodName = prefix.concat(String.valueOf(mappedDtoField.targetField().charAt(0)).toUpperCase()).concat(mappedDtoField.targetField().substring(1));
            if (targetMethodName.contains(".")) {
                var parts = targetMethodName.split("\\.");
                targetMethodName = String.join("().", parts);
            }
            return targetMethodName.concat("()");
        }
    }
}
