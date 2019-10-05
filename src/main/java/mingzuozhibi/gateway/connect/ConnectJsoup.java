package mingzuozhibi.gateway.connect;

import mingzuozhibi.common.model.Result;
import mingzuozhibi.gateway.modules.Module;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

import static mingzuozhibi.gateway.security.session.SessionUtils.findLoggedName;

@Component
public class ConnectJsoup {

    @Autowired
    private ConnectService connect;

    public Result<String> waitRequest(String moduleName, String uri, Consumer<Connection> consumer) {
        Result<String> bodyResult = new Result<>();
        Optional<String> httpPrefixOpt = connect.getHttpPrefix(moduleName);
        if (!httpPrefixOpt.isPresent()) {
            bodyResult.setErrorMessage("服务未注册: " + moduleName);
            return bodyResult;
        }
        try {
            bodyResult.setContent(fetchBody(httpPrefixOpt.get() + uri, consumer));
        } catch (IOException e) {
            bodyResult.setErrorMessage("服务不可用: " + moduleName);
        }
        return bodyResult;
    }

    private String fetchBody(String url, Consumer<Connection> consumer) throws IOException {
        Connection connection = Jsoup.connect(url);
        connection.ignoreContentType(true);
        findLoggedName().ifPresent(username -> {
            connection.header("X-USERNAME", username);
        });
        if (consumer != null) {
            consumer.accept(connection);
        }
        return connection.execute().body();
    }

    public Result<String> waitRequest(Module module, String uri, Consumer<Connection> consumer) {
        return waitRequest(module.getModuleName(), uri, consumer);
    }

    public Result<String> get(Module module, String uri) {
        return waitRequest(module.getModuleName(), uri, null);
    }

    public Result<String> getSlow(Module module, String uri) {
        return waitRequest(module, uri, connection -> {
            connection.timeout(60 * 1000);
        });
    }

    public Result<String> post(Module module, String uri, String body) {
        return waitRequest(module, uri, connection -> {
            connection.header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            connection.method(Connection.Method.POST);
            connection.requestBody(body);
        });
    }

    public Result<String> put(Module module, String uri, String body) {
        return waitRequest(module, uri, connection -> {
            connection.header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            connection.method(Connection.Method.PUT);
            connection.requestBody(body);
        });
    }

    public Result<String> delete(Module module, String uri) {
        return waitRequest(module, uri, connection -> {
            connection.method(Connection.Method.DELETE);
        });
    }

}
