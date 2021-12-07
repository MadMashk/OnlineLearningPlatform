package spring.security;

import model.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import repositories.IAppUserRepository;

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
