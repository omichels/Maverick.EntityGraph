package com.bechtle.eagl.graph.api.security;

import com.bechtle.eagl.graph.api.security.errors.NoSubscriptionFound;
import com.bechtle.eagl.graph.api.security.errors.RevokedApiKeyUsed;
import com.bechtle.eagl.graph.api.security.errors.UnknownApiKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@Primary
public class SecurityAuthenticationManager implements ReactiveAuthenticationManager {

    @Value("${application.security.apiKey}")
    String key;



    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        Assert.notNull(authentication, "Authentication is null in Authentication Manager");

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return handleBasicAuthentication((UsernamePasswordAuthenticationToken) authentication)
                    .map(auth -> (Authentication) auth);
        }

        if (authentication instanceof ApiKeyToken) {
            return handleApiKeyAuthentication((ApiKeyToken) authentication)
                    .map(auth -> (Authentication) auth);

        }


        return Mono.just(authentication);
    }

    private Mono<? extends Authentication> handleApiKeyAuthentication(ApiKeyToken authentication) {
        // check if this is the admin user
        if(authentication.getApiKey().equalsIgnoreCase(this.key)) {
            return Mono.just(new AdminAuthentication());
        }

        return Mono.just(authentication);

    }

    private Mono<? extends Authentication> handleBasicAuthentication(UsernamePasswordAuthenticationToken authentication) {
        return Mono.just(authentication);
    }
}
