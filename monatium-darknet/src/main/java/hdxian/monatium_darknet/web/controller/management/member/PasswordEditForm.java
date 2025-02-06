package hdxian.monatium_darknet.web.controller.management.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordEditForm {

    @NotBlank
    private String oldPassword;

    @NotBlank
    @Size(min = 8, max = 50)
    private String newPassword;
}
