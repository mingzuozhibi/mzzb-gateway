package mingzuozhibi.gateway.module;

import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Component
public class ConnectHelper {

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;

    public void setModuleAddr(String moduleName, String moduleAddr) {
        valueOps.set(keyOfAddr(moduleName), moduleAddr);
    }

    public Optional<String> getModuleAddr(String moduleName) {
        return Optional.ofNullable(valueOps.get(keyOfAddr(moduleName)));
    }

    private String keyOfAddr(String moduleName) {
        return moduleName + ".addr";
    }

}
