package hdxian.monatium_darknet.controller.management;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginForm {

    @NotEmpty
    private String loginId;

    @NotEmpty
    private String password;

    public LoginForm() {
    }

    public LoginForm(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }

}
