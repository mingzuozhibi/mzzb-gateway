package mingzuozhibi.gateway.module;

import com.google.gson.JsonObject;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.List;

@Component
public class MessageHelper {

    @Resource(name = "redisTemplate")
    private ListOperations<String, String> listOps;

    public void pushModuleMsg(String moduleName, JsonObject data) {
        data.addProperty("acceptOn", Instant.now().toEpochMilli());
        listOps.leftPush(keyOfMsgs(moduleName), data.toString());
        listOps.trim(keyOfMsgs(moduleName), 0, 999);
    }

    public List<String> findModuleMsg(String moduleName, int page, int pageSize) {
        int start = (page - 1) * pageSize;
        int end = page * pageSize - 1;
        return listOps.range(keyOfMsgs(moduleName), start, end);
    }

    public Long countModuleMsg(String moduleName) {
        return listOps.size(keyOfMsgs(moduleName));
    }

    private String keyOfMsgs(String moduleName) {
        return moduleName + ".msgs";
    }

}
