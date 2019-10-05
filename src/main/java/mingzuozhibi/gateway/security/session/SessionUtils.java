package mingzuozhibi.gateway.security.session;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mingzuozhibi.common.security.SimpleAuthentication;
import mingzuozhibi.common.security.SimpleAuthority;
import mingzuozhibi.common.security.SimpleUserDetails;
import mingzuozhibi.gateway.security.user.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static mingzuozhibi.gateway.security.session.SessionListener.getSessionCount;

public abstract class SessionUtils {

    private static AtomicLong guestId = new AtomicLong(0);

    public static void setAutoLoginToken(HttpServletResponse res, String token) {
        res.addHeader("X-Login-Token", token);
    }

    public static UserDetails buildUserDetails(User user) {
        SimpleUserDetails userDetails = new SimpleUserDetails();
        userDetails.setUsername(user.getUsername());
        userDetails.setEnabled(user.isEnabled());
        user.getRoles().forEach(role -> {
            userDetails.getAuthorities().add(new SimpleAuthority("ROLE_" + role));
        });
        return userDetails;
    }

    public static void setLoggedUser(User user) {
        Objects.requireNonNull(user);
        UserDetails userDetails = buildUserDetails(user);
        Authentication authentication = new SimpleAuthentication(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static void setLoggedOut() {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new AnonymousAuthenticationToken(
            UUID.randomUUID().toString(),
            String.format("Guest_%d", guestId.incrementAndGet()),
            Collections.singleton(new SimpleGrantedAuthority("ROLE_Guest"))
        ));
    }

    public static Optional<String> findLoggedName() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (isLogged(authentication)) {
            return Optional.of(authentication.getName());
        } else {
            return Optional.empty();
        }
    }

    public static String getName() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return authentication.getName();
    }

    public static boolean isLogged() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return isLogged(authentication);
    }

    public static Set<String> getRoles() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
    }

    public static JsonObject buildSession() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        JsonObject object = new JsonObject();
        object.addProperty("userName", authentication.getName());
        object.addProperty("isLogged", isLogged(authentication));
        object.add("userRoles", buildRoles(authentication));
        object.addProperty("userCount", getSessionCount());
        return object;
    }

    // private method

    private static boolean isLogged(Authentication authentication) {
        return authentication.isAuthenticated() &&
            authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(Predicate.isEqual("ROLE_Login"));
    }

    private static JsonArray buildRoles(Authentication authentication) {
        JsonArray userRoles = new JsonArray();
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .forEach(userRoles::add);
        return userRoles;
    }

}
