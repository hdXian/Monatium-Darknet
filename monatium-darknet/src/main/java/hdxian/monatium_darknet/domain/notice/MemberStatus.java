package hdxian.monatium_darknet.domain.notice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {
    ACTIVE("활성화"),
    INACTIVE("비활성화");

    private final String description;

}
