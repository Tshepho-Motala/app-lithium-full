package lithium.service.cashier.processor.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.function.Predicate.not;

//TODO: may be the basis for merging with lithium.rest.LoggingRequestInterceptor
public interface HttpRequestBodyExtractor {

    default Optional<String> extractBody(@NonNull ClientHttpResponse response){
        return Optional.of(response)
                .filter(this::hasContentLength)
                .filter(not(this::is4xxResponse))
                .map(this::getBody);
    }

    private boolean hasContentLength(ClientHttpResponse response) {
        return response.getHeaders().getContentLength() != 0;
    }

    /* Do not try to get the body from responses with status 4xx.
     * response.getBody() calls HttpUrlConnection.getInputStream0() and throws exception in case of status 4xx
     * see  java.base/sun.net.www.protocol.http.HttpURLConnection.getInputStream0(HttpURLConnection.java:1997)
     */
    private boolean is4xxResponse(ClientHttpResponse response){
        try {
            return is4xxClientError(response.getRawStatusCode());
        } catch (IOException e) {
            return true;
        }
    }
    private Optional<String> getContentSubtype(HttpHeaders headers) {
        return Optional.of(headers)
                .map(HttpHeaders::getContentType)
                .map(MediaType::getSubtype);
    }

    private String getBody(ClientHttpResponse response){
        try {
            return StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

    private  boolean hasTextBody(ClientHttpResponse response) {
        return getContentSubtype(response.getHeaders())
                .filter(of("json"::equals)
                        .or("text"::equals)
                        .or("xml"::equals)
                        .or("x-www-form-urlencoded"::equals))
                .isPresent();
    }

    static <T> Predicate<T> of(final Predicate<T> lambda) {
        return lambda;
    }

    /* Using this method instead of exchange.getStatusCode().is4xxClientError() is due to avoid IllegalArgumentException
     * which is thrown when http status code (e.g. 521) could not be found in HttpStatus Enum */
    private static boolean is4xxClientError(int rawStatusCode) throws IOException {
        return HttpStatus.Series.resolve(rawStatusCode) == HttpStatus.Series.CLIENT_ERROR;
    }
}
