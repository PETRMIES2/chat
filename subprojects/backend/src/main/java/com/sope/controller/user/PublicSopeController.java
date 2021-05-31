package com.sope.controller.user;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.KeyBasedPersistenceTokenService;
import org.springframework.security.core.token.Token;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sope.SopeCache;
import com.sope.controller.ApiStructure;
import com.sope.domain.SopeTransactionExecutor;
import com.sope.domain.SopeTransactionExecutor.TransactionConsumer;
import com.sope.domain.SopeTransactionExecutor.TransactionSupplier;
import com.sope.domain.user.TokenDTO;
import com.sope.domain.user.User;
import com.sope.domain.user.UserDTO;
import com.sope.domain.user.UserService;

@RestController
@RequestMapping(ApiStructure.PUBLIC)
public class PublicSopeController {

    private final AuthenticationManager authenticationManager;
    private final SopeTransactionExecutor transactionExecutor;
    private final KeyBasedPersistenceTokenService keyBasedPersistenceTokenService;
    private UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Inject
    public PublicSopeController(AuthenticationManager authenticationManager, SopeTransactionExecutor transactionExecutor, KeyBasedPersistenceTokenService keyBasedPersistenceTokenService,
            UserService userService, BCryptPasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.transactionExecutor = transactionExecutor;
        this.keyBasedPersistenceTokenService = keyBasedPersistenceTokenService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void print() {
        System.out.println(passwordEncoder.encode("maisteri7kiva"));
    }

    @RequestMapping(value = "authenticate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public TokenDTO authenticate(@RequestBody final UserDTO userDTO) throws Exception {
        System.out.println("CURRENT PASSWORD '" + userDTO.getPassword() + "'");
        System.out.println("ENCODED '" + passwordEncoder.encode(userDTO.getPassword()) + "'");

        final UsernamePasswordAuthenticationToken authenticationRequest = new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword());

        transactionExecutor.read(new TransactionConsumer() {

            @Override
            public void consumeTransaction() {
                Authentication authentication =  authenticationManager.authenticate(authenticationRequest);
                if (!authentication.isAuthenticated()) {
                    throw new AuthenticationFailed(userDTO.getUsername());
                }
//                authenticationRequest.setAuthenticated(authentication.isAuthenticated());
            }
        });
        Token token = keyBasedPersistenceTokenService.allocateToken(userDTO.getUsername());
        // System.out.println("TOKEN " + token);
        TokenDTO tokenDTO = new TokenDTO(token.getKey());
        return tokenDTO;

    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public UserDTO saveUser(@RequestBody final UserDTO userDTO) {
        return transactionExecutor.write(new TransactionSupplier<UserDTO>() {

            @Override
            public UserDTO get() {
                if (!userService.isUsernameAvailable(userDTO.getUsername())) {
                    throw new UsernameAlreadyTaken();
                }
                User user = userService.generateRandomPasswordAndSave(new UserConverter().apply(userDTO));
                UserDTO newUser = new UserDTOConverter().apply(user);
                Token token = keyBasedPersistenceTokenService.allocateToken(userDTO.getUsername());
                newUser.setToken(token.getKey());

                return newUser;
            }
        });
    }

    @RequestMapping(value = "available/{username}", method = RequestMethod.GET)
    public void checkIfUserNameIsAvailable(@PathVariable final String username) {
        synchronized (username) {
            if (SopeCache.getCachedUserName(username)) {
                throw new UsernameAlreadyTaken();
            }
            SopeCache.reserverUsername(username);
        }
        transactionExecutor.read(new TransactionConsumer() {
            @Override
            public void consumeTransaction() {
                if (!userService.isUsernameAvailable(username)) {
                    throw new UsernameAlreadyTaken();
                }
            }
        });
    }
}
