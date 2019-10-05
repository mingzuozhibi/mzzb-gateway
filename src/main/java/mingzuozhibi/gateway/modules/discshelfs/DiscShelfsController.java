package mingzuozhibi.gateway.modules.discshelfs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import mingzuozhibi.common.BaseController;
import mingzuozhibi.common.model.Content;
import mingzuozhibi.gateway.connect.ConnectJsoup;
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

    @Autowired
    private ConnectJsoup jsoup;

    @Resource(name = "redisTemplate")
    private SetOperations<String, String> setOps;

    @Transactional
    @GetMapping(value = "/api/gateway/discShelfs")
    public String findAll(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "20") int pageSize) {

        if (pageSize > 40) {
            return errorMessage("pageSize不能超过40");
        }

        String uri = String.format("/discShelfs?page=%d&pageSize=%d", page, pageSize);
        Content content = Content.parse(jsoup.get(DISC_SHELFS, uri));
        if (content.isSuccess()) {
            matchTracked(content.getArray());
        }
        return content.getRoot().toString();
    }

    private void matchTracked(JsonArray discShelfs) {
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
