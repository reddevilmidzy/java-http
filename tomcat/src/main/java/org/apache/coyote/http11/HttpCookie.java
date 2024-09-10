package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> header;

    public HttpCookie(final Map<String, String> header) {
        this.header = header;
    }

    public static HttpCookie parse(final String str) {
        if (str == null || str.isBlank()) {
            return new HttpCookie(Collections.emptyMap());
        }
        final var header = new HashMap<String, String>();
        for (final var cookie : str.split(";")) {
            final var key = cookie.split("=")[0].strip();
            final var value = cookie.split("=")[1].strip();
            header.put(key, value);
        }
        return new HttpCookie(header);
    }

    public boolean containsKey(final String key) {
        return header.containsKey(key);
    }

    public String get(final String key) {
        return header.get(key);
    }

    public Map<String, String> getHeader() {
        return Collections.unmodifiableMap(header);
    }

    @Override
    public String toString() {
        return "HttpCookie{" +
               "header=" + header +
               '}';
    }
}
