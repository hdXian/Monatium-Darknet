package hdxian.monatium_darknet.service.dto;

import hdxian.monatium_darknet.domain.notice.MemberRole;
import lombok.Data;

@Data
public class MemberDto {
    private String loginId;
    private MemberRole role;
    private String password;
    private String nickName;
}
