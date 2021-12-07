package spring.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import model.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsImpl implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String userName;
    private String email;
    @JsonIgnore
    private String passWord;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Integer id, String userName, String email, String passWord, Collection<? extends GrantedAuthority> authorities) {

        this.id = id;
        this.userName = userName;
        this.email = email;
        this.passWord = passWord;
        this.authorities = authorities;
    }

    public UserDetailsImpl() {
    }

    public static UserDetailsImpl build(AppUser user){
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
        return new UserDetailsImpl(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getPassWord(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passWord;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    public Integer getId() {
        return id;
    }



    public String getEmail() {
        return email;
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
}

