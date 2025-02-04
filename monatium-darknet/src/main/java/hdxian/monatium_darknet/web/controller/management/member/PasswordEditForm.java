package hdxian.monatium_darknet.web.controller.management.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordEditForm {
    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;
}
