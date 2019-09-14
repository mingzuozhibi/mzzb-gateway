package mingzuozhibi.gateway.modules.discspider;

import mingzuozhibi.common.BaseController;
import mingzuozhibi.common.model.Result;
import mingzuozhibi.gateway.utils.JsoupHelper;
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
        Result<String> bodyResult = jsoupHelper.waitRequest(DISC_SPIDER, "/fetchDisc/" + asin, connection -> {
            connection.timeout(60 * 1000);
        });
        if (bodyResult.isUnfinished()) {
            return errorMessage(bodyResult.formatError());
        }
        return bodyResult.getContent();
    }

}
