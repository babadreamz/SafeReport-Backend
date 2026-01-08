package africa.semicolon.safereportbackend.security;

import africa.semicolon.safereportbackend.data.repositories.Agencies;
import africa.semicolon.safereportbackend.data.repositories.ResponderUnits;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final ResponderUnits responderUnits;
    private final Agencies agencies;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Searching user across tables for username: {}", username);
        return responderUnits.findByUsername(username)
                .map( responder -> new PrincipalUser(
                        responder.getId(),
                        responder.getUsername(),
                        responder.getRoles(),
                        responder.getPassword()
                ))
                .or(()->agencies.findByUsername(username)
                        .map(agency -> new PrincipalUser(
                                agency.getId(),
                                agency.getUsername(),
                                agency.getRoles(),
                                agency.getPassword()
                        )))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
