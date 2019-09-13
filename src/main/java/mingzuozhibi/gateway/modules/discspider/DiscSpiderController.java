package mingzuozhibi.gateway.modules.discspider;

import mingzuozhibi.common.BaseController;
import mingzuozhibi.gateway.utils.jsoup.JsoupHelper;
import mingzuozhibi.gateway.utils.jsoup.JsoupResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static mingzuozhibi.gateway.modules.Module.DISC_SPIDER;

@RestController
public class DiscSpiderController extends BaseController {

    @Autowired
    private JsoupHelper jsoupHelper;

    @Transactional
    @GetMapping(value = "/fetchDisc/{asin}")
    public String fetchDisc(@PathVariable String asin) {
        JsoupResponse bodyResponse = jsoupHelper.waitRequest(DISC_SPIDER, "/fetchDisc/" + asin, connection -> {
            connection.timeout(90 * 1000);
        });
        if (bodyResponse.hasErrors()) {
            return errorMessage(bodyResponse.formatError());
        }
        return bodyResponse.getContent();
    }

}
