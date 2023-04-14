package lithium.server.security.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;

public class WebResponseExceptionTranslator extends DefaultWebResponseExceptionTranslator {

    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {

        if (e instanceof CustomOAuthException) {
            CustomOAuthException customOauthException = (CustomOAuthException) e;
            return ResponseEntity
                    .status(customOauthException.getHttpErrorCode())
                    .body(customOauthException);
        }

        return super.translate(e);
    }
}
