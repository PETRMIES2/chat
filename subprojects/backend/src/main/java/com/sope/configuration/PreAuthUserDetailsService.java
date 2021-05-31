package com.sope.configuration;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.KeyBasedPersistenceTokenService;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.sope.SopeCache;
import com.sope.domain.SopeTransactionExecutor;
import com.sope.domain.SopeTransactionExecutor.TransactionSupplier;

@Component
public class PreAuthUserDetailsService implements AuthenticationUserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(PreAuthUserDetailsService.class);

    private final UserDetailsService userDetailsService;
    private final SopeTransactionExecutor transactionExecutor;
    private final KeyBasedPersistenceTokenService keyBasedPersistenceTokenService;

    @Inject
    public PreAuthUserDetailsService(final UserDetailsService userDetailsService, final SopeTransactionExecutor transactionExecutor,
            final KeyBasedPersistenceTokenService keyBasedPersistenceTokenService) {
        this.userDetailsService = userDetailsService;
        this.transactionExecutor = transactionExecutor;
        this.keyBasedPersistenceTokenService = keyBasedPersistenceTokenService;
    }

    @Override
    public UserDetails loadUserDetails(Authentication authentication) throws UsernameNotFoundException {
        String principal = authentication.getPrincipal().toString();
        final Token verifiedToken = tryToVerifyKey(principal);
       
        UserDetails userDetails = null;
        if (SopeCache.stillValidInCache(verifiedToken.getExtendedInformation())) {
            userDetails = SopeCache.getCachedUserDetails(verifiedToken.getExtendedInformation());
        } else {
            userDetails = transactionExecutor.read(new TransactionSupplier<UserDetails>() {
                
                @Override
                public UserDetails get() {
                    return userDetailsService.loadUserByUsername(verifiedToken.getExtendedInformation());
                }
            });
            SopeCache.putUserDetailsToCache(verifiedToken.getExtendedInformation(), userDetails);
        }
        return userDetails;
    }

    private Token tryToVerifyKey(String key) {
        try {
            // User token päivitetään esim. kerran päivässä / viikossa...
            return keyBasedPersistenceTokenService.verifyToken(key);
        } catch (Exception e) {
            logger.info("Cannot verificate token key '" + key + "'");
            throw new BadCredentialsException("Token key error'" + key + "'");
        }
    }
}
