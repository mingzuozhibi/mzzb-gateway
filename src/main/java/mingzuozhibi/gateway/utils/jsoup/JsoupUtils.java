package mingzuozhibi.gateway.utils.jsoup;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.function.Consumer;

public abstract class JsoupUtils {

    public static JsoupResponse waitRequest(String url) {
        return waitRequest(url, null);
    }

    public static JsoupResponse waitRequest(String url, Consumer<Connection> consumer) {
        JsoupResponse result = new JsoupResponse();
        for (int retry = 0; retry < 3; retry++) {
            try {
                Connection connection = Jsoup.connect(url)
                        .ignoreContentType(true)
                        .timeout(10000);
                if (consumer != null) {
                    consumer.accept(connection);
                }
                result.setContent(connection.execute().body());
                break;
            } catch (Exception e) {
                result.addError(e);
            }
        }
        return result;
    }

}
