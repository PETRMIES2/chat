package com.sope.domain.user;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sope.SopeCache;
import com.sope.domain.EntityRepository;
import com.sope.domain.ResourceService;
import com.sope.domain.firebase.MessageService;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EntityRepository entityRepository;
    private final MessageService firebaseService;
    private final BCryptPasswordEncoder passwordEncoder;
    private SecureRandom random = new SecureRandom();
    
    @Inject
    public UserService(UserRepository userRepository, EntityRepository entityRepository, MessageService firebaseService, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.entityRepository = entityRepository;
        this.firebaseService = firebaseService;
        this.passwordEncoder = passwordEncoder;
    }

    public void updateUserPermissionStatus(String username) {
        Optional<User> persistedUser = userRepository.findByUsername(username);
        if (persistedUser.isPresent()) {
            User user = persistedUser.get();
            addWarning(user);
        }
    }

    void addWarning(User user) {

        UserPermissions permissions = user.getPermission();
        permissions.addWarning();

        if (permissions.getWarningCount() >= ResourceService.BAN_LIMIT) {
            int banMinutes = ResourceService.DEFAULT_BAN_MINUTES;

            if (permissions.isExtendedBan(ResourceService.EXTENDED_BAN)) {
                banMinutes = ResourceService.EXTENDED_BAN_MINUTES;
            }
            permissions.bannedFor(banMinutes);
            // send message through firebase --> monta minuuttia käyttäjä on
            // READ-ONLY -tilassa

        } else {
            // Send warning to user
        }

    }

    void releaseBanIfPossible(String username) {
        Optional<User> persistedUser = userRepository.findByUsername(username);
        if (persistedUser.isPresent() && persistedUser.get().getPermission().shouldReleaseBan()) {
            persistedUser.get().getPermission().releaseBan();
            entityRepository.save(persistedUser.get());
        }

    }

    public void updateUser(User user) {
        if (user != null) {
            Optional<User> persistedUser = userRepository.findByUsername(user.getUsername());
            if (persistedUser.isPresent()) {
                User persisted = persistedUser.get();
                persisted.setEmail(user.getEmail());
                persisted.setPassword(user.getPassword());
                persisted.setFirebaseToken(user.getFirebaseToken());
                persisted.setLastname(user.getLastname());
                persisted.setFirstname(user.getFirstname());
                save(persisted);
            }

        }

    }

    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setLastUsageDate(new Date());
        entityRepository.save(user);
        SopeCache.removeUserFromCache(user.getUsername());
        return user;
    }

    public boolean isUsernameAvailable(String username) {
        Optional<User> persistedUser = userRepository.findByUsername(username);

        if (persistedUser.isPresent()) {
            return false;
        }
        return true;
    }

    public User generateRandomPasswordAndSave(User user) {
        String randomPassword = nextRandomPassword();
        user.setUncrypted(randomPassword);
        user.setPassword(randomPassword);
        user.setCreated(new Date());
        System.out.println("random word " + randomPassword);
        return save(user);
    }
    private String nextRandomPassword() {
        return new BigInteger(130, random).toString(32);
    }


}
