package mingzuozhibi.gateway.utils;

import mingzuozhibi.common.model.Result;
import mingzuozhibi.common.spider.SpiderJsoup;
import mingzuozhibi.gateway.connect.ConnectHelper;
import mingzuozhibi.gateway.modules.Module;
import org.jsoup.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public class JsoupHelper {

    @Autowired
    private ConnectHelper connectHelper;

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
        Optional<String> httpPrefix = connectHelper.getHttpPrefix(moduleName);
        if (!httpPrefix.isPresent()) {
            return Result.ofErrorMessage(moduleName + "服务不可用");
        }
        return SpiderJsoup.waitRequest(httpPrefix.get() + uri, consumer);
    }

}
