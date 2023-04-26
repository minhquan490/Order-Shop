package com.bachlinh.order.core.http.ssl.internal;

import com.bachlinh.order.utils.ResourceUtils;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class CertificateParser {
    public static final int BUFFER_SIZE = 8192;
    private static final String HEADER = "-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+";

    private static final String BASE64_TEXT = "([a-z0-9+/=\\r\\n]+)";

    private static final String FOOTER = "-+END\\s+.*CERTIFICATE[^-]*-+";

    private static final Pattern PATTERN = Pattern.compile(HEADER + BASE64_TEXT + FOOTER, Pattern.CASE_INSENSITIVE);

    private CertificateParser() {
    }

    /**
     * Load certificates from the specified resource.
     *
     * @param path the certificate to parse
     * @return the parsed certificates
     */
    static X509Certificate[] parse(String path) {
        CertificateFactory factory = getCertificateFactory();
        List<X509Certificate> certificates = new ArrayList<>();
        readCertificates(path, factory, certificates::add);
        return certificates.toArray(new X509Certificate[0]);
    }

    private static CertificateFactory getCertificateFactory() {
        try {
            return CertificateFactory.getInstance("X.509");
        } catch (CertificateException ex) {
            throw new IllegalStateException("Unable to get X.509 certificate factory", ex);
        }
    }

    private static void readCertificates(String resource, CertificateFactory factory,
                                         Consumer<X509Certificate> consumer) {
        try {
            String text = readText(resource);
            Matcher matcher = PATTERN.matcher(text);
            while (matcher.find()) {
                String encodedText = matcher.group(1);
                byte[] decodedBytes = decodeBase64(encodedText);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);
                while (inputStream.available() > 0) {
                    consumer.accept((X509Certificate) factory.generateCertificate(inputStream));
                }
            }
        } catch (CertificateException | IOException ex) {
            throw new IllegalStateException("Error reading certificate from '" + resource + "' : " + ex.getMessage(),
                    ex);
        }
    }

    private static String readText(String resource) throws IOException {
        URL url = ResourceUtils.getURL(resource);
        try (Reader reader = new InputStreamReader(url.openStream())) {
            return copyToString(reader);
        }
    }

    private static byte[] decodeBase64(String content) {
        byte[] bytes = content.replace("\r", "").replace("\n", "").getBytes();
        return Base64.getDecoder().decode(bytes);
    }

    private static String copyToString(Reader in) throws IOException {
        if (in == null) {
            return "";
        }

        StringWriter out = new StringWriter(BUFFER_SIZE);
        copy(in, out);
        return out.toString();
    }

    private static int copy(Reader in, Writer out) throws IOException {
        try {
            int charCount = 0;
            char[] buffer = new char[BUFFER_SIZE];
            int charsRead;
            while ((charsRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, charsRead);
                charCount += charsRead;
            }
            out.flush();
            return charCount;
        } finally {
            close(in);
            close(out);
        }
    }

    private static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException ex) {
            // ignore
        }
    }
}
