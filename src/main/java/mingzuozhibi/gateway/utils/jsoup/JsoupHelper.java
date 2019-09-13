package mingzuozhibi.gateway.utils.jsoup;

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

    public JsoupResponse waitRequest(Module module, String uri) {
        return waitRequest(module.getModuleName(), uri);
    }

    public JsoupResponse waitRequest(Module module, String uri, Consumer<Connection> consumer) {
        return waitRequest(module.getModuleName(), uri, consumer);
    }

    public JsoupResponse waitRequest(String moduleName, String uri) {
        return waitRequest(moduleName, uri, null);
    }

    public JsoupResponse waitRequest(String moduleName, String uri, Consumer<Connection> consumer) {
        Optional<String> httpPrefix = connectHelper.getHttpPrefix(moduleName);
        if (!httpPrefix.isPresent()) {
            return JsoupResponse.of(moduleName + "服务不可用");
        }
        return JsoupUtils.waitRequest(httpPrefix.get() + uri, consumer);
    }

}
