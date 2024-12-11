package hdxian.monatium_darknet.service.dto;

import hdxian.monatium_darknet.domain.notice.MemberStatus;
import lombok.Data;

@Data
public class MemberDto {

    private String loginId;
    private String password;
    private String nickName;
    private MemberStatus status;

}
