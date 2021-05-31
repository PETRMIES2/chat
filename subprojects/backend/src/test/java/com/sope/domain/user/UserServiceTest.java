package com.sope.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sope.domain.EntityRepository;
import com.sope.domain.ResourceService;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EntityRepository entityRepository;
    
    @InjectMocks
    private UserService userService;

    private User user;
    private UserPermissions permission;

    @Before
    public void setUp() {
        user = new User();
        permission = user.getPermission();
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));

    }

    @Test
    public void shouldBanUser() {
        permission.bannedFor(ResourceService.DEFAULT_BAN_MINUTES);
        assertThat(permission.getBanDateTime()).isNotNull();
        assertThat(permission.getBanReleaseDateTime()).isEqualTo(new LocalDateTime(permission.getBanDateTime()).plusMinutes(ResourceService.DEFAULT_BAN_MINUTES).toDate());

    }

    @Test
    public void shouldReleaseUserBan() {
        permission.bannedFor(ResourceService.DEFAULT_BAN_MINUTES);
        permission.setBanReleaseDateTime(new LocalDateTime(permission.getBanDateTime()).minusMinutes(30).toDate());
        userService.releaseBanIfPossible("TESTETS");
        assertThat(permission.isBanned()).isFalse();
        assertThat(permission.getLastBanDate()).isNotNull();
        assertThat(permission.getTotalBanTimes()).isEqualTo(1);
        assertThat(permission.getWarningCount()).isEqualTo(0);
        verify(entityRepository).save(isA(User.class));
    }

    @Test
    public void shouldAddWarningToUser() {
        userService.updateUserPermissionStatus("TestTest");
        assertThat(permission.getWarningCount()).isEqualTo(1);
    }

    @Test
    public void shouldBanUserWhenWarningLimitReached() {
        permission.setWarningCount(2);
        userService.updateUserPermissionStatus("TestTest");
        assertThat(permission.getWarningCount()).isEqualTo(3);
        assertThat(permission.getBanDateTime()).isNotNull();
        assertThat(permission.getBanReleaseDateTime()).isEqualTo(new LocalDateTime(permission.getBanDateTime()).plusMinutes(ResourceService.DEFAULT_BAN_MINUTES).toDate());

    }

    @Test
    public void shouldBanUserForExtendedTime() {
        permission.setWarningCount(2);
        permission.setTotalBanTimes(5);
        userService.updateUserPermissionStatus("TestTest");
        assertThat(permission.getWarningCount()).isEqualTo(3);
        assertThat(permission.getBanDateTime()).isNotNull();
        assertThat(permission.getBanReleaseDateTime()).isEqualTo(new LocalDateTime(permission.getBanDateTime()).plusMinutes(ResourceService.EXTENDED_BAN_MINUTES).toDate());

    }

}
