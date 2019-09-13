package mingzuozhibi.gateway.modules.discshelfs;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import mingzuozhibi.common.BaseController;
import mingzuozhibi.gateway.utils.GsonUtils;
import mingzuozhibi.gateway.utils.jsoup.JsoupHelper;
import mingzuozhibi.gateway.utils.jsoup.JsoupResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static mingzuozhibi.gateway.modules.Module.DISC_SHELFS;

@Slf4j
@RestController
public class DiscShelfsController extends BaseController {

    @Resource(name = "redisTemplate")
    private SetOperations<String, String> setOps;

    @Autowired
    private JsoupHelper jsoupHelper;

    private Gson gson = GsonUtils.getGson();

    @Transactional
    @GetMapping(value = "/gateway/discShelfs")
    public String findAll(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "20") int pageSize) {

        if (pageSize > 40) {
            return errorMessage("pageSize不能超过40");
        }

        String uri = String.format("/discShelfs?page=%d&pageSize=%d", page, pageSize);
        JsoupResponse response = jsoupHelper.waitRequest(DISC_SHELFS, uri);
        if (response.hasErrors()) {
            return errorMessage(response.formatError());
        }

        JsonObject result = gson.fromJson(response.getContent(), JsonObject.class);
        if (result.get("success").getAsBoolean()) {
            matchTracked(result);
        }
        return result.toString();
    }


    private void matchTracked(JsonObject result) {
        result.get("data").getAsJsonArray().forEach(element -> {
            JsonObject discShelf = element.getAsJsonObject();
            discShelf.addProperty("tracked", isTracked(discShelf));
        });
    }

    private Boolean isTracked(JsonObject discShelf) {
        String asin = discShelf.get("asin").getAsString();
        return setOps.isMember("disc.track", asin);
    }

}
