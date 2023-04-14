package lithium.tokens;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import java.security.Principal;

/**
 * A simpler service to obtain a Lithium Token, abstracting the need for a controller API to inject a token
 * store.
 * <pre>
 * {@code
 *  @Autowired
 *  private LithiumTokenUtilService tokenService;
 *
 *  ...
 *
 *  @GetMapping
 *  private DoSomething(Principal principal) {
 *      LithiumTokenUtil token = tokenService.getUtil(principal);
 *  }
 * }
 * </pre>
 * @see LithiumTokenUtil
 * @see JWTUser
 */
@Service
public class LithiumTokenUtilService {

    @Autowired
    TokenStore tokenStore;

    public LithiumTokenUtil getUtil(Principal principal) {
        LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
        return util;
    }

    public LithiumTokenUtil getUtilForCurrentPrincipal() {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

         if(authentication == null) {
             return null;
         }

         return LithiumTokenUtil.builder(tokenStore, authentication).build();
    }

    public JWTUser getUser(Principal principal) {
        return getUtil(principal).getJwtUser();
    }

}
