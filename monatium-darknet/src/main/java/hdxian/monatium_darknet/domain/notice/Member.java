package hdxian.monatium_darknet.domain.notice;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "members")
@Getter @Setter // getter, setter는 추후 개선해야 함
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private String loginId;
    private String password;
    private String nickName;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notice> notices = new ArrayList<>();

    // 비즈니스 로직
    public void addNotice(Notice notice) {
        this.notices.add(notice);
        notice.setMember(this);
    }

    public void removeNotice(Notice notice) {
        this.notices.remove(notice);
        notice.setMember(null);
    }

    // for JPA spec (일반 비즈니스 로직에서 사용 x)
    protected Member() {
    }

    // 생성 메서드
    public static Member createMember(String loginId, MemberRole role, String password, String nickName) {
        Member member = new Member();
        member.setLoginId(loginId);
        member.setRole(role);
        member.setPassword(password);
        member.setNickName(nickName);
        member.setStatus(MemberStatus.INACTIVE); // 생성 시 기본 상태는 INACTIVE

        return member;
    }

}
