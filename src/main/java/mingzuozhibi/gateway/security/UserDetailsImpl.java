package mingzuozhibi.gateway.security;

import com.google.gson.JsonObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    private String username;
    private boolean enabled;
    private Set<GrantedAuthority> authorities = new HashSet<>();

    public UserDetailsImpl(JsonObject userObj) {
        this.username = userObj.get("username").getAsString();
        this.enabled = userObj.get("enabled").getAsBoolean();
        userObj.get("roles").getAsJsonArray().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getAsString()));
        });
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
