
package com.sope.controller.user;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sope.controller.ApiStructure;
import com.sope.domain.SopeTransactionExecutor;
import com.sope.domain.SopeTransactionExecutor.TransactionConsumer;
import com.sope.domain.user.UserDTO;
import com.sope.domain.user.UserService;

@RestController
@RequestMapping(ApiStructure.USERS)
public class UserController {
    private SopeTransactionExecutor transactionExecutor;
    private UserService userService;

    @Inject
    public UserController(SopeTransactionExecutor transactionExecutor, UserService userService) {
        this.transactionExecutor = transactionExecutor;
        this.userService = userService;
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public void updateUserInformation(@RequestBody final UserDTO user) {
        System.out.println("FIREBASE TOKEN " + user.getFirebasetoken());
        System.out.println("email " + user.getEmail());
        transactionExecutor.write(new TransactionConsumer() {

            @Override
            public void consumeTransaction() {
                userService.updateUser(new UserConverter().apply(user));
            }
        });
    }

}
