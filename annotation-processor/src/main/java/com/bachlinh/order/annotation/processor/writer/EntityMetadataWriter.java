package com.bachlinh.order.annotation.processor.writer;

import com.bachlinh.order.annotation.processor.meta.FieldMeta;
import com.bachlinh.order.annotation.processor.parser.ClassMetadataParser;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Collection;

public class EntityMetadataWriter implements ClassWriter {

    private final JavaFileObject source;
    private final Elements elements;
    private final Types types;

    EntityMetadataWriter(JavaFileObject source, Elements elements, Types types) {
        this.source = source;
        this.elements = elements;
        this.types = types;
    }

    @Override
    public void write(Element element) throws IOException {
        var parser = ClassMetadataParser.entityMetaModelParser(element, elements, types);
        try (var writer = source.openWriter()) {
            writePackage(writer, parser.getPackage());
            writeClass(writer, parser.getClassName());
            writeFields(writer, parser.getFields());
            writeDoubleSeparator(writer);
            writer.write('}');
        }
    }

    private void writeFields(Writer writer, Collection<FieldMeta> fieldMetas) throws IOException {
        var fieldWriter = new InternalFieldWriter(writer);
        for (FieldMeta meta : fieldMetas) {
            fieldWriter.write(meta);
            writer.write("\n");
        }
    }

    private void writeClass(Writer writer, String className) throws IOException {
        var template = "public final class {0} ";
        writer.write(MessageFormat.format(template, className));
        writer.write('{');
        writeDoubleSeparator(writer);
    }

    private void writePackage(Writer writer, String packageName) throws IOException {
        var template = "package {0};";
        writer.write(MessageFormat.format(template, packageName));
        writeDoubleSeparator(writer);
    }

    private void writeDoubleSeparator(Writer writer) throws IOException {
        writer.write(System.lineSeparator());
        writer.write(System.lineSeparator());
    }

    private record InternalFieldWriter(Writer writer) implements FieldWriter {

        @Override
        public void write(FieldMeta meta) throws IOException {
            var template = "public static final {0} {1} = \"{2}\";";
            writer.write('\t');
            writer.write(MessageFormat.format(template, meta.type(), transferFieldName(meta.name().toString()), meta.name()));
        }

        private String transferFieldName(String fieldValue) {
            char[] chars = fieldValue.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : chars) {
                if (Character.isUpperCase(c)) {
                    builder.append("_");
                    builder.append(c);
                } else {
                    builder.append(Character.toUpperCase(c));
                }
            }
            return builder.toString();
        }
    }
}
