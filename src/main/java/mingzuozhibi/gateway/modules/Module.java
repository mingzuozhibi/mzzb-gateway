package mingzuozhibi.gateway.modules;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Module {

    DISC_SHELFS("mzzb-disc-shelfs"),
    DISC_SPIDER("mzzb-disc-spider"),
    MZZB_SERVER("mzzb-server");

    private String moduleName;

}
