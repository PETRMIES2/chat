package com.sope.configuration;

import java.util.List;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.token.KeyBasedPersistenceTokenService;
import org.springframework.security.core.token.SecureRandomFactoryBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import com.google.common.collect.Lists;

@Configuration
public class SpringSecurityConfiguration {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Inject
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService, BCryptPasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    @Inject
    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider daoAuthProvider, PreAuthenticatedAuthenticationProvider preAuthProvider) {
        List providerList = Lists.newArrayList(daoAuthProvider, preAuthProvider);
        return new ProviderManager(providerList);
    }

    @Inject
    @Bean
    public KeyBasedPersistenceTokenService keyBasedPersistenceTokenService() throws Exception {
        KeyBasedPersistenceTokenService keyBasedPersistenceTokenService = new KeyBasedPersistenceTokenService();
        keyBasedPersistenceTokenService.setSecureRandom(new SecureRandomFactoryBean().getObject());
        keyBasedPersistenceTokenService.setServerInteger(-435256413);
        keyBasedPersistenceTokenService.setServerSecret("sbdvc234ASWERxcvz#¤asf454D");
        return keyBasedPersistenceTokenService;
    }

    @Inject
    @Bean
    public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider(PreAuthUserDetailsService preAuthUserDetailsService) {
        PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();
        preAuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService(preAuthUserDetailsService);
        return preAuthenticatedAuthenticationProvider;
    }

}
