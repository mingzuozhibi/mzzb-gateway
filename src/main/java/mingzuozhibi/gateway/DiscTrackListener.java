package mingzuozhibi.gateway;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class DiscTrackListener {

    @Resource(name = "redisTemplate")
    private SetOperations<String, String> setOpts;

    @JmsListener(destination = "disc.track")
    public void discTrack(String json) {
        JsonObject root = new Gson().fromJson(json, JsonObject.class);
        String name = root.get("name").getAsString();
        String asin = root.get("asin").getAsString();
        setOpts.add("disc.track", asin);
        log.info("JMS <- disc.track [name={}, asin={}]", name, asin);
    }

}
