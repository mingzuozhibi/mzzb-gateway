package mingzuozhibi.gateway.security;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class SessionUtils {

    private static AtomicLong guestId = new AtomicLong(0);

    public static void setAutoLoginToken(HttpServletResponse res, String token) {
        res.addHeader("X-AUTO-LOGIN", token);
    }

    public static void setLoggedOut() {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new AnonymousAuthenticationToken(
            UUID.randomUUID().toString(),
            String.format("Guest_%d", guestId.incrementAndGet()),
            Collections.singleton(new SimpleGrantedAuthority("ROLE_Guest"))
        ));
    }

    public static void setLoggedUser(JsonObject userObj) {
        Objects.requireNonNull(userObj);
        setUserDetails(new UserDetailsImpl(userObj));
    }

    public static void setUserDetails(UserDetails userDetails) {
        Objects.requireNonNull(userDetails);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UserDetailsAuthentication(userDetails));
    }

    public static JsonObject buildSession() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        JsonObject object = new JsonObject();
        object.addProperty("userName", authentication.getName());
        object.addProperty("isLogged", isLogged(authentication));
        object.add("userRoles", buildRoles(authentication));
        return object;
    }

    public static boolean isLogged() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return isLogged(authentication);
    }

    private static boolean isLogged(Authentication authentication) {
        return authentication.isAuthenticated() &&
            authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(Predicate.isEqual("ROLE_Login"));
    }

    public static Set<String> getRoles() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
    }

    private static JsonArray buildRoles(Authentication authentication) {
        JsonArray userRoles = new JsonArray();
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .forEach(userRoles::add);
        return userRoles;
    }

}
