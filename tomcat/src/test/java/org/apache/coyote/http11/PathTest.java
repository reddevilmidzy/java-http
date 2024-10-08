package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PathTest {

    @ParameterizedTest
    @DisplayName("확장자 있는 상태로 요청이 오면 경로를 반환한다.")
    @CsvSource(value = {"/css/styles.css|css", "/index.html|html"}, delimiter = '|')
    void requestUrlWithExtension(final String path, final String expected) {
        //given
        final var request = new Path(path);

        //when && then
        assertAll(
                () -> assertThat(request.getUrl().getPath()).endsWith(path),
                () -> assertThat(request.getExtension()).isEqualTo(expected),
                () -> assertThat(request.getRequestParam()).isEmpty()
        );
    }

    @Test
    @DisplayName("쿼리스트링 요청이 오면 파싱하여 변환한다.")
    void requestQueryParam() {
        //given
        final var query = "login?account=redddy&password=password";
        final var request = new Path(query);

        //when
        final var expected = Map.of("account", "redddy", "password", "password");

        //then
        assertThat(request.getRequestParam()).isEqualTo(expected);
    }
}
