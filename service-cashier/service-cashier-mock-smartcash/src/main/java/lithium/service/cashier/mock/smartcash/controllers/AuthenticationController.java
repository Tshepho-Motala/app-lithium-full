package lithium.service.cashier.mock.smartcash.controllers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lithium.service.cashier.mock.smartcash.configuration.AuthenticationConfiguration;
import lithium.service.cashier.mock.smartcash.configuration.SmartcashConfigurationProperties;
import lithium.service.cashier.mock.smartcash.data.exceptions.SmartcashErrorMessageException;
import lithium.service.cashier.processor.smartcash.data.SmartcashAuthorizationRequest;
import lithium.service.cashier.processor.smartcash.data.SmartcashAuthorizationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/oauth2/token")
public class AuthenticationController {

    @Autowired
    private SmartcashConfigurationProperties properties;

    @RequestMapping
    public SmartcashAuthorizationResponse authToken(@RequestBody SmartcashAuthorizationRequest authorizationRequest) {
        if (!properties.getAuthentication().getClientId().equals(authorizationRequest.getClientId()) || !properties.getAuthentication().getClientSecret().equals(authorizationRequest.getClientSecret())) {
            throw new SmartcashErrorMessageException(400, "invalid_client", "Invalid client authentication");
        }
        return SmartcashAuthorizationResponse.builder()
            .tokenType("bearer")
            .expiresIn(properties.getAuthentication().getExpiresIn() / 100)
            .accessToken(getJWTToken(properties.getAuthentication().getClientId()))
            .build();
    }


    private String getJWTToken(String username) {
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
            .commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts
            .builder()
            .setId("smartcashJWT")
            .setSubject(username)
            .claim("authorities",
                grantedAuthorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + properties.getAuthentication().getExpiresIn()))
            .signWith(SignatureAlgorithm.HS512,
                properties.getAuthentication().getClientSecret().getBytes()).compact();

        return token;
    }
}
