package mingzuozhibi.gateway.utils.jsoup;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class JsoupResponse {

    private boolean success = false;
    private List<Exception> errors;
    private String errorMessage;
    private String content;

    public JsoupResponse() {
        errors = new LinkedList<>();
    }

    private JsoupResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void addError(Exception e) {
        errors.add(e);
    }

    public void setContent(String content) {
        this.success = true;
        this.content = content;
    }

    public boolean hasErrors() {
        return !success;
    }

    public String formatError() {
        if (errorMessage != null) {
            return errorMessage;
        }
        AtomicInteger count = new AtomicInteger(0);
        return "未能成功抓取：" + errors.stream()
                .map(e -> e.getClass().getCanonicalName() + ": " + e.getMessage())
                .distinct()
                .map(str -> String.format("(%d)[%s]", count.incrementAndGet(), str))
                .collect(Collectors.joining(" "));
    }

    public String getContent() {
        return content;
    }

    public static JsoupResponse of(String errorMessage) {
        Objects.requireNonNull(errorMessage);
        return new JsoupResponse(errorMessage);
    }

}
