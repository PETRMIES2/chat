package com.sope.domain.user;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.sope.domain.EntityRepository;

@Service
public class UserRepository {
	private final EntityRepository entityRepository;

    @Inject
	public UserRepository(EntityRepository entityRepository) {
		this.entityRepository = entityRepository;
	}

	public List<User> getUsers() {
		String hql = "from User";
		return entityRepository.get(hql, null);
	}

	public Optional<User> findByUsername(String username) {
		String hql = "from User where username = :username";
		ImmutableMap<Object, Object> params = ImmutableMap.builder()
				.put("username", username)
				.build();
		return entityRepository.getUnique(hql, params);
	}

}
