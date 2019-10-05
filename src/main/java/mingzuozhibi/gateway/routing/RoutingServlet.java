package mingzuozhibi.gateway.routing;

import mingzuozhibi.common.model.Result;
import mingzuozhibi.gateway.connect.ConnectJsoup;
import org.jsoup.Connection.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet({"/api/mzzb-server/*"})
public class RoutingServlet extends BaseServlet {

    @Autowired
    private ConnectJsoup jsoup;

    private Pattern pattern = Pattern.compile("^/api/([^/]+)(/.*)$");

    public void doService(HttpServletRequest request, HttpServletResponse response) {
        String requestURI = request.getRequestURI();
        Matcher matcher = pattern.matcher(requestURI);
        if (matcher.find()) {
            String moduleName = matcher.group(1);
            String uri = getUri(matcher.group(2), request.getQueryString());
            Result<String> bodyResult = jsoup.waitRequest(moduleName, uri, connection -> {
                connection.method(Method.valueOf(request.getMethod()));
                getBody(request).filter(StringUtils::hasText).ifPresent(body -> {
                    connection.requestBody(body);
                    connection.header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
                });
            });
            if (!bodyResult.isUnfinished()) {
                responseText(response, bodyResult.getContent());
            } else {
                responseError(response, bodyResult.formatError());
            }
        } else {
            responseError(response, "路由无法识别: %s", requestURI);
        }
    }

    private String getUri(String baseUri, String queryString) {
        StringBuilder builder = new StringBuilder();
        builder.append("/api");
        builder.append(baseUri);
        if (StringUtils.hasText(queryString)) {
            builder.append('?').append(queryString);
        }
        return builder.toString();
    }

    private Optional<String> getBody(HttpServletRequest request) {
        if (request.getContentLengthLong() > 0) {
            try (Scanner scanner = new Scanner(request.getInputStream())) {
                StringBuilder builder = new StringBuilder();
                while (scanner.hasNextLine()) {
                    builder.append(scanner.nextLine());
                }
                return Optional.of(builder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

}
