package org.apache.coyote.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private static final String HEADER_SEPARATOR = ":";

    private final Map<String, String> headers;

    public HttpHeaders() {
        this.headers = new HashMap<>();
    }

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders parse(final BufferedReader reader) throws IOException {
        final var httpRequestHeaders = new HashMap<String, String>();
        var line = "";
        while ((line = reader.readLine()) != null && !line.isBlank()) {
            final int index = line.indexOf(HEADER_SEPARATOR);
            httpRequestHeaders.put(line.substring(0, index).strip(), line.substring(index + 1).strip());
        }
        return new HttpHeaders(httpRequestHeaders);
    }

    public boolean contains(final String name) {
        return headers.containsKey(name);
    }

    public String get(final String key) {
        return headers.get(key);
    }

    public void set(final String key, final String value) {
        headers.put(key, value);
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}
