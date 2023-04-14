package lithium.service.casino.provider.iforium.infrastructure.logging;

import lithium.service.casino.provider.iforium.infrastructure.servlet.CachedBodyHttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Order(1)
@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!log.isDebugEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        CachedBodyHttpServletRequestWrapper cachedBodyHttpServletRequestWrapper = new CachedBodyHttpServletRequestWrapper(request);
        ContentCachingResponseWrapper contentCachingResponseWrapper = new ContentCachingResponseWrapper(response);

        logRequest(cachedBodyHttpServletRequestWrapper);
        filterChain.doFilter(cachedBodyHttpServletRequestWrapper, contentCachingResponseWrapper);
        logResponse(contentCachingResponseWrapper);

        contentCachingResponseWrapper.copyBodyToResponse();
    }

    private static void logRequest(HttpServletRequest request) throws IOException {
        StringBuilder msg = new StringBuilder("request ");
        msg.append(request.getMethod()).append(" ");
        msg.append(request.getRequestURI());

        String queryString = request.getQueryString();
        if (queryString != null) {
            msg.append('?').append(queryString);
        }

        HttpHeaders headers = new ServletServerHttpRequest(request).getHeaders();
        msg.append(", headers=").append(headers);

        msg.append(", body=").append(IOUtils.toString(request.getInputStream()));

        log.debug(msg.toString());
    }

    private static void logResponse(ContentCachingResponseWrapper response) throws IOException {
        StringBuilder msg = new StringBuilder("response ");
        msg.append("httpStatusCode=").append(response.getStatus());
        msg.append(", headers=").append(getResponseHeaders(response));
        msg.append(", body=").append(IOUtils.toString(response.getContentInputStream()));

        log.debug(msg.toString());
    }

    private static HttpHeaders getResponseHeaders(ContentCachingResponseWrapper response) {
        HttpHeaders result = new HttpHeaders();
        response.getHeaderNames()
                .forEach(headerName -> result.put(headerName, new ArrayList<>(response.getHeaders(headerName))));
        return result;
    }
}
