package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final String BODY_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final Map<String, String> body;

    public HttpRequest(final RequestLine requestLine, final HttpHeaders headers, final Map<String, String> body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final var requestLine = RequestLine.from(reader.readLine());
        final var httpHeaders = HttpHeaders.parse(reader);
        final var requestBody = parseRequestBody(httpHeaders, reader);
        return new HttpRequest(requestLine, httpHeaders, requestBody);
    }

    private static Map<String, String> parseRequestBody(final HttpHeaders headers, final BufferedReader bufferedReader)
            throws IOException {
        if (!headers.contains(HttpHeaderField.CONTENT_LENGTH.getValue())) {
            return Map.of();
        }
        final int contentLength = Integer.parseInt(headers.get(HttpHeaderField.CONTENT_LENGTH.getValue()));
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final var requestBody = new String(buffer);
        return generate(requestBody);
    }

    private static Map<String, String> generate(final String requestBody) {
        final var body = new HashMap<String, String>();
        final var params = requestBody.split(BODY_SEPARATOR);
        for (final var param : params) {
            final var key = param.split(KEY_VALUE_SEPARATOR)[KEY_INDEX];
            final var value = param.split(KEY_VALUE_SEPARATOR)[VALUE_INDEX];
            body.put(key.strip(), value.strip());
        }
        return body;
    }

    public String getPath() {
        return requestLine.getPath().getRequestPath();
    }

    public int getContentLength() throws IOException {
        final var absolutePath = requestLine.getAbsolutePath();
        if (absolutePath == null) {
            return "".getBytes().length;
        }
        final String file = new String(Files.readAllBytes(new File(absolutePath.getFile()).toPath()));
        return file.getBytes().length;
    }

    private String getFile(final Path path) throws IOException {
        final var resource = path.getAbsolutePath();
        if (resource == null) {
            return "";
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    public String getResources() throws IOException {
        return getFile(requestLine.getPath());
    }

    public boolean hasMethod(final HttpMethod method) {
        return requestLine.isEqualHttpMethod(method);
    }

    public String getContentTypeToResponseText() {
        final var path = requestLine.getPath();
        final var contentType = ContentType.from(path);
        return contentType.toResponseText();
    }

    public String getBodyValue(final String key) {
        return body.get(key);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public Map<String, String> getBody() {
        return body;
    }
}
