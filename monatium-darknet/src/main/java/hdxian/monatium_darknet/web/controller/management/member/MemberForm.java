package hdxian.monatium_darknet.web.controller.management.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemberForm {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    @Size(min = 5, max = 20)
    private String loginId;

    @NotBlank
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$")
    @Size(min = 1, max = 20)
    private String nickname;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;
}
