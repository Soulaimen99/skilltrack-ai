package com.sou.repository;

import jakarta.enterprise.context.ApplicationScoped;

import com.sou.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
	
	public User findByUsername( String username ) {
		return find( "username", username ).firstResult();
	}
}
