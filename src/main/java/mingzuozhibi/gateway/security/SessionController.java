package mingzuozhibi.gateway.security;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import mingzuozhibi.common.BaseController;
import mingzuozhibi.common.gson.GsonFactory;
import mingzuozhibi.common.model.Content;
import mingzuozhibi.common.model.Result;
import mingzuozhibi.gateway.connect.ConnectJsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static mingzuozhibi.gateway.modules.Module.USER_SERVER;
import static mingzuozhibi.gateway.security.SessionUtils.*;

@RestController
public class SessionController extends BaseController {

    private Gson gson = GsonFactory.createGson();

    @Autowired
    private ConnectJsoup jsoup;

    @PostMapping("/gateway/register")
    public String register(@RequestBody LoginReq req) {
        if (StringUtils.isEmpty(req.username)) {
            return errorMessage("用户名不能为空");
        }
        if (StringUtils.isEmpty(req.password)) {
            return errorMessage("密码不能为空");
        }
        Result<String> bodyResult = jsoup.post(USER_SERVER, "/session/register", gson.toJson(req));
        Content content = Content.parse(bodyResult);
        if (!content.isSuccess()) {
            return errorMessage(content.getMessage());
        }
        return objectResult("注册成功");
    }

    @GetMapping("/gateway/session")
    public String sessionQuery(HttpServletRequest req, HttpServletResponse res) {
        if (!isLogged()) {
            String token = req.getHeader("X-AUTO-LOGIN");
            if (StringUtils.hasLength(token)) {
                Result<String> bodyResult = jsoup
                    .post(USER_SERVER, "/session/token", token);
                if (!bodyResult.isUnfinished()) {
                    Content content = new Content(bodyResult.getContent());
                    if (content.isSuccess()) {
                        setLoggedUser(content.getObject());
                    } else {
                        setAutoLoginToken(res, "");
                    }
                }
            }
        }
        return objectResult(buildSession());
    }

    @PostMapping("/gateway/session")
    public String sessionLogin(@RequestBody LoginReq req, HttpServletResponse res) {
        if (StringUtils.isEmpty(req.username)) {
            return errorMessage("用户名不能为空");
        }
        if (StringUtils.isEmpty(req.password)) {
            return errorMessage("密码不能为空");
        }
        Result<String> bodyResult = jsoup
            .post(USER_SERVER, "/session/login", gson.toJson(req));
        Content content = Content.parse(bodyResult);
        if (!content.isSuccess()) {
            return errorMessage(content.getMessage());
        }
        JsonObject root = content.getObject();
        setLoggedUser(root.get("user").getAsJsonObject());
        setAutoLoginToken(res, root.get("token").getAsString());
        return objectResult(buildSession());
    }

    @DeleteMapping("/gateway/session")
    public String sessionLogout(HttpServletResponse res) {
        setLoggedOut();
        setAutoLoginToken(res, "");
        return objectResult(buildSession());
    }

    @Setter
    @Getter
    @ToString
    private static class LoginReq {
        private String username;
        private String password;
    }

}
