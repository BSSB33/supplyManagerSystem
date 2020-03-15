package com.elte.supplymanagersystem.security;

import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticatedUser authenticatedUser;

    /**
     * Loads and Returns the LoggedInUser by Username
     * @param username User Logged in
     * @return Authorized user
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        User oUser = userRepository.findByUsername(username);
        authenticatedUser.setUser(oUser);
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(oUser.getRole().toString()));

        return new org.springframework.security.core.userdetails.User(oUser.getUsername(), oUser.getPassword(), grantedAuthorities);
    }
}
