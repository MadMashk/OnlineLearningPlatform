package org.example.spring.security;

import org.example.model.AppUser;
import org.example.repositories.IAppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private IAppUserRepository appUserRepository;
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        AppUser user = appUserRepository
                .findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("user " + userName+ " doesn't exist"));
        return UserDetailsImpl.build(user);
    }
}
