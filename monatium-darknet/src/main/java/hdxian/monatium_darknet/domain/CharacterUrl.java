package hdxian.monatium_darknet.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class CharacterUrl {

    private String portrait_url;
    private String profile_url;
    private String body_url;

    protected CharacterUrl() {
    }

    public CharacterUrl(String portrait_url, String profile_url, String body_url) {
        this.portrait_url = portrait_url;
        this.profile_url = profile_url;
        this.body_url = body_url;
    }

}
