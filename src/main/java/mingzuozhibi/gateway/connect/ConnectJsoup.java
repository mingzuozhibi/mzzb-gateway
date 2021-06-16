package mingzuozhibi.gateway.connect;

import mingzuozhibi.common.model.Result;
import mingzuozhibi.common.spider.SpiderJsoup;
import mingzuozhibi.gateway.modules.Module;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public class ConnectJsoup {

    @Autowired
    private ConnectService connectService;

    @SuppressWarnings("unchecked")
    private Result<String> waitRequest(String moduleName, String uri, Consumer<Connection> consumer) {
        Optional<String> httpPrefix = connectService.getHttpPrefix(moduleName);
        if (!httpPrefix.isPresent()) {
            return Result.ofErrorMessage(moduleName + "服务不可用");
        }
        return SpiderJsoup.waitRequest(httpPrefix.get() + uri, consumer);
    }

    public Result<String> waitRequest(Module module, String uri, Consumer<Connection> consumer) {
        return waitRequest(module.getModuleName(), uri, consumer);
    }

    public Result<String> get(Module module, String uri) {
        return waitRequest(module.getModuleName(), uri, null);
    }

    public Result<String> getSlow(Module module, String uri) {
        Optional<String> httpPrefix = connectService.getHttpPrefix(module.getModuleName());
        if (!httpPrefix.isPresent()) {
            return Result.ofErrorMessage(module.getModuleName() + "服务不可用");
        }
        return waitRequestSlow(httpPrefix.get() + uri);
    }

    public Result<String> waitRequestSlow(String url) {
        Result<String> result = new Result<>();
        try {
            Connection connection = Jsoup.connect(url).ignoreContentType(true);
            connection.timeout(180 * 1000);
            result.setContent(connection.execute().body());
        } catch (Exception e) {
            result.pushError(e);
        }
        return result;
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

    public Result<String> delete(Module module, String uri, String body) {
        return waitRequest(module, uri, connection -> {
            connection.header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            connection.method(Connection.Method.DELETE);
            connection.requestBody(body);
        });
    }

}
