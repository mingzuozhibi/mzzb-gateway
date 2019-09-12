package mingzuozhibi.gateway;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import mingzuozhibi.common.BaseController;
import mingzuozhibi.gateway.module.ModuleMessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Optional;

@Slf4j
@RestController
public class DiscComingController extends BaseController {

    @Resource(name = "redisTemplate")
    private SetOperations<String, String> setOpts;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOpts;

    @Autowired
    private SpiderHelper spiderHelper;

    @Autowired
    private ModuleMessageHelper moduleMessageHelper;

    @JmsListener(destination = "disc.track")
    public void discTrack(String json) {
        JsonObject root = new Gson().fromJson(json, JsonObject.class);
        String name = root.get("name").getAsString();
        String asin = root.get("asin").getAsString();
        setOpts.add("disc.track", asin);
        log.info("JMS <- disc.track [name={}, asin={}]", name, asin);
    }

    @Transactional
    @GetMapping(value = "/gateway/discComing")
    public String findAll(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "20") int pageSize) {
        // 校验
        if (pageSize > 40) {
            return errorMessage("pageSize不能超过40");
        }

        String json = valueOpts.get(String.format("gateway.discComing/%d/%d", page, pageSize));
        if (json == null) {
            json = spiderHelper.waitRequest(buildUrl("/discShelfs?page=%d&pageSize=%d", page, pageSize));
        }

        JsonObject result = GsonUtils.getGson().fromJson(json, JsonObject.class);
        if (result.get("success").getAsBoolean()) {
            JsonArray discShelfs = result.get("data").getAsJsonArray();
            discShelfs.forEach(element -> {
                JsonObject discShelf = element.getAsJsonObject();
                discShelf.addProperty("tracked", isTracked(discShelf));
            });
        }

        return result.toString();
    }

    private Boolean isTracked(JsonObject discShelf) {
        String asin = discShelf.get("asin").getAsString();
        return setOpts.isMember("disc.track", asin);
    }

    private String buildUrl(String uri, Object... args) {
        Optional<String> moduleAddr = moduleMessageHelper.getModuleAddr("mzzb-disc-shelfs");
        if (moduleAddr.isPresent()) {
            return "http://" + moduleAddr.get() + String.format(uri, args);
        } else {
            throw new RuntimeException("服务暂时无法使用");
        }
    }

}
