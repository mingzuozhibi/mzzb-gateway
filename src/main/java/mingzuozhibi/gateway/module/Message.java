package mingzuozhibi.gateway.module;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
public class Message {

    private String type;
    private String text;
    private Instant createOn;
    private Instant acceptOn;

}
