package mingzuozhibi.gateway.message;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mingzuozhibi.common.BaseController;
import mingzuozhibi.gateway.utils.GsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessageController extends BaseController {

    @Autowired
    private MessageHelper messageHelper;

    private Gson gson = GsonUtils.getGson();

    @GetMapping("/gateway/messages/{moduleName}")
    public String findMessages(
        @PathVariable String moduleName,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "50") int pageSize) {

        List<String> moduleMsg = messageHelper.findModuleMsg(moduleName, page, pageSize);
        Long count = messageHelper.countModuleMsg(moduleName);

        JsonArray root = new JsonArray();
        moduleMsg.forEach(msg -> root.add(gson.fromJson(msg, JsonObject.class)));
        return objectResult(root, buildPage(page, pageSize, count));
    }

    public JsonElement buildPage(int currentPage, int pageSize, long totalElements) {
        JsonObject object = new JsonObject();
        object.addProperty("currentPage", currentPage);
        object.addProperty("pageSize", pageSize);
        object.addProperty("totalElements", totalElements);
        return object;
    }

}
