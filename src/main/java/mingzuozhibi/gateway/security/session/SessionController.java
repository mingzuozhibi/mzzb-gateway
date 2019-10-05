package mingzuozhibi.gateway.security.session;

import lombok.Setter;
import mingzuozhibi.common.BaseController;
import mingzuozhibi.common.model.Result;
import mingzuozhibi.gateway.security.token.Token;
import mingzuozhibi.gateway.security.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

import static mingzuozhibi.gateway.security.session.SessionUtils.*;

@RestController
public class SessionController extends BaseController {

    @Autowired
    private TokenService tokenService;

    @Setter
    private static class LoginForm {
        private String username;
        private String password;
    }

    @GetMapping("/api/gateway/session")
    public String sessionQuery(HttpServletRequest req, HttpServletResponse res) {
        if (!isLogged()) {
            String uuid = req.getHeader("X-Login-Token");
            if (StringUtils.hasLength(uuid)) {
                Result<Token> tokenResult = tokenService.authToken(uuid);
                if (tokenResult.isUnfinished()) {
                    setAutoLoginToken(res, "");
                } else {
                    Token token = tokenResult.getContent();
                    setLoggedUser(token.getUser());
                    req.getSession(true).setAttribute("tokenId", token.getId());
                }
            }
        }
        return objectResult(buildSession());
    }

    @PostMapping("/api/gateway/session")
    public String sessionLogin(@RequestBody LoginForm form, HttpServletRequest req, HttpServletResponse res) {
        if (StringUtils.isEmpty(form.username)) {
            return errorMessage("用户名不能为空");
        }
        if (StringUtils.isEmpty(form.password)) {
            return errorMessage("密码不能为空");
        }
        Result<Token> tokenResult = tokenService.authLogin(form.username, form.password);
        if (tokenResult.isUnfinished()) {
            return errorMessage(tokenResult.formatError());
        }
        Token token = tokenResult.getContent();
        setLoggedUser(token.getUser());
        setAutoLoginToken(res, token.getUuid());
        req.getSession(true).setAttribute("tokenId", token.getId());
        return objectResult(buildSession());
    }

    @DeleteMapping("/api/gateway/session")
    public String sessionLogout(HttpServletRequest req, HttpServletResponse res) {
        Long tokenId = (Long) req.getSession(true).getAttribute("tokenId");
        setLoggedOut();
        setAutoLoginToken(res, "");
        if (!Objects.isNull(tokenId)) {
            tokenService.dropToken(tokenId);
        }
        return objectResult(buildSession());
    }

}
