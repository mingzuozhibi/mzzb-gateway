package mingzuozhibi.gateway.connect;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import mingzuozhibi.common.jms.JmsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConnectListener {

    @Autowired
    private JmsMessage jmsMessage;
    @Autowired
    private ConnectHelper connectHelper;

    @JmsListener(destination = "module.connect")
    public void moduleConnect(String json) {
        JsonObject root = new Gson().fromJson(json, JsonObject.class);
        String name = root.get("name").getAsString();
        String addr = root.get("addr").getAsString();
        connectHelper.setModuleAddr(name, addr);
        jmsMessage.notify(String.format("JMS <- module.connect [name=%s, addr=%s]", name, addr));
    }

}
