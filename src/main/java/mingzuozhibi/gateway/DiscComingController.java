package mingzuozhibi.gateway;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import mingzuozhibi.common.BaseController;
import mingzuozhibi.gateway.module.ConnectHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Optional;

import static mingzuozhibi.gateway.JsoupUtils.waitRequest;

@Slf4j
@RestController
public class DiscComingController extends BaseController {

    public static final String MZZB_DISC_SHELFS = "mzzb-disc-shelfs";

    @Resource(name = "redisTemplate")
    private SetOperations<String, String> setOps;

    @Autowired
    private ConnectHelper connectHelper;

    @JmsListener(destination = "disc.track")
    public void discTrack(String json) {
        JsonObject root = new Gson().fromJson(json, JsonObject.class);
        String name = root.get("name").getAsString();
        String asin = root.get("asin").getAsString();
        setOps.add("disc.track", asin);
        log.info("JMS <- disc.track [name={}, asin={}]", name, asin);
    }

    @Transactional
    @GetMapping(value = "/gateway/discComing")
    public String findAll(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "20") int pageSize) {

        if (pageSize > 40) {
            return errorMessage("pageSize不能超过40");
        }

        String json = waitRequest(buildUrl("/discShelfs?page=%d&pageSize=%d", page, pageSize));

        JsonObject result = GsonUtils.getGson().fromJson(json, JsonObject.class);
        if (result.get("success").getAsBoolean()) {
            matchTracked(result);
        }

        return result.toString();
    }

    private String buildUrl(String uri, Object... args) {
        Optional<String> moduleAddr = connectHelper.getModuleAddr(MZZB_DISC_SHELFS);
        if (moduleAddr.isPresent()) {
            return "http://" + moduleAddr.get() + String.format(uri, args);
        } else {
            throw new RuntimeException("服务暂时无法使用");
        }
    }

    private void matchTracked(JsonObject result) {
        JsonArray discShelfs = result.get("data").getAsJsonArray();
        discShelfs.forEach(element -> {
            JsonObject discShelf = element.getAsJsonObject();
            discShelf.addProperty("tracked", isTracked(discShelf));
        });
    }

    private Boolean isTracked(JsonObject discShelf) {
        String asin = discShelf.get("asin").getAsString();
        return setOps.isMember("disc.track", asin);
    }

}
