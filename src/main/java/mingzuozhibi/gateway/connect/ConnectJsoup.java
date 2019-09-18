package mingzuozhibi.gateway.connect;

import mingzuozhibi.common.model.Result;
import mingzuozhibi.common.spider.SpiderJsoup;
import mingzuozhibi.gateway.modules.Module;
import org.jsoup.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public class ConnectJsoup {

    @Autowired
    private ConnectService connectService;

    public Result<String> waitRequest(Module module, String uri) {
        return waitRequest(module.getModuleName(), uri);
    }

    public Result<String> waitRequest(Module module, String uri, Consumer<Connection> consumer) {
        return waitRequest(module.getModuleName(), uri, consumer);
    }

    public Result<String> waitRequest(String moduleName, String uri) {
        return waitRequest(moduleName, uri, null);
    }

    @SuppressWarnings("unchecked")
    public Result<String> waitRequest(String moduleName, String uri, Consumer<Connection> consumer) {
        Optional<String> httpPrefix = connectService.getHttpPrefix(moduleName);
        if (!httpPrefix.isPresent()) {
            return Result.ofErrorMessage(moduleName + "服务不可用");
        }
        return SpiderJsoup.waitRequest(httpPrefix.get() + uri, consumer);
    }

    public Result<String> postRequest(Module module, String uri, String body) {
        return waitRequest(module, uri, connection -> {
            connection.header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            connection.method(Connection.Method.POST);
            connection.requestBody(body);
        });
    }

    public Result<String> putRequest(Module module, String uri, String body) {
        return waitRequest(module, uri, connection -> {
            connection.header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            connection.method(Connection.Method.PUT);
            connection.requestBody(body);
        });
    }

    public Result<String> deleteRequest(Module module, String uri, String body) {
        return waitRequest(module, uri, connection -> {
            connection.header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            connection.method(Connection.Method.DELETE);
            connection.requestBody(body);
        });
    }

}
