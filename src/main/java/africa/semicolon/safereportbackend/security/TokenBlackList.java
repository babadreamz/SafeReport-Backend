package africa.semicolon.safereportbackend.security;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class TokenBlackList {
    private final Set<String> blackList = new HashSet<String>();
    public void addToken(String token) {
        blackList.add(token);
    }
    public boolean containsToken(String token) {
        return blackList.contains(token);
    }
}