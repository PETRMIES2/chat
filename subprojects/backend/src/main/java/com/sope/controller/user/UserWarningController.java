
package com.sope.controller.user;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sope.controller.ApiStructure;
import com.sope.domain.SopeTransactionExecutor;
import com.sope.domain.SopeTransactionExecutor.TransactionConsumer;
import com.sope.domain.user.UserService;

@RestController
@RequestMapping(ApiStructure.USERS)
public class UserWarningController {
    private SopeTransactionExecutor transactionExecutor;
    private UserService userService;

    @Inject
    public UserWarningController(SopeTransactionExecutor transactionExecutor, UserService userService) {
        this.transactionExecutor = transactionExecutor;
        this.userService = userService;
    }

    @RequestMapping(value="/{username}/warning", method = RequestMethod.PUT)
    public void giveWarningToUser(@PathVariable final String username) {
        transactionExecutor.write(new TransactionConsumer() {

            @Override
            public void consumeTransaction() {
                userService.updateUserPermissionStatus(username);
            }

        });
    }

}
