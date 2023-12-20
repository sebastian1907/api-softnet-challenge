package com.softnet.app.config.security.auth;

import com.softnet.app.config.security.support.JwtVerifyHandler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CurrentUserAuthenticationBearer {
    public static Mono<Authentication> create(JwtVerifyHandler.VerificationResult verificationResult) {
        var claims = verificationResult.claims;
        var subject = claims.getSubject();
        List<String> roles = claims.get("role", List.class);
        var authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        var principalId = "";

        try {
            principalId = subject;
        } catch (NumberFormatException ignore) { }

        if (Objects.equals(principalId, ""))
            return Mono.empty(); // invalid value for any of jwt auth parts

        var principal = new UserPrincipal(principalId, claims.getIssuer());

        return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(principal, null, authorities));
    }

}
