package hdxian.monatium_darknet.domain.notice;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter // getter, setter는 추후 개선해야 함
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String memberId;
    private String password;
    private String nickName;

    @OneToMany(mappedBy = "member")
    private List<Notice> notices = new ArrayList<>();

    // for JPA spec (일반 비즈니스 로직에서 사용 x)
    protected Member() {
    }

    // 생성 메서드
    public static Member createMember(String memberId, String password, String nickName) {
        Member member = new Member();
        member.setMemberId(memberId);
        member.setPassword(password);
        member.setNickName(nickName);

        return member;
    }

}
