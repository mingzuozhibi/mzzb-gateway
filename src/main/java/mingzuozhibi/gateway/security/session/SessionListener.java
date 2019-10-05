package mingzuozhibi.gateway.security.session;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicInteger;

@WebListener
public class SessionListener implements HttpSessionListener {

    private static AtomicInteger sessionCount = new AtomicInteger();

    public static int getSessionCount() {
        return sessionCount.get();
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        sessionCount.incrementAndGet();
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        sessionCount.decrementAndGet();
    }

}
