package africa.semicolon.safereportbackend.security;

import africa.semicolon.safereportbackend.data.models.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class PrincipalUser implements UserDetails {
    private String id;
    private String username;
    private List<Role> roles;
    private String password;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null)return Collections.emptyList();
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password != null?password:"";
    }

    @Override
    public String getUsername() {
        return username;
    }
}
