package epam.task.gymbootdb.dto;

import epam.task.gymbootdb.entity.User;

import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
public class GymUserDetails implements UserDetails {

    private final long id;
    private final String username;
    private final String password;
    private final boolean isActive;
    private final Set<SimpleGrantedAuthority> authorities = new HashSet<>();

    public GymUserDetails(User user) {
        id = user.getId();
        username = user.getUsername();
        password = user.getPassword();
        isActive = user.isActive();
        authorities.addAll(user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
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
        return isActive;
    }
}
