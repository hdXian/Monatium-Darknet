package hdxian.monatium_darknet.domain.notice;

// 최고 관리자, 그냥 관리자

import lombok.Getter;

/**
 ** 중요 ** Spring Security User의 Role로 활용됨.
  */
@Getter
public enum MemberRole {
    SUPER("최고 관리자"), NORMAL("그냥 관리자");

    private final String name;

    MemberRole(String name) {
        this.name = name;
    }

}
