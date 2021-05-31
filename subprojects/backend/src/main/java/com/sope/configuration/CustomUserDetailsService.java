package com.sope.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sope.domain.SopeTransactionExecutor;
import com.sope.domain.SopeTransactionExecutor.TransactionSupplier;
import com.sope.domain.user.User;
import com.sope.domain.user.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	private final SopeTransactionExecutor transactionExecutor;

	@Inject
	public CustomUserDetailsService(UserRepository userRepository, SopeTransactionExecutor transactionExecutor) {
		this.userRepository = userRepository;
		this.transactionExecutor = transactionExecutor;
	}

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		return transactionExecutor.read(new TransactionSupplier<UserDetails>() {
			@Override
			public UserDetails get() {
				Optional<User> user = userRepository.findByUsername(username);
				if (!user.isPresent()) {
					throw new UsernameNotFoundException(String.format("User %s does not exist!", username));
				}
				return new UserRepositoryUserDetails(user.get());
			}
		});
	}

	private class UserRepositoryUserDetails implements UserDetails {
		User user;
		private static final long serialVersionUID = 1L;

		private UserRepositoryUserDetails(User user) {
			this.user = user;
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return Collections.emptyList();
		}

		@Override
		public String getUsername() {
			return user.getUsername();
		}

		@Override
		public boolean isAccountNonExpired() {
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public String getPassword() {
			return user.getPassword();
		}

	}

}
