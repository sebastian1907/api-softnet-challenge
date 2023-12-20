package com.softnet.app.config;

import com.softnet.app.config.security.AuthenticationManager;
import com.softnet.app.config.security.support.JwtVerifyHandler;
import com.softnet.app.config.security.support.ServerHttpBearerAuthenticationConverter;
import com.softnet.app.config.security.support.ServerHttpCookieAuthenticationConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

@Configuration
@EnableReactiveMethodSecurity
@Slf4j
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, AuthenticationManager authManager) {
        http
                .authorizeExchange(exchanges ->{
                    exchanges.pathMatchers(HttpMethod.OPTIONS).permitAll();
                    exchanges.pathMatchers("public/**", "/status", "/actuator/**").permitAll();
                    exchanges.anyExchange().authenticated();
                })
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .exceptionHandling(exceptionHandlingSpec -> {
                    exceptionHandlingSpec.authenticationEntryPoint((swe, e) -> {
                        log.info("[1] Authentication error: Unauthorized[401]: " + e.getMessage());

                        return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
                    });
                    exceptionHandlingSpec.accessDeniedHandler((swe, e) -> {
                        log.info("[2] Authentication error: Access Denied[401]: " + e.getMessage());

                        return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
                    });
                })
                .addFilterAt(bearerAuthenticationFilter(authManager), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(cookieAuthenticationFilter(authManager), SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }

    AuthenticationWebFilter bearerAuthenticationFilter(AuthenticationManager authManager) {
        AuthenticationWebFilter bearerAuthenticationFilter = new AuthenticationWebFilter(authManager);
        bearerAuthenticationFilter.setAuthenticationConverter(new ServerHttpBearerAuthenticationConverter(new JwtVerifyHandler(jwtSecret)));
        bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));

        return bearerAuthenticationFilter;
    }

    AuthenticationWebFilter cookieAuthenticationFilter(AuthenticationManager authManager) {
        AuthenticationWebFilter cookieAuthenticationFilter = new AuthenticationWebFilter(authManager);
        cookieAuthenticationFilter.setAuthenticationConverter(new ServerHttpCookieAuthenticationConverter(new JwtVerifyHandler(jwtSecret)));
        cookieAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));

        return cookieAuthenticationFilter;
    }

}
