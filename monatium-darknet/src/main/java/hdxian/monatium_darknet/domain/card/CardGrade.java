package hdxian.monatium_darknet.domain.card;

import lombok.Getter;

@Getter
public enum CardGrade {
    LEGENDARY(1),
    RARE(2),
    ADVANCED(3),
    NORMAL(4);

    private final int order;

    CardGrade(int order) {
        this.order = order;
    }

}
