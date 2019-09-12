package mingzuozhibi.gateway.module;

import com.google.gson.JsonObject;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class ModuleMessageHelper {

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;

    @Resource(name = "redisTemplate")
    private ListOperations<String, String> listOps;


    public void setModuleAddr(String moduleName, String moduleAddr) {
        valueOps.set(keyOfAddr(moduleName), moduleAddr);
    }

    public Optional<String> getModuleAddr(String moduleName) {
        return Optional.ofNullable(valueOps.get(keyOfAddr(moduleName)));
    }

    private String keyOfAddr(String moduleName) {
        return moduleName + ".addr";
    }

    public void pushModuleMsg(String moduleName, JsonObject data) {
        data.addProperty("acceptOn", Instant.now().toEpochMilli());
        listOps.leftPush(keyOfMsgs(moduleName), data.toString());
        listOps.trim(keyOfMsgs(moduleName), 0, 999);
    }

    public List<String> listModuleMsg(String moduleName) {
        return listOps.range(keyOfMsgs(moduleName), 0, -1);
    }

    private String keyOfMsgs(String moduleName) {
        return moduleName + ".msgs";
    }

}
