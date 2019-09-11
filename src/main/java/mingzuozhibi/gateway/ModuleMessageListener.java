package mingzuozhibi.gateway;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ModuleMessageListener {

    @Autowired
    private ModuleMessageHelper moduleMessageHelper;

    @JmsListener(destination = "module.connect")
    public void moduleConnect(String json) {
        JsonObject root = new Gson().fromJson(json, JsonObject.class);
        String name = root.get("name").getAsString();
        String addr = root.get("addr").getAsString();
        moduleMessageHelper.setModuleAddr(name, addr);
        log.info("JMS <- module.connect [name={}, addr={}]", name, addr);
    }

    @JmsListener(destination = "module.message")
    public void moduleMessage(String json) {
        JsonObject root = new Gson().fromJson(json, JsonObject.class);
        String name = root.get("name").getAsString();
        JsonObject data = root.get("data").getAsJsonObject();
        moduleMessageHelper.pushModuleMsg(name, data);
        log.info("JMS <- module.message [name={}, data={}]", name, data);
    }

}
