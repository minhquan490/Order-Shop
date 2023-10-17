package com.bachlinh.order.annotation.processor.writer;

import com.bachlinh.order.annotation.processor.meta.FieldMeta;
import com.bachlinh.order.annotation.processor.parser.ClassMetadataParser;
import com.bachlinh.order.core.annotation.FullTextField;

import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

class EntityProxyClassWriter implements ClassWriter {
    private final JavaFileObject source;

    EntityProxyClassWriter(JavaFileObject source) {
        this.source = source;
    }

    @Override
    public void write(Element element) throws IOException {
        var parser = ClassMetadataParser.entityParser(element);
        try (var writer = source.openWriter()) {
            writePackage(writer, parser.getPackage());
            writeImport(writer, parser.getImports());
            writeClass(writer, parser.getClassName());
            writeField(writer, parser.getFields().get(0));
            writeConstructor(writer, parser.getClassName());
            writeMethod(writer, parser.getFields().get(0), element);
            writeSetter(writer, parser.getFields().get(0));
            writeGetWrappedObjectType(writer, parser.getFields().get(0));
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

    private void writeClass(Writer writer, String className) throws IOException {
        var template = "public class {0} extends AbstractEntityProxy ";
        writer.write("@ActiveReflection");
        writer.write(System.lineSeparator());
        writer.write(MessageFormat.format(template, className.substring(className.lastIndexOf(".") + 1)));
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

    private void writeMethod(Writer writer, FieldMeta fieldMeta, Element element) throws IOException {
        writer.write('\t');
        var methodWriter = new InterMethodWriter(writer, fieldMeta);
        methodWriter.write(element);
        writeDoubleSeparator(writer);
    }

    private void writeSetter(Writer writer, FieldMeta meta) throws IOException {
        var template = "public void setTarget(Object {0}) ";
        var tab = '\t';
        var contentTemplate = "this.delegate = ({0}) {1};";
        writeOverride(writer);
        writeTab(writer);
        writer.write(MessageFormat.format(template, meta.name()));
        writer.write('{');
        writer.write(System.lineSeparator());
        writer.write(tab);
        writer.write(tab);
        writer.write(MessageFormat.format(contentTemplate, meta.type(), meta.name()));
        writer.write(System.lineSeparator());
        writer.write(tab);
        writer.write('}');
        writeDoubleSeparator(writer);
    }

    private void writeGetWrappedObjectType(Writer writer, FieldMeta meta) throws IOException {
        var returnTemplate = "return {0}.class;";
        var methodName = "public Class<?> getWrappedObjectType() {";
        writeOverride(writer);
        writeTab(writer);
        writer.write(methodName);
        writer.write(System.lineSeparator());
        writeTab(writer);
        writeTab(writer);
        writer.write(MessageFormat.format(returnTemplate, meta.type()));
        writer.write(System.lineSeparator());
        writeTab(writer);
        writer.write('}');
        writeDoubleSeparator(writer);
    }

    private void writeDoubleSeparator(Writer writer) throws IOException {
        writer.write(System.lineSeparator());
        writer.write(System.lineSeparator());
    }

    private void writeTab(Writer writer) throws IOException {
        var tab = '\t';
        writer.write(tab);
    }

    private void writeOverride(Writer writer) throws IOException {
        writeTab(writer);
        writer.write("@Override");
        writer.write(System.lineSeparator());
    }

    private record InternalFieldWriter(Writer writer) implements FieldWriter {

        @Override
        public void write(FieldMeta meta) throws IOException {
            var template = "private {0} {1};";
            writer.write('\t');
            writer.write(MessageFormat.format(template, meta.type(), meta.name()));
        }
    }

    private record InterMethodWriter(Writer writer, FieldMeta fieldMeta) implements MethodWriter {

        @Override
        public void write(Element element) throws IOException {
            var methodName = "public Map<String, Object> getStoreableFieldValue() {";
            writer.write("@Override");
            writer.write(System.lineSeparator());
            writeTab(writer);
            writer.write(methodName);
            writer.write(System.lineSeparator());
            writeDoubleTab(writer);
            writer.write("Map<String, Object> data = new HashMap<>();");
            writer.write(System.lineSeparator());
            var fields = getFullTextFieldNames(element);
            writeMethodContent(fields, writer);
            writeDoubleTab(writer);
            writer.write("return data;");
            writer.write(System.lineSeparator());
            writeTab(writer);
            writer.write('}');
        }

        private void writeTab(Writer writer) throws IOException {
            var tab = '\t';
            writer.write(tab);
        }

        private void writeDoubleTab(Writer writer) throws IOException {
            var tab = '\t';
            writer.write(tab);
            writer.write(tab);
        }

        private void writeMethodContent(List<CharSequence> fieldNames, Writer writer) throws IOException {
            if (fieldNames.isEmpty()) {
                writer.write(System.lineSeparator());
                return;
            }
            var template = "data.put(\"{0}\", {1});";
            var methods = getFullTextMethods(fieldNames);
            for (int i = 0; i < fieldNames.size(); i++) {
                writeDoubleTab(writer);
                writer.write(MessageFormat.format(template, fieldNames.get(i), methods.get(i)));
                writer.write(System.lineSeparator());
            }
        }

        private List<CharSequence> getFullTextFieldNames(Element element) {
            return element.getEnclosedElements()
                    .stream()
                    .filter(variableElement -> variableElement.getAnnotation(FullTextField.class) != null)
                    .map(Element::getSimpleName)
                    .map(CharSequence.class::cast)
                    .toList();
        }

        private List<CharSequence> getFullTextMethods(Collection<CharSequence> fieldNames) {
            return fieldNames.stream()
                    .map(charSequence -> {
                        var template = "delegate.get{0}{1}()";
                        var string = charSequence.toString();
                        return MessageFormat.format(template, string.substring(0, 1).toUpperCase(Locale.getDefault()), string.substring(1));
                    })
                    .map(CharSequence.class::cast)
                    .toList();
        }
    }
}
