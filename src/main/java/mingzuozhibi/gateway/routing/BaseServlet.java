package mingzuozhibi.gateway.routing;

import com.google.gson.JsonObject;
import mingzuozhibi.common.jms.JmsMessage;
import mingzuozhibi.common.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public abstract class BaseServlet implements Servlet {

    @Autowired
    protected JmsMessage message;

    public abstract void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        try {
            doService(request, response);
        } catch (Exception e) {
            responseError(response, "路由转发遇到错误: %s", Result.formatErrorCause(e));
        }
    }

    public void responseError(HttpServletResponse response, String format, Object... args) {
        responseText(response, errorMessage(String.format(format, args)));
    }

    public void responseError(HttpServletResponse response, String error) {
        responseText(response, errorMessage(error));
    }

    public void responseText(HttpServletResponse response, String content) {
        try {
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);
            response.flushBuffer();
        } catch (IOException e) {
            message.danger("responseText error: " + Result.formatErrorCause(e));
        }
    }

    private String errorMessage(String error) {
        Objects.requireNonNull(error);
        JsonObject root = new JsonObject();
        root.addProperty("success", false);
        root.addProperty("message", error);
        return root.toString();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }

}
