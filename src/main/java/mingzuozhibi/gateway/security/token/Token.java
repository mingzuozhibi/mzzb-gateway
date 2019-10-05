package mingzuozhibi.gateway.security.token;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mingzuozhibi.common.BaseModel;
import mingzuozhibi.gateway.security.user.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Token extends BaseModel {

    @ManyToOne(optional = false)
    private User user;

    @Column(length = 36, nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false)
    private Instant expireOn;

    public Token(User user) {
        this.user = user;
        this.uuid = UUID.randomUUID().toString();
        this.expireOn = Instant.now().plusSeconds(14 * 86400);
    }

    public boolean tokenExpired() {
        return Instant.now().isAfter(expireOn);
    }

}
