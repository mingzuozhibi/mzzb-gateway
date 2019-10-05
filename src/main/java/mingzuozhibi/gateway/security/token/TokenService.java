package mingzuozhibi.gateway.security.token;

import lombok.Data;
import mingzuozhibi.common.model.Result;
import mingzuozhibi.gateway.security.user.User;
import mingzuozhibi.gateway.security.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class TokenService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Transactional
    @SuppressWarnings("unchecked")
    public Result<Token> authToken(String uuid) {
        Optional<Token> tokenOpt = tokenRepository.findByUuid(uuid);
        if (!tokenOpt.isPresent()) {
            return Result.ofErrorMessage("Token不存在");
        }
        Token token = tokenOpt.get();
        if (token.tokenExpired()) {
            return Result.ofErrorMessage("Token已失效");
        }
        User user = token.getUser();
        if (!user.isEnabled()) {
            return Result.ofErrorMessage("该用户已禁用");
        }
        onLoginSuccess(user);
        return Result.ofContent(token);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public Result<Token> dropToken(Long id) {
        Optional<Token> tokenOpt = tokenRepository.findById(id);
        if (!tokenOpt.isPresent()) {
            return Result.ofErrorMessage("Token不存在");
        }
        Token token = tokenOpt.get();
        tokenRepository.delete(token);
        return Result.ofContent(token);
    }

    @Data
    private static class LoginForm {
        private String username;
        private String password;
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public Result<Token> authLogin(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            return Result.ofErrorMessage("用户名不存在");
        }
        User user = userOpt.get();
        if (!user.isEnabled()) {
            return Result.ofErrorMessage("该用户已禁用");
        }
        if (!user.getPassword().equals(password)) {
            return Result.ofErrorMessage("密码错误");
        }
        onLoginSuccess(user);
        return Result.ofContent(createToken(user));
    }

    private Token createToken(User user) {
        return tokenRepository.save(new Token(user));
    }

    private void onLoginSuccess(User user) {
        user.setLoggedOn(Instant.now());
    }

}
