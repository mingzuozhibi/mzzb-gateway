package mingzuozhibi.gateway.modules.discshelfs;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import mingzuozhibi.common.BaseController;
import mingzuozhibi.common.jms.JmsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class DiscShelfsListener extends BaseController {

    @Autowired
    private JmsMessage jmsMessage;

    @Resource(name = "redisTemplate")
    private SetOperations<String, String> setOps;

    @JmsListener(destination = "disc.track")
    public void discTrack(String json) {
        JsonObject root = new Gson().fromJson(json, JsonObject.class);
        setOps.add("disc.track", root.get("asin").getAsString());
        jmsMessage.sendMsgAndLogger("notify", "JMS <- disc.track: " + json);
    }

}
