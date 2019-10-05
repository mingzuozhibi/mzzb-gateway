package mingzuozhibi.gateway.security.user;

import lombok.Setter;
import mingzuozhibi.common.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static mingzuozhibi.gateway.security.session.SessionUtils.buildUserDetails;

@RestController
public class UserController extends BaseController {

    @Autowired
    private UserRepository userRepository;

    @Setter
    private static class AddOneForm {
        private String username;
        private String password;
        private boolean enabled = true;
    }

    @Setter
    private static class SetOneForm {
        private boolean enabled;
        private Set<String> roles = new HashSet<>();
    }

    @GetMapping("/userDetails/{username}")
    public String findUserDetails(@PathVariable String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            return errorMessage("用户名不存在");
        }
        User user = userOpt.get();
        return objectResult(buildUserDetails(user));
    }

    @Transactional
    @PostMapping("/api/gateway/register")
    public String addOne(@RequestBody AddOneForm form) {
        if (StringUtils.isEmpty(form.username)) {
            return errorMessage("用户名不能为空");
        }
        if (StringUtils.isEmpty(form.password)) {
            return errorMessage("密码不能为空");
        }
        if (userRepository.existsByUsername(form.username)) {
            return errorMessage("用户名已存在");
        }
        User user = new User(form.username, form.password, form.enabled);
        userRepository.save(user);
        return objectResult(user);
    }

    @Transactional
    @PreAuthorize("hasRole('User_Admin')")
    @GetMapping("/api/gateway/users")
    public String findAll(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "20") int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        Page<User> userPage = userRepository.findAll(pageRequest);
        List<User> userList = userPage.getContent();
        return objectResult(userList, userPage);
    }

    @Transactional
    @PreAuthorize("hasRole('User_Admin')")
    @GetMapping("/api/gateway/users/{id}")
    public String findById(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            return errorMessage("用户Id不存在");
        }
        User user = userOpt.get();
        return objectResult(user);
    }

    @Transactional
    @PreAuthorize("hasRole('User_Admin')")
    @PutMapping("/api/gateway/users/{id}")
    public String setOne(@PathVariable Long id, @RequestBody SetOneForm form) {
        // Find User By Id
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            return errorMessage("用户Id不存在");
        }
        User user = userOpt.get();
        // Edit User
        user.setRoles(form.roles);
        user.setEnabled(form.enabled);
        return objectResult(user);
    }

}
