package mingzuozhibi.gateway.module;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mingzuozhibi.common.BaseController;
import mingzuozhibi.gateway.GsonUtils;
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

    @GetMapping("/gateway/moduleMessages/{moduleName}")
    public String findModuleMessages(
            @PathVariable String moduleName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int pageSize) {

        List<String> moduleMsg = messageHelper.findModuleMsg(moduleName, page, pageSize);
        JsonArray root = new JsonArray();
        moduleMsg.forEach(msg -> root.add(gson.fromJson(msg, JsonObject.class)));
        return objectResult(root);
    }

}
